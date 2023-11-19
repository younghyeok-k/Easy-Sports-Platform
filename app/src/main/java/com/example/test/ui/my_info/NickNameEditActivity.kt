package com.example.test.ui.my_info

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.test.api.AuthApi
import com.example.test.api.RetrofitInstance
import com.example.test.application.SharedManager
import com.example.test.databinding.ActivityNickNameEditBinding
import com.example.test.model.User
import com.example.test.model.UserEdit
import com.example.test.ui.intro.IntroActivity
import com.example.test.ui.intro.IntroViewModel
import com.example.test.ui.main.MainActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NickNameEditActivity : AppCompatActivity() {
    private val binding by lazy { ActivityNickNameEditBinding.inflate(layoutInflater) }
    private lateinit var viewModel: IntroViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[IntroViewModel::class.java]
        initUi()

    }

    private val userEdit: UserEdit? = null
    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        nickname.editText?.addTextChangedListener {
            nicknameButton.isEnabled = nickname.editText!!.text.toString().isNotBlank()

        }

        nicknameButton.setOnClickListener {
            val nickname = nickname.editText!!.text.toString()

            lifecycleScope.launch {
                try {
                    viewModel.nickname(nickname)
                    info()
                    val intent = Intent(this@NickNameEditActivity, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    startActivity(intent)
                    finish()

                } catch (_: Exception) {
                    Toast.makeText(
                        this@NickNameEditActivity,
                        "이미 사용중인 닉네임입니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }

    }

    //    private fun updateNickname(useredit: UserEdit) {
//        RetrofitInstance.retrofit.create(AuthApi::class.java)
//            .updateNickname(useredit)
//            .enqueue(object : Callback<String> {
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    if (response.isSuccessful) {
//                        val body = response.body()
//                        response.body()?.let { dto ->
//                            Log.d("updatesdsd", dto)
//                            Toast.makeText(
//                                this@NickNameEditActivity,
//                                "변경이 완료되었습니다.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Toast.makeText(
//                        this@NickNameEditActivity,
//                        "이미 사용하고있는 닉네임 입니다.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })
//
//    }
//}
    private fun info() {
        RetrofitInstance.retrofit.create(AuthApi::class.java)
            .getMyUser()
            .enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        response.body()?.let { dto ->
                            SharedManager.saveCurrentUser(dto)
                        }

                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {

                }
            })

    }
}
