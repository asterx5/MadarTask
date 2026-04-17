# Madar DB — MadarSoft Database Task

A local user management app built for MadarSoft as a technical task. Users can be added, deleted, sorted, and browsed through a paginated list backed by a local Room database.

---

## Honest Time Breakdown

The core CRUD functionality took roughly **30 minutes**. The remaining **2–3 hours** went into things that are easy to skip but matter in a real project: catching edge cases, handling the empty-state flash before the first page loads, getting the `@RawQuery` + Paging 3 invalidation right, writing instrumented tests, and setting up theming properly.

Themes and test cases were not required by the spec. I added them because this is how I would approach any production task, not as a demonstration of volume — just standard practice.

---

## Architecture

The app uses **Clean Architecture** with three layers: Domain, Data, and Presentation.

Honestly, for a project of this size **Repository → ViewModel would have been sufficient**. A direct DAO call from the ViewModel would have worked fine. I used Clean Architecture to show how I would structure a real, scalable project — not because the complexity demanded it.

The main benefit it provided here: the `UserRepository` interface allowed the instrumented tests to swap in an in-memory Room database cleanly, with no changes to the layers above it.

### ViewModel Design

A single `UserViewModel` covers both screens. I think of a ViewModel as a coordinator — as long as the responsibilities are cohesive and operate on the same data, keeping them together is simpler than splitting artificially. The ViewModel itself is thin: it holds sort state and delegates everything else to use cases.

That said, every project is different. A larger feature set might justify splitting or adopting a stricter pattern.

### MVI vs MVVM

This project uses MVVM. For more complex screens I would consider MVI — it enforces a single immutable state object and explicit user intents, which makes state transitions easier to trace and test. The key point is that switching between them is largely a **presentation-layer decision**: both patterns still delegate business logic to use cases, so moving from one to the other does not require touching the Domain or Data layers. It is more a style preference than an architectural commitment. For this app, MVVM with `StateFlow` is sufficient and less boilerplate.

---

## Technology Choices

### Jetpack Compose
Standard choice in 2026. Declarative UI is now the default for any new Android work.

### Koin
I currently work on KMP projects and have settled on Koin as my DI framework of choice. Hilt is Android-only and its annotation processing adds friction that Koin avoids with plain Kotlin DSL. Since Koin works across KMP targets, it's the more future-proof choice for any team that might expand beyond Android.

### Room
For a pure Android app, Room is the natural choice — first-class Kotlin coroutine and Flow support, type safety, and tight Paging 3 integration. If this were a KMP project I would use **SQLDelight** instead, which compiles SQL at build time and generates multiplatform-compatible code.

### Paging 3
I hadn't had a practical reason to use Paging 3 in my own apps before this task. The 1,000-user seed data provided a real use case: the entire dataset cannot be sorted in memory after fetching, so pagination had to happen at the SQL layer via `@RawQuery`. This led to one interesting constraint — the typical `Result` wrapper I use in use cases (a custom sealed class with `Loading`, `Success`, `Error` states) would have conflicted with Paging 3's own `LoadState` mechanism, so I kept the use cases simple and let Paging handle its own state.

### Navigation 3
I usually use **Voyager** in KMP projects. I chose Navigation 3 here to experiment with it on a low-stakes project. It exposes the back stack as a plain `MutableList`, which makes navigation logic transparent. Any navigation library would have been fine for something this simple — the main decision was using a **sealed class** for type-safe routes instead of string paths, which eliminates a whole class of runtime errors.

---

## Written With

This project was written with **Claude Code** and verified manually — reading the generated code, testing on a device, fixing failures, and iterating, the same way I would work on any production feature.