package com.example.test.ui.main.children

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.example.CenterResponse
import com.example.test.api.AuthApi
import com.example.test.api.CenterApi
import com.example.test.api.RetrofitInstance
import com.example.test.application.SharedManager
import com.example.test.databinding.FragmentMyInfoBinding
import com.example.test.model.User
import com.example.test.ui.intro.IntroActivity
import com.example.test.ui.my_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MyInfoFragment : Fragment() {
    private var _binding: FragmentMyInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyInfoBinding.inflate(inflater, container, false)
        info()
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        info()

        with(binding) {
//            nameTextView.text = SharedManager.getInstance().getCurrentUser()?.username
//            point.text= SharedManager.getInstance().getCurrentUser()?.point.toString()

            logOut.setOnClickListener {
                SharedManager.getInstance().run {
                    saveBearerToken(null)
                    saveCurrentUser(null)
                }

                startActivity(Intent(requireContext(), IntroActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
                requireActivity().finish()
            }

            reservationHistoryButton.setOnClickListener {
                startActivity(Intent(requireContext(), MyReservationsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })

            }
            pointchargeText.setOnClickListener {
                startActivity(Intent(requireContext(), PointchargeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
                requireActivity().finish()
            }
            nicknameEditButton.setOnClickListener {
                startActivity(Intent(requireContext(), NickNameEditActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
                requireActivity().finish()
            }
            mypostsButton.setOnClickListener {
                startActivity(Intent(requireContext(), MypostActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
            }
            mycommnetpostsButton.setOnClickListener {
                startActivity(Intent(requireContext(), MyCommentpostsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
            }
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
                            with(binding) {
                                nameTextView.text=dto.nickname
                                point.text=dto.point.toString()
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {

                }
            })

    }

}