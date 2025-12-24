package com.example.cinesuggest.data.mapper

import com.example.cinesuggest.data.remote.dto.UserResponseDto
import com.example.cinesuggest.domain.model.User

fun UserResponseDto.toDomain() : User {
    return User(
        id = this.id,
        username = this.username
    )
}