package com.example.valentine_garage.ui.testModels

data class CompletedJob(
    val vehicle:  String,
    val mechanic: String,
    val work:     String,
    val date:     String,
    val invoice:  String,
    val amount:   Double,
    val filter:   String   // "Week" | "Month" | "All"
)
