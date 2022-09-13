package com.example.encryptionapp

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings (
    val username: String? = null,
    val password: String? = null
)