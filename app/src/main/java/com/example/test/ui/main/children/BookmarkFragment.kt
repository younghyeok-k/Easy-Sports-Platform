package com.example.test.ui.main.children

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.FragmentBookmarkBinding
import com.example.test2.Adapter.NewsAdpater
import com.example.test2.Adapter.WeatherAdapter
import com.example.test2.Model.ModelNews
import com.example.test2.Model.ModelWeather
import com.example.test2.network.*
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class BookmarkFragment : Fragment() {

    private val TAG = BookmarkFragment::class.java.simpleName
    lateinit var weatherRecyclerView: RecyclerView
    lateinit var newsRecyclerView: RecyclerView
    private val clientId = "N0PQoQDH_BW4MgVBisw1"
    private val clientSecret = "IbesixkkrN"
    private var base_date = "20221101" // 발표 일자
    private var base_time = "1200" // 발표 시각
    private var nx = "81" // 예보지점 X 좌표
    private var ny = "75" // 예보지점 Y 좌표
    private var curPoint: Point? = null
    private lateinit var currentLocation: String
    private lateinit var binding: FragmentBookmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)
        weatherRecyclerView = binding.weatherRecyclerView
        newsRecyclerView = binding.newsRecyclerView
        val tvDate=binding.tvDate


        weatherRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        weatherRecyclerView.layoutManager =
            LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.HORIZONTAL }
        newsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
// 오늘 날짜 텍스트뷰 설정
        tvDate.text = SimpleDateFormat(
            "MM월 dd일",
            Locale.getDefault()
        ).format(Calendar.getInstance().time) + " 날씨"
        requestLocation()
        getResultSearch()


        return binding.root


    }
    // 날씨 가져와서 설정하기
    private fun setWeather(nx: Int, ny: Int) {
        val cal = Calendar.getInstance()
        base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time) // 현재 날짜
        val timeH = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time)
        val timeM = SimpleDateFormat("MM", Locale.getDefault()).format(cal.time)

// API 가져오기 적당하게 변환
        base_time = getBaseTime(timeH, timeM)
// API 가져오기 적당하게 변환
// base_time = getBaseTime(timeH, timeM)
// 현재 시각이 00시이고 45분 이하여서 baseTime이 2330이면 어제 정보 받아오기
        if (timeH == "00" && base_time == "2330") {
            cal.add(Calendar.DATE, -1).toString()
            base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }

// 날씨 정보 가져오기
// (한 페이지 결과 수 = 60, 페이지 번호 = 1, 응답 자료 형식-"JSON", 발표 날싸, 발표 시각, 예보지점 좌표)
        val call =
            ApiObject.retrofitService.GetWeather(60, 1, "JSON", base_date, base_time, nx, ny)

// 비동기적으로 실행하기
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            // 응답 성공 시
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
// 날씨 정보 가져오기
                    val it: List<ITEM> = response.body()!!.response.body.items.item

// 현재 시각부터 1시간 뒤의 날씨 6개를 담을 배열
                    val weatherArr = arrayOf(
                        ModelWeather(),
                        ModelWeather(),
                        ModelWeather(),
                        ModelWeather(),
                        ModelWeather(),
                        ModelWeather(),
                    )
                    val rnarr = arrayOf(
                        String(),
                        String(),
                        String(),
                        String(),
                        String(),
                        String(),
                    )
                    val wsdarr = arrayOf(
                        String(),
                        String(),
                        String(),
                        String(),
                        String(),
                        String(),
                    )
                    val uuuarr = arrayOf(
                        String(),
                        String(),
                        String(),
                        String(),
                        String(),
                        String(),
                    )

// 배열 채우기

                    var index = 0
                    val totalCount = response.body()!!.response.body.totalCount - 1
                    for (i in 0..totalCount) {
                        index %= 6
                        when (it[i].category) {
                            "PTY" -> weatherArr[index].rainType = it[i].fcstValue // 강수 형태
                            "REH" -> weatherArr[index].humidity = it[i].fcstValue // 습도
                            "SKY" -> weatherArr[index].sky = it[i].fcstValue // 하늘 상태
                            "T1H" -> weatherArr[index].temp = it[i].fcstValue // 기온
                            "RN1" -> rnarr[index] = it[i].fcstValue // 1시간 강수량
                            "WSD" -> wsdarr[index] = it[i].fcstValue // 풍속
                            "UUU" -> uuuarr[index] = it[i].fcstValue // 동서 바람

                            else -> continue
                        }
                        index++
                    }
                    weatherArr[0].fcstTime = "지금"
                    var wsd = wsdarr[0]
                    var uuu=uuuarr[0]


// 각 날짜 배열 시간 설정
                    for (i in 0..5) weatherArr[i].fcstTime = it[i].fcstTime

// 리사이클러 뷰에 데이터 연결
                    weatherRecyclerView.adapter = WeatherAdapter(weatherArr)
                    Log.d("시간:", base_time)
//                    val tvTime = findViewById<TextView>(R.id.tvTime)
                    val imgWeather = binding.imgWeather
                    val tvTemp = binding.tvTemp
                    val tvHumidity = binding.tvHumidity
                    val clotext = binding.clotext
                    val umtext = binding.umtext
                    val wendytext = binding.wendytext
                    val clothes = binding.clothes
                    val tvwww = binding.tvwww
                    val tvrainy = binding.tvrainy
//                    tvTime.text = base_time
                    imgWeather.setImageResource(
                        getRainImage(
                            weatherArr[0].rainType,
                            weatherArr[0].sky,
                            weatherArr[0].fcstTime
                        )
                    )
//                    tvTime.text = getTime(weatherArr[0].fcstTime)
                    tvHumidity.text = weatherArr[0].humidity + "%"
                    tvTemp.text = weatherArr[0].temp + "°"
                    clotext.text = getRecommends(weatherArr[0].temp.toInt())
                    wendytext.text = getWSD(wsd.toInt())
                    clothes.setImageResource(getclo(weatherArr[0].temp.toInt()))
//                    tvwww.text
                    tvrainy.text=if (rnarr[0].equals("강수없음")) "-mm" else rnarr[0]
//                    if(rn=="강수없음"){
//                        umtext.text="필요없음"
//                    }else{
//                        umtext.text="강수확률"+rn+""
//                    }

                    for (i in 0..5) {
                        if (!rnarr[i].equals("강수없음")) {
                            umtext.text = "챙겨야합니다"
                        }

                    }
                    tvwww.text=if (0<=uuu.toFloat()) "동 "+uuu+" m/s" else "서 "+uuu+" m/s"

                }
            }

            //check
            // 응답 실패 시
            override fun onFailure(call: Call<WEATHER>, t: Throwable) {

            }
        })
    }


    // 내 현재 위치의 위경도를 격자 좌표로 변환하여 해당 위치의 날씨정보 설정하기
    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        try {
            // 나의 현재 위치 요청
            val locationRequest = LocationRequest.create()
            locationRequest.run {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 60 * 100000   // 요청 간격(100초)
            }
            val locationCallback = object : LocationCallback() {
                // 요청 결과
                override fun onLocationResult(p0: LocationResult) {
                    p0.let {
                        for (location in it.locations) {

                            Log.d("위치", (location.latitude).toString())
                            Log.d("위치", (location.longitude).toString())
                            var geocoder = Geocoder(requireContext().applicationContext)
                            var mResultlist: List<Address>

                            mResultlist = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            ) as List<Address>
                            currentLocation = mResultlist[0].locality

                            val txad = binding.address
                            txad.text = currentLocation
                            // 현재 위치의 위경도를 격자 좌표로 변환
                            curPoint = dfsXyConv(location.latitude, location.longitude)
                            Log.d("위치2", (curPoint!!.x).toString())
                            Log.d("위치2", (curPoint!!.y).toString())
                            // nx, ny지점의 날씨 가져와서 설정하기
                            setWeather(curPoint!!.x, curPoint!!.y)

                        }
                    }
                }
            }

            // 내 위치 실시간으로 감지
            Looper.myLooper()?.let {
                locationClient.requestLocationUpdates(
                    locationRequest, locationCallback,
                    it
                )
            }


        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    fun getResultSearch() {
        val apiInterface: ApiInterface = ApiClient.instance!!.create(ApiInterface::class.java)
        val call = apiInterface.getSearchResult(clientId, clientSecret, "news", "오늘날씨")

        call!!.enqueue(object : retrofit2.Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful && response.body() != null) {
                    var title: String
                    var link: String
                    var pubDate: String
                    var jsonObject: JSONObject? = null

                    val newlist = mutableListOf(
                        ModelNews(),
                        ModelNews(),
                        ModelNews(),
                        ModelNews(),
                        ModelNews(),
                        ModelNews(),
                        ModelNews(),
                        ModelNews(),
                        ModelNews(),
                        ModelNews(),
                    )

                    try {
                        jsonObject = JSONObject(response.body())
                        val jsonArray = jsonObject.getJSONArray("items")
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            link = item.getString("link")
                            title = item.getString("title").replace("&quot;", " \"\" ")
                                .replace("&apos;", "").replace("<b>", "").replace("</b>", "")
                            pubDate = item.getString("pubDate")
                            newlist[i].title = title
                            newlist[i].link = link

                            newlist[i].pubDate = pubDate
                            Log.d("뉴스", newlist[i].title)
                            Log.d("뉴스", newlist[i].link)
                        }
                        val adpter = NewsAdpater(this, newlist)
                        adpter.setOnItemClickListener(object : NewsAdpater.OnItemClickListener {
                            override fun onItemClick(v: View, pos: Int) {
                                var intent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(newlist[pos].link))
                                startActivity(intent)
                            }
                        })
                        newsRecyclerView.adapter = adpter

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {

                }
            }

            override fun onFailure(call: Call<String?>?, t: Throwable) {

            }
        })
    }

    fun getRainImage(rainType: String, sky: String, factTime: String): Int {
        return when (rainType) {
            "0" -> getWeatherImage(sky, factTime)
            "1" -> R.drawable.rainy
            "2" -> R.drawable.hail
            "3" -> R.drawable.snowy
            "4" -> R.drawable.brash
            else -> getWeatherImage(sky, factTime)
        }
    }

    fun getWeatherImage(sky: String, factTime: String): Int {
        // 하늘 상태
        return when (sky) {
            "1" -> getWeatherImage2(factTime)                      // 맑음
            "3" -> R.drawable.cloudy                     // 구름 많음
            "4" -> R.drawable.blur2                // 흐림
            else -> R.drawable.ic_launcher_foreground   // 오류
        }
    }


    fun getWeatherImage2(factTime: String): Int {

        return if (factTime.toInt() < 600) return R.drawable.after
        else if (factTime.toInt() < 1800) return R.drawable.sun2
        else return R.drawable.after


    }

    fun getclo(temp: Int): Int {
        return when (temp) {
            in 5..8 -> R.drawable.clo58
            in 9..11 -> R.drawable.clo119
            in 12..16 -> R.drawable.clo1216
            in 17..19 -> R.drawable.clo1719
            in 20..22 -> R.drawable.clo2022
            in 23..27 -> R.drawable.clo2327
            in 28..50 -> R.drawable.clo2850
            else -> R.drawable.clo0
        }
    }

    fun getRecommends(temp: Int): String {
        return when (temp) {
            in 5..8 -> "겨울 코트, 가죽 자켓, 기모"
            in 9..11 -> "얇은 코트, 데님자켓, 간절기 점퍼"
            in 12..16 -> "자켓, 가디건, 두꺼운 니트"
            in 17..19 -> "니트, 맨투맨, 후드, 긴바지"
            in 20..22 -> "블라우스, 긴팔 티, 슬랙스"
            in 23..27 -> "얇은 셔츠, 반바지, 면바지"
            in 28..50 -> "민소매, 반바지, 린넨 옷"
            else -> "패딩, 장갑, 목도리"
        }
    }

    fun getWSD(temp: Int): String {
        return when (temp) {
            in 0..1 -> "매우 양호"
            in 2..3 -> "양호"
            in 4..8 -> "약간강한 바람"
            in 9..14 -> "강한 바람"
            in 15..25 -> "간판이 떨어져 나갈정도의 바람"
            in 25..34 -> "허술한 비 붕괴정도의 바람"
            in 28..50 -> "기차전복 정도의 바람"
            else -> "콘크리트 건축물 붕괴 바람"
        }
    }

    fun getBaseTime(h : String, m : String) : String {
        var result = ""

        // 45분 전이면
        if (m.toInt() < 45) {
            // 0시면 2330
            if (h == "00") result = "2330"
            // 아니면 1시간 전 날씨 정보 부르기
            else {
                var resultH = h.toInt() - 1
                // 1자리면 0 붙여서 2자리로 만들기
                if (resultH < 10) result = "0" + resultH + "30"
                // 2자리면 그대로
                else result = resultH.toString() + "30"
            }
        }
        // 45분 이후면 바로 정보 받아오기
        else result = h + "30"

        return result
    }

    // 위경도를 기상청에서 사용하는 격자 좌표로 변환
    fun dfsXyConv(v1: Double, v2: Double): Point {
        val RE = 6371.00877     // 지구 반경(km)
        val GRID = 5.0          // 격자 간격(km)
        val SLAT1 = 30.0        // 투영 위도1(degree)
        val SLAT2 = 60.0        // 투영 위도2(degree)
        val OLON = 126.0        // 기준점 경도(degree)
        val OLAT = 38.0         // 기준점 위도(degree)
        val XO = 43             // 기준점 X좌표(GRID)
        val YO = 136       // 기준점 Y좌표(GRID)
        val DEGRAD = Math.PI / 180.0
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)

        var ra = Math.tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5)
        ra = re * sf / Math.pow(ra, sn)
        var theta = v2 * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        val x = (ra * Math.sin(theta) + XO + 0.5).toInt()
        val y = (ro - ra * Math.cos(theta) + YO + 0.5).toInt()

        return Point(x, y)
    }

}
