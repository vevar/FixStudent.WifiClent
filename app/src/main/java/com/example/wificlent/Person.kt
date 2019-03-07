package com.example.wificlent

import java.io.Serializable

abstract class Person(
        val id: Int,
        val firstName: String,
        val surName: String,
        val middleName: String
)  : Serializable {
    var login: String? = null
    var password: String? = null
}