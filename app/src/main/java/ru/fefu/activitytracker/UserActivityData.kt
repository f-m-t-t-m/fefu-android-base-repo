package ru.fefu.activitytracker

import java.time.LocalDateTime

data class UserActivityData(
    val distance: String,
    val activityType: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val user: String,
)