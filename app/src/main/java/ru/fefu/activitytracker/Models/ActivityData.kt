package ru.fefu.activitytracker.Models

import java.time.LocalDateTime

data class ActivityData(
    val distance: String,
    val activityType: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
)