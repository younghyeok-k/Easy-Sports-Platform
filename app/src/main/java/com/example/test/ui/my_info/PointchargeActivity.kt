package com.example.test.ui.my_info


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.test.R
import com.example.test.api.AuthApi
import com.example.test.api.RetrofitInstance
import com.example.test.application.SharedManager
import com.example.test.databinding.ActivityMyReservationsBinding
import com.example.test.databinding.ActivityPointchargeBinding
import com.example.test.model.User
import com.example.test.model.post.Post
import com.example.test.ui.main.MainActivity
import com.example.test.ui.post.PostEditorActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PointchargeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPointchargeBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() = with(binding) {

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        pointCharge.editText?.addTextChangedListener {
            pointChargeButton.isEnabled = pointCharge.editText?.text.toString().isNotBlank() == true
        }

        pointChargeButton.setOnClickListener {
            val inputText = pointCharge.editText?.text.toString()
            if (inputText.isNotBlank()) {
                val point = inputText.toInt()
                updatePoint(point)
                info()

                val intent = Intent(this@PointchargeActivity, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
                finish()
            }
        }

    }

    private fun updatePoint(point: Int) {
        RetrofitInstance.retrofit.create(AuthApi::class.java)
            .updatePoint(point)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("MainActivity", "success");
                        response.body()?.let { dto ->
                            Log.d("updatesdsd", dto)
                            Toast.makeText(
                                this@PointchargeActivity,
                                "${point} 충전이 완료되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("updatesdsd", "실패")
                }
            })

    }
}

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
                    Log.d("MainActivity", "success");
                    response.body()?.let { dto ->
                        SharedManager.saveCurrentUser(dto)
                    }

                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {

            }
        })

}
