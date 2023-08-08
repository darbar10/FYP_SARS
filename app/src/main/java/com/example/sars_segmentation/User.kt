package com.example.sars_segmentation

data class User(
    val fullName: String,
    val email: String,
    val education: Int,
    val marital_status: Int,
    val income: Int? = null,
    val kidHome: Int? = null,
    val teenHome: Int? = null,
    val recency: Int? = null,
    val total_spent: Int? = null,
    val password: String)