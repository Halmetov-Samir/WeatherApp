package com.samsa.weatherapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.samsa.weatherapp.databinding.SampleWeatherCardBinding
import com.samsa.weatherapp.model.ForecastItem
import com.samsa.weatherapp.utils.formatDate
import com.samsa.weatherapp.utils.formatForecastDate
import java.util.Date

class ForecastAdapter(private val cont: Context): RecyclerView.Adapter<ForecastAdapter.ForecastHolder>() {
    private val differ = AsyncListDiffer(this, DiffCallback)

    fun submitList(newList: List<ForecastItem>?) {
        differ.submitList(newList)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ForecastItem>() {
            override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
                return oldItem.dt == newItem.dt
            }

            override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ForecastHolder(val binding: SampleWeatherCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fData: ForecastItem) = with(binding) {
            tvForecastTemp.text = "+${fData.main?.temp}℃"
            tvWeek.text = formatForecastDate(Date(fData.dt!!))

            binding.imageView.setImageResource(
                cont.resources.getIdentifier(
                    "ic_${
                        fData.weather?.get(
                            0
                        )?.icon
                    }", "drawable", cont.packageName)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastHolder {
        val binding = SampleWeatherCardBinding.inflate(LayoutInflater.from(parent.context), parent, false) // Используем inflate из Binding
        return ForecastHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastHolder, position: Int) {
        val fData = differ.currentList[position]
        holder.bind(fData)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
