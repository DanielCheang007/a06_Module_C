package com.example.a04_module_c

import java.io.Serializable
import java.util.*

data class Tour(
    val activityId: Int,
    val activityName: String,
    val activityDate: Date,
    val activityType: String,
    val activityDescription: String,
    val maxParticipant: Int,
    val joinedParticipant: Int,
    val presentNo: Int,
    val absentNo: Int,
    val isActive: String // 數據庫使用 varchar(1) 存儲 "1" or "0"， 實際比賽時要看題目中介紹 API 時是否有帶引號，有引號就是字串
): Serializable { // 加上 Serializable 之後，可以整個 instance 在 activities 或者 fragments 之間傳遞

}