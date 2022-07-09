package com.example.weaterman

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weaterman.databinding.ListItemBinding

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.Holder>() {

    private var dayList = ArrayList<WeatherInfo>()

    class Holder(view: View): RecyclerView.ViewHolder(view){

        private val binding = ListItemBinding.bind(view)
        @SuppressLint("SetTextI18n")
        fun bind(elem: WeatherInfo) = with(binding){
            dayTime.text = elem.dayTime
            MaxMinTemp.text = "${elem.tempMin}°C/${elem.tempMax}°C"
            descriptionDay.text = elem.description
            imageView2.setImageResource(elem.img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dayList[position])
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addDay(day: WeatherInfo){
        dayList.add(day)
        notifyDataSetChanged()
    }
    fun clearDayList(){
        dayList = ArrayList<WeatherInfo>()
    }
}