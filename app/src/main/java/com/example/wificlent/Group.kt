package com.example.wificlent

import java.io.Serializable


data class Group(
        val id: Int,
        val name: String
) : Serializable {
}