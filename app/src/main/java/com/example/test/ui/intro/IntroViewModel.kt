package com.example.test.ui.intro

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.example.LoginResponse
import com.example.test.api.AuthApi
import com.example.test.api.RetrofitInstance
import com.example.test.application.SharedManager
import com.example.test.model.User
import com.example.test.model.joinPost
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class IntroViewModel : ViewModel() {
    private val api = RetrofitInstance.getInstance().create(AuthApi::class.java)
    var signmessage: String = ""
    suspend fun login(name: String, password: String) {
        suspendCoroutine<Unit> {
            val json = JSONObject().apply {
                put("username", name)
                put("password", password)
            }.toString()

            api.getLogin(json).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    it.resume(Unit)
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    it.resume(Unit)
                }
            })
        }

        if (SharedManager.getBearerToken().isBlank()) {
            throw Exception("Failed to sign in")
        }

        val user = suspendCoroutine<User?> {
            api.getMyUser().enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    it.resume(response.body())
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    it.resumeWithException(t)
                }
            })
        } ?: throw Exception("Failed to sign in")

        SharedManager.saveCurrentUser(user)
    }

    suspend fun join(name: String, password: String, nickname: String) = suspendCoroutine {
        val json = JSONObject().apply {
            put("username", name)
            put("password", password)
            put("nickname", nickname)
        }.toString()

        api.getJoin(json).enqueue(object : Callback<joinPost> {
            override fun onResponse(
                call: Call<joinPost>,
                response: Response<joinPost>
            ) {
                it.resume(response.body())
                if (response.isSuccessful) {
                    val body = response.body()
                    response.body()?.let { dto ->
                        Log.d("updatesdsd", dto.msg)
                        signmessage = dto.msg
                        if (signmessage.isNullOrBlank()) {
                            throw Exception("using nickname")
                        }
                    }

                }
            }

            override fun onFailure(call: Call<joinPost>, t: Throwable) {
                it.resumeWithException(t)
            }
        })

    }
    suspend fun nickname( nickname: String) = suspendCoroutine {
        val json = JSONObject().apply {
            put("nickname", nickname)
        }.toString()

        api.updateNickname(json).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                it.resume(response.body())
                if (response.isSuccessful) {
                    val body = response.body()

                    Log.d("updatesdsd2", body.toString())
                    response.body()?.let { dto ->
                        Log.d("updatesdsd", dto)
                        signmessage = dto
                    }

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                it.resumeWithException(t)
            }
        })
    }
}