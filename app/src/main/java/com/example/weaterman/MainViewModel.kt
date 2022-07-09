package com.example.weaterman

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

open class MainViewModel: ViewModel(), LocationListener {
    val listData = MutableLiveData<List<WeatherInfo>>()
    val dayData = MutableLiveData<WeatherInfo>()
    @SuppressLint("StaticFieldLeak")
    private var activity: FragmentActivity? = null
    val adapter = WeatherAdapter()
    private val imageIdList = listOf(
        R.drawable.sunny,
        R.drawable.partly_cloudy,
        R.drawable.cloudy,
        R.drawable.drizzle,
        R.drawable.rain,
        R.drawable.show,
        R.drawable.hail,
        R.drawable.thunderstorm,
        R.drawable.end_of_the_world,
    )
    private val weatherDescribe = listOf(
        "ясно",
        "облачн",
        "пасмурно",
        "туман",
        "дождь",
        "снег",
        "град",
        "гроза"
    )
    private val key = "c58c6b30fac929947abde8df899ea688"

    fun getAPI(s: String, a: FragmentActivity){
        val url = "https://api.openweathermap.org/data/2.5/forecast?q=$s" +
                            "&appid=$key&lang=ru&units=metric"
        activity = a
        CoroutineScope(Dispatchers.IO).launch {
            getURLData(url)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getURLData(url: String) {
        val queue = Volley.newRequestQueue(activity)
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response->
                val obj = JSONObject(response)
                val day = obj.getJSONArray("list").getJSONObject(0)

                val dayUpdate = WeatherInfo(
                    day.getString("dt_txt"),
                    day.getJSONArray("weather").getJSONObject(0)
                        .getString("description"),
                    day.getJSONObject("main").getInt("temp_min")
                        .toString(),
                    day.getJSONObject("main").getInt("temp_max")
                        .toString(),
                    getImg(day.getJSONArray("weather").getJSONObject(0)
                        .getString("description")),
                    obj.getJSONObject("city")
                        .getString("name"),
                    day.getJSONObject("main").getInt("temp")
                        .toString(),
                    day.getJSONObject("main")
                        .getInt("feels_like").toString(),
                    day.getJSONObject("wind")
                        .getDouble("speed").toString(),
                    day.getJSONObject("wind")
                        .getDouble("deg").toString()
                )
                dayData.value = dayUpdate

                adapter.clearDayList()
                for (elem in 1..obj.getInt("cnt") step 4){
                    val oneDay = obj.getJSONArray("list").getJSONObject(elem)
                    val dayInfo = WeatherInfo(
                        oneDay.getString("dt_txt"),
                        oneDay.getJSONArray("weather").getJSONObject(0)
                            .getString("description"),
                        oneDay.getJSONObject("main").getInt("temp_min")
                            .toString(),
                        oneDay.getJSONObject("main").getInt("temp_max")
                            .toString(),
                        getImg(oneDay.getJSONArray("weather").getJSONObject(0)
                            .getString("description")),
                        obj.getJSONObject("city")
                            .getString("name"),
                        oneDay.getJSONObject("main").getInt("temp")
                            .toString(),
                        oneDay.getJSONObject("main")
                            .getInt("feels_like").toString(),
                        oneDay.getJSONObject("wind")
                            .getDouble("speed").toString(),
                        oneDay.getJSONObject("wind")
                            .getDouble("deg").toString()
                    )
                    adapter.addDay(dayInfo)
                }

            },
            {
                Toast.makeText(activity, "$it", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequest)

    }
    private fun getImg(s: String): Int {
        var out = imageIdList[8]
        for (elem in weatherDescribe){
            if (elem in s){
                out = imageIdList[weatherDescribe.indexOf(elem)]
            }
        }
        return out
    }

    @SuppressLint("MissingPermission")
    fun getLocation(a: FragmentActivity) {
        activity = a
        try {
            val locationManager: LocationManager =
                activity!!.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000,
                500.toFloat(), this
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$latitude&lon=$longitude&appid=$key&lang=ru&units=metric"
        CoroutineScope(Dispatchers.IO).launch{getURLData(url)}
    }
}