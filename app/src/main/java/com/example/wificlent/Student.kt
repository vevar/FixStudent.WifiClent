package com.example.wificlent

class Student(
        id: Int,
        firstName: String,
        surName: String,
        middleName: String
) : Person(id, firstName, surName, middleName) {
    var uidDevice: String? = null
    var group: Group? = null

}