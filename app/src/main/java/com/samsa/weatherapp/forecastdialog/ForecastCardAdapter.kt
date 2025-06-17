package com.samsa.weatherapp.forecastdialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samsa.weatherapp.databinding.SampleForecastCardBinding
import com.samsa.weatherapp.model.ForecastItem
import com.samsa.weatherapp.utils.formatTime

class ForecastCardAdapter : ListAdapter<ForecastItem, ForecastCardAdapter.ForecastViewHolder>(DiffCallback()) {

    inner class ForecastViewHolder(private val binding: SampleForecastCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem) {
            binding.tvForecastCardWeek.text = formatTime(item.dt_txt!!)
            binding.tvForecastCardTemp.text = "+${item.main?.temp ?: 0}℃"
            binding.tvForecastCardWind.text = "${item.wind?.speed ?: 0}м/с"
            binding.tvForecastCardHumidity.text = "${item.main?.humidity ?: 0}%"

            binding.imageView7.setImageResource(
                itemView.context.resources.getIdentifier(
                    "ic_${
                        item.weather?.get(
                            0
                        )?.icon}", "drawable", itemView.context.packageName))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = SampleForecastCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ForecastItem>() {
        override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
            return oldItem == newItem
        }
    }
}