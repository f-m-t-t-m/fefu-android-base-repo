package ru.fefu.activitytracker.Retrofit.response

data class UserModel (
    val id: Long,
    val name: String,
    val login: String,
    val gender: GenderModel,
)