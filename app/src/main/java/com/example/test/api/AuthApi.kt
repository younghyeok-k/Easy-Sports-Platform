package com.example.test.api


import com.example.example.LoginResponse
import com.example.test.model.User
import com.example.test.model.UserEdit
import com.example.test.model.joinPost
import retrofit2.Call
import retrofit2.http.*

interface AuthApi {
    @Headers("Content-Type: application/json")
    @POST("login")
    fun getLogin(
        @Body user: String,
//   @Header("authorization") accessToken:String
    ): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("join")
    fun getJoin(
        @Body user: String
    ): Call<joinPost>

    @GET("user/success")
    fun getMyUser(): Call<User>

    @Headers("Content-Type: application/json")
    @PATCH("user/point")
    fun updatePoint(
        @Query("chargePoint") chargePoint: Int
    ): Call<String>

    @Headers("Content-Type: application/json")
    @PATCH("user/update")
    fun updateNickname(
        @Body user: String
    ): Call<String>
 // 여기서 다시 해야함 바디값 먼말하는줄 모르겠음

    @Headers("Content-Type: application/json")
    @PATCH("user/update")
    fun updateEmail(
        @Body user: String
    ): Call<String>
}
//data class LoginRequest(
//    var username : String,
//    var password : String
//)