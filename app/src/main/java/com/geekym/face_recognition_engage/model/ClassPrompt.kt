package com.geekym.face_recognition_engage.model

import com.geekym.face_recognition_engage.Users

data class ClassPrompt(
    val classID: String,
    val userID: String,
    val className: String,
    val userName: String,
    val timeStamp: String,
    val latLong: LatLong,
    val expectedStudents: Int,
    val studentsList: List<Users>
)

data class LatLong(
    val lat: Long,
    val long: Long
)
