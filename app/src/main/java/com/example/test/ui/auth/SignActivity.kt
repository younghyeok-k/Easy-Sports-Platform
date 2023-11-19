package com.example.test.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.test.R
import com.example.test.databinding.ActivityPointchargeBinding
import com.example.test.databinding.ActivitySignBinding
import com.example.test.ui.intro.IntroActivity
import com.example.test.ui.intro.IntroViewModel
import kotlinx.coroutines.launch
import kotlin.math.sign

class SignActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySignBinding.inflate(layoutInflater) }
    private lateinit var viewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[IntroViewModel::class.java]


        with(binding) {
            toolbar.setNavigationOnClickListener {
                val intent = Intent(this@SignActivity, IntroActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
                finish()
            }
            pwcheckbutton.setOnClickListener {
                if (binding.signPW.text.toString()==binding.signPW2.text.toString()){
                    Toast.makeText(
                        this@SignActivity,
                        "동일합니다",
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    Toast.makeText(
                        this@SignActivity,
                        "비밀번호 다릅니다",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            signupbutton.setOnClickListener {
                val name = binding.signID.text.toString().trim()
                val password = binding.signPW.text.toString().trim()
                val nickname = binding.nickname.text.toString().trim()
                lifecycleScope.launch {
                    try {
                        viewModel.join(name, password, nickname)
                        Toast.makeText(
                            this@SignActivity,
                            "회원가입이 완료되었습니다",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@SignActivity, IntroActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        startActivity(intent)
                        finish()
                    } catch (_: Exception) {
                        Toast.makeText(
                            this@SignActivity,
                            viewModel.signmessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }


                }

            }
        }
    }

}