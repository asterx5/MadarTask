# App Architecture — Madar DB

## Overview

Madar DB is a local user management app built with **Clean Architecture** layered into three core zones: **Domain**, **Data**, and **Presentation**. Each zone has a single responsibility and depends only inward — UI knows nothing about Room, and Room knows nothing about Compose.

```
Presentation  →  Domain  ←  Data
   (UI/VM)     (models,    (Room, Paging,
                use cases,  DataSource,
                repo iface) RepoImpl)
```

Koin wires all layers together at startup. The theme layer sits alongside Presentation and is purely visual.

---

## Why Clean Architecture

The app manages a large local dataset (tested at 1,000+ records) with dynamic sorting and paginated loading. Clean Architecture was chosen because:

- **The Domain layer owns the rules.** Sorting, validation (age 1–100, required fields), and the User model are defined once and never duplicated across layers.
- **The Data layer can be swapped.** Because `UserRepository` and `UserLocalDataSource` are interfaces defined in the Domain layer, the concrete Room implementation can be replaced — for tests this is already done with an in-memory database.
- **The Presentation layer is thin.** Screens only call ViewModel methods and render state. No SQL, no mapping, no coroutine management leaks into Compose.

---

## Layers

### 1. Domain

**Location:** `domain/model/`, `domain/repository/`, `domain/usecase/`

#### Models

| Class | Type | Why |
|---|---|---|
| `User` | `data class` | Immutable value object. Kotlin `data class` gives structural equality and `copy()` for free — no need for manual `equals`/`hashCode`. |
| `Gender` | `enum` | Type-safe, exhaustive. Stored as a string in Room via `GenderConverter` so the database is human-readable. |
| `SortOrder` | `enum` with `label: String` | Each entry carries its own display label, keeping UI code clean — the dropdown just iterates `SortOrder.entries` and reads `.label` directly. |

#### Repository Interface — `UserRepository`

Defined in the Domain layer so that use cases and ViewModels depend on an abstraction, not on Room. This is the classic **Dependency Inversion Principle**: high-level policy (use cases) does not depend on low-level detail (SQLite).

`getUsers` returns `Flow<PagingData<User>>` rather than a plain list because sorting must happen at the SQL level — with 1,000+ rows, in-memory re-ordering after fetching is not viable.

#### Use Cases

Each use case is a single `class` with `operator fun invoke(...)` so it can be called like a function:

```kotlin
addUserUseCase(user)     // not addUserUseCase.execute(user)
getUsersUseCase(order)   // reads naturally
```

**One class per operation** was chosen over a single "UserService" class because:
- Each use case can be injected, tested, and replaced independently.
- The ViewModel constructor explicitly declares which operations it needs, making dependencies visible.
- Koin `factory { }` means each use case is a lightweight, short-lived object — no shared mutable state.

---

### 2. Data

**Location:** `data/local/`

#### UserEntity + GenderConverter

`UserEntity` is the Room representation of a user. It is deliberately separate from the `User` domain model. Mapping happens in the repository via extension functions (`toDomain()`, `toEntity()`), keeping Room annotations out of the domain.

`GenderConverter` stores `Gender` as a `String`. This was chosen over storing the ordinal (`Int`) because ordinals break if the enum order ever changes.

#### UserDao — `@RawQuery` instead of multiple `@Query`

Dynamic sorting cannot be achieved with a fixed `@Query` because the `ORDER BY` clause changes at runtime. The two realistic options were:

| Option | Tradeoff |
|---|---|
| 8 separate `@Query` methods (one per `SortOrder`) | Type-safe but verbose; adding a sort option means adding a DAO method |
| `@RawQuery` with `SupportSQLiteQuery` | One method, SQL built in a `companion object`; `observedEntities = [UserEntity::class]` preserves Room's invalidation tracking |

`@RawQuery` was chosen for maintainability. The `buildSortQuery` companion function is the single place where sort-to-SQL translation lives, tested directly via `UserDaoPagingTest`.

#### UserLocalDataSource (interface + impl)

An extra abstraction layer between the DAO and the Repository. This exists so the Repository depends on an interface, not directly on a `@Dao`-annotated type. In tests, `UserLocalDataSourceImpl` is already bypassed — tests construct a real DAO from an in-memory `AppDatabase` and use it directly.

#### UserRepositoryImpl — Paging 3 `Pager`

The Repository creates a `Pager` per sort order:

```kotlin
Pager(PagingConfig(pageSize = 20, enablePlaceholders = false)) {
    localDataSource.getUsers(sortOrder)
}.flow.map { pagingData -> pagingData.map { it.toDomain() } }
```

**Why pageSize = 20:** Enough rows to fill any screen without over-fetching. Room's PagingSource automatically invalidates when the underlying table changes — inserts and deletes refresh the list without any manual work.

**Why mapping happens here:** The Repository is the boundary between data and domain. `UserEntity` never crosses this boundary upward; `User` never crosses it downward.

---

### 3. Presentation

**Location:** `presentation/`

#### UserViewModel

A single ViewModel covers both screens. This was a deliberate choice over two separate ViewModels:

- Both screens operate on the same `User` dataset.
- Sharing one ViewModel scoped to the `NavDisplay` means the paginated list survives navigation back from AddUser without reloading.
- Koin's `viewModel { }` DSL scopes it to the `ViewModelStoreOwner` provided by Navigation 3's `rememberViewModelStoreNavEntryDecorator`.

**`flatMapLatest` + `cachedIn` pattern:**

```kotlin
val users: Flow<PagingData<User>> = sortOrder
    .flatMapLatest { order -> getUsersUseCase(order) }
    .cachedIn(viewModelScope)
```

- `flatMapLatest` cancels the previous paging flow when the sort order changes and starts a fresh one — this is the only correct way to swap `PagingSource` at runtime.
- `cachedIn` is placed **outside** the lambda. Placing it inside would create a separate cache per sort invocation, causing stale data on back-navigation.

#### Navigation — Navigation 3 + Sealed Class `Screen`

`Screen` is a `sealed class` with `data object` entries:

```kotlin
sealed class Screen {
    data object DisplayUsers : Screen()
    data object AddUser : Screen()
}
```

**Why sealed class over string routes:** Type-safe. Adding a screen requires adding a `data object` — the compiler enforces exhaustiveness in `when` expressions. No stringly-typed route strings to mistype.

**Why Navigation 3 over NavController:** Navigation 3 exposes the back stack as a plain `MutableList<Any>`, making navigation logic transparent — push to add a screen, `removeLastOrNull()` to go back. No `NavController` wrapper needed.

#### Screens — Jetpack Compose

Screens are stateless composables that receive a `ViewModel` and callbacks. State lives in the ViewModel (sort order, paged items) or in local `remember` (dialog visibility, form fields). This split follows the **state hoisting** principle: ephemeral UI state stays local, durable app state lives in the ViewModel.

**Empty state detection:**

```kotlin
val loadingComplete = users.loadState.refresh is LoadState.NotLoading
val hasItems = users.itemCount > 0
val isEmpty = loadingComplete && !hasItems
```

`hasItems` drives the FAB and toolbar icons rather than `!isEmpty`, so they never flash visible during the initial page load before data arrives.

---

### 4. Dependency Injection — Koin

**Location:** `di/AppModule.kt`, `MadarTaskApplication.kt`

Koin was chosen over Hilt because:
- No annotation processing required — all wiring is plain Kotlin DSL.
- Compatible with the KSP-only build setup (AGP 9+ dropped the `kotlin.android` plugin, and Hilt still requires it at time of writing).
- A single `appModule` is sufficient for this app's scope — no need for multi-module DI graphs.

**Scoping decisions:**

| Binding | Scope | Reason |
|---|---|---|
| `AppDatabase`, `UserDao` | `single` | Database connection is expensive to create and must be shared. |
| `UserLocalDataSource`, `UserRepository` | `single` | Stateless wrappers; one instance is enough. |
| Use cases | `factory` | Lightweight objects, no shared state. New instance per injection site costs nothing. |
| `UserViewModel` | `viewModel { }` | Scoped to the `ViewModelStoreOwner` — survives config changes, cleaned up when the nav entry is removed. |

---

### 5. Theme

**Location:** `ui/theme/`

| File | Responsibility |
|---|---|
| `Color.kt` | MadarSoft brand tokens: `MadarOrange (#ED7326)` as primary, `MadarGreen (#8BB04F)` as secondary, `MadarDark (#353C3E)` as base. Full light/dark sets. |
| `Type.kt` | Nunito from Google Fonts via `ui-text-google-fonts`. Four weights (Normal, Medium, SemiBold, Bold), seven typescale roles. |
| `Theme.kt` | `MadarTaskTheme` composable applies colour scheme, typography, and shapes. Dynamic colour is disabled — brand identity takes priority over wallpaper-based colour. Shapes use mild rounding (medium = 16 dp) consistent with Material 3 expressive guidelines. |

---

## Testing Strategy

**Location:** `app/src/androidTest/`

`UserDaoPagingTest` tests at the **DAO layer** using an in-memory Room database. This level was chosen because:

- The `@RawQuery` sort logic is pure SQL — the only meaningful way to verify it is to actually run it.
- `TestPager` from `paging-testing` drives the `PagingSource` imperatively: call `refresh()`, call `append()`, inspect `page.data`. No Compose, no ViewModel, no coroutine complexity.
- `PagingConfig(pageSize = 20, initialLoadSize = 20)` — `initialLoadSize` must match `pageSize` in tests; the default `3 × pageSize` initial load would otherwise make the first-page size assertion incorrect.

Eight tests cover: page size enforcement, append behaviour, remainder page, empty database, all four sort directions, and pager independence across sort changes.

---

## Dependency Flow Summary

```
MadarTaskApplication
    └── Koin (appModule)
            ├── AppDatabase → UserDao
            ├── UserDao → UserLocalDataSourceImpl
            ├── UserLocalDataSourceImpl → UserRepositoryImpl
            ├── UserRepositoryImpl → [AddUserUseCase, DeleteUserUseCase,
            │                         ClearAllUsersUseCase, GetUsersUseCase]
            └── All UseCases → UserViewModel
                                    ├── UsersScreen (collectAsLazyPagingItems)
                                    └── AddUserScreen (form callbacks)
```

Dependency arrows always point inward — Presentation → Domain ← Data. No layer ever imports from the layer above it.
