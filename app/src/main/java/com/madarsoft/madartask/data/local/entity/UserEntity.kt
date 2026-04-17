package com.madarsoft.madartask.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.madarsoft.madartask.domain.model.Gender
import com.madarsoft.madartask.domain.model.User

@Entity(tableName = "users")
@TypeConverters(GenderConverter::class)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int,
    val jobTitle: String,
    val gender: Gender
)

class GenderConverter {
    @TypeConverter
    fun fromGender(gender: Gender): String = gender.name

    @TypeConverter
    fun toGender(value: String): Gender = Gender.valueOf(value)
}

fun UserEntity.toDomain() = User(id = id, name = name, age = age, jobTitle = jobTitle, gender = gender)
fun User.toEntity() = UserEntity(id = id, name = name, age = age, jobTitle = jobTitle, gender = gender)
