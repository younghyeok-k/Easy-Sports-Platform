package com.example.test.model.reservation

import com.example.test.model.User
import com.google.gson.annotations.SerializedName


data class ResponrRservationDetail (

    @SerializedName("reservationId" ) var reservationId : Int?              = null,
    @SerializedName("centerId"      ) var centerId      : Int?              = null,
    @SerializedName("name"          ) var name          : String?           = null,
    @SerializedName("status"        ) var status        : String?           = null,
    @SerializedName("user"          ) var user          : User?             = User(),
    @SerializedName("reservingDate" ) var reservingDate : String?           = null,
    @SerializedName("reservingTime" ) var reservingTime : ArrayList<String> = arrayListOf(),
    @SerializedName("headCount"     ) var headCount     : Int?              = null,
    @SerializedName("price"         ) var price         : Int?              = null,
    @SerializedName("imgUrl"       ) var imgeUrl       : String?           = null

)