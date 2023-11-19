package com.example.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserEdit(

    val username: String = "",
    val password: String? = null,
    val email: String? = null,
    var nickname: String? = null

) : Parcelable
