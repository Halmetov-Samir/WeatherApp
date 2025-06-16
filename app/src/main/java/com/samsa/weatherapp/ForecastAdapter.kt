package com.samsa.weatherapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.samsa.weatherapp.databinding.SampleWeatherCardBinding
import com.samsa.weatherapp.forecastdialog.ForecastDialogFragment
import com.samsa.weatherapp.model.DayData
import com.samsa.weatherapp.model.ForecastItem
import com.samsa.weatherapp.utils.dayDataFormatDate
import com.samsa.weatherapp.utils.formatDate
import com.samsa.weatherapp.utils.formatForecastDate
import com.samsa.weatherapp.utils.parseDateToTimestamp
import java.util.Date

class ForecastAdapter(private val cont: Context, private val fragmentManager: FragmentManager): RecyclerView.Adapter<ForecastAdapter.ForecastHolder>() {
    private val differ = AsyncListDiffer(this, DiffCallback)

    fun submitList(newList: List<DayData>) {
        differ.submitList(newList)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DayData>() {
            override fun areItemsTheSame(oldItem: DayData, newItem: DayData): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: DayData, newItem: DayData): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ForecastHolder(val binding: SampleWeatherCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fData: DayData) = with(binding) {
            var middleTemp = 0.0
            fData.forecastItems.forEach { middleTemp += it.main!!.temp!! }
            middleTemp /= fData.forecastItems.size

            tvForecastTemp.text = "+%.1f℃".format(middleTemp)
            tvWeek.text = dayDataFormatDate(fData.date)
            imageView.setImageResource(
                cont.resources.getIdentifier(
                    "ic_${
                        fData.forecastItems.get(fData.forecastItems.size/2).weather?.get(
                            0
                        )?.icon}", "drawable", cont.packageName))
            clClicableElement.setOnClickListener {
                val dialog = ForecastDialogFragment.newInstance(ArrayList(fData.forecastItems))
                dialog.show(fragmentManager, "ForecastDialog")
            }
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
