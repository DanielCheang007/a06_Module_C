package com.example.a04_module_c

import java.io.Serializable

data class User(
    val username: String,
    val role: String,
    val thumbnail: String
): Serializable { // 加上 Serializable 之後，可以整個 instance 在 activities 或者 fragments 之間傳遞
}