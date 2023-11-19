package com.example.test.dialog

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.example.Content
import com.example.test.api.ReservationApi
import com.example.test.api.RetrofitInstance
import com.example.test.model.reservation.CenterReservationStatusResponse
import com.example.test.model.reservation.RequestReservation
import com.example.test.model.reservation.ResponseReservation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.suspendCoroutine

class ReservationViewModel : ViewModel() {
    private val apiService = RetrofitInstance.retrofit.create(ReservationApi::class.java)

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> get() = _selectedDate

    private var _reservationStatus: Flow<CenterReservationStatusResponse>? = null
    val reservationStatus get() = _reservationStatus!!

    fun fetch(content: Content) {
        if (_reservationStatus != null) return

        _reservationStatus = _selectedDate.flatMapLatest {
            return@flatMapLatest flow {
                while (true) {
                    try {
                        val status = getReservationStatus(content, it)
                        if (status != null) {
                            emit(status)
                        }
                    } catch (_: Exception) {
                    }

                    delay(1000)
                }
            }
        }
    }

    fun setSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    private suspend fun getReservationStatus(
        content: Content,
        date: Date
    ): CenterReservationStatusResponse? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        return suspendCoroutine {
            apiService.getCenterReservation(
                content.centerId!!.toString(),
                dateFormat.format(date)
            )
                .enqueue(object : Callback<CenterReservationStatusResponse> {
                    override fun onResponse(
                        call: Call<CenterReservationStatusResponse>,
                        response: Response<CenterReservationStatusResponse>
                    ) {
                        it.resumeWith(Result.success(response.body()))
                    }

                    override fun onFailure(
                        call: Call<CenterReservationStatusResponse>,
                        t: Throwable
                    ) {
                        it.resumeWith(Result.failure(t))
                    }
                })
        }
    }

    suspend fun reserve(
        content: Content,
        times: List<String>,
        headcount: String
    ): ResponseReservation? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val request = RequestReservation().apply {
            reservingDate = dateFormat.format(_selectedDate.value)
            reservingTimes = times
            headCount = headcount
        }

        Log.d(
            "ReservationViewModel",
            "Reserve at ${request.reservingDate} ${request.reservingTimes?.joinToString(", ")}"
        )

        val result = suspendCoroutine {
            apiService.reserve(
                content.centerId!!.toString(),
                request
            )
                .enqueue(object : Callback<ResponseReservation> {
                    override fun onResponse(
                        call: Call<ResponseReservation>,
                        response: Response<ResponseReservation>
                    ) {
                        it.resumeWith(Result.success(response.body()))
                    }

                    override fun onFailure(
                        call: Call<ResponseReservation>,
                        t: Throwable
                    ) {
                        it.resumeWith(Result.failure(t))
                    }
                })
        }

        return result
    }
}