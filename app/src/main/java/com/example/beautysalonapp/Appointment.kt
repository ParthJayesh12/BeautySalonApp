package com.example.beautysalonapp

data class Appointment(
    var appointmentId: String = "",
    val date: String = "",
    val time: String = "",
    val staff: String = "",
    val status: String = "",
    val service: String = "",
    val description: String = "",
    val userId: String = "",
    var createdAt: Long = 0L
)
