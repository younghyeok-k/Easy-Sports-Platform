package com.example.test.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.example.example.Content
import com.example.test.api.PostApi
import com.example.test.api.ReservationApi
import com.example.test.api.RetrofitInstance
import com.example.test.databinding.DialogMyreservationBinding
import com.example.test.databinding.DialogReservationBinding
import com.example.test.databinding.ItemMyReservationBinding
import com.example.test.model.post.PostsResponse
import com.example.test.model.reservation.RContent
import com.example.test.model.reservation.ResponrRservationDetail
import com.example.test.ui.my_info.MyReservationsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class MyReservationDialog : DialogFragment() {
    companion object {
        private const val ARG_CONTENT = "ARG_CONTENT"
        const val TAG = "MyReservationDialog"

        fun show(fragmentManager: FragmentManager, content: RContent) {
            val dialog = MyReservationDialog()
            dialog.arguments = bundleOf(ARG_CONTENT to content)
            dialog.show(fragmentManager, TAG)
        }
    }


    private var content: RContent? = null
    private var _binding: DialogMyreservationBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments?.getParcelable(ReservationDialog.ARG_CONTENT)

        if (savedInstanceState != null) {
            content = savedInstanceState.getParcelable(ReservationDialog.ARG_CONTENT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ReservationDialog.ARG_CONTENT, content)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMyreservationBinding.inflate(inflater, container, false)
        binding.root[0].updateLayoutParams<FrameLayout.LayoutParams> {
            width =
                (inflater.context.resources.displayMetrics.widthPixels - inflater.context.resources.displayMetrics.density * 32).toInt()
        }
        mygetrevservation(content!!)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {
            headerCloseButton.setOnClickListener { dismiss() }

        }

    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun mygetrevservation(reservation: RContent) {
        RetrofitInstance.retrofit.create(ReservationApi::class.java)
            .getMyReservationsDetail(reservation.centerId!!, reservation.reservationId!!)
            .enqueue(object : Callback<ResponrRservationDetail> {
                override fun onResponse(
                    call: Call<ResponrRservationDetail>,
                    response: Response<ResponrRservationDetail>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        response.body()?.let { dto ->
                            with(binding) {
                                Glide.with(imageC)
                                    .load(dto.imgeUrl)
                                    .into(imageC)
                                nameTextView.text = "시설명 : ${dto?.name}"
                                addressTextView.text = "날짜 : ${dto.reservingDate}"
                                getheadcount.text = "인원수 : ${dto.headCount}"
                                operatingTimeTextView.text = "예약시간: ${dto.reservingTime.joinToString(", ")}"
                                datetime.text= "상태 : ${dto?.status}"
                                priceTextView.text =
                                    String.format(Locale.US, "가격 : %,d", dto?.price ?: 0)
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<ResponrRservationDetail>, t: Throwable) {

                }
            })

    }
}