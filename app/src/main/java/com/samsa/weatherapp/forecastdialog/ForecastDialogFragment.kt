package com.samsa.weatherapp.forecastdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.samsa.weatherapp.R
import com.samsa.weatherapp.databinding.ForecastDialogBinding
import com.samsa.weatherapp.model.ForecastItem
import com.samsa.weatherapp.utils.dayDataFormatDate

class ForecastDialogFragment : DialogFragment() {

    private lateinit var binding: ForecastDialogBinding
    private lateinit var adapter: ForecastCardAdapter

    private val forecastList: List<ForecastItem>?
        get() = arguments?.getSerializable(ARG_ITEMS_LIST) as? ArrayList<ForecastItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ForecastDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ForecastCardAdapter()

        binding.tvForecastDialogWeek.text = dayDataFormatDate(forecastList?.get(0)?.dt_txt ?: "")

        binding.rcForecastCards.adapter = adapter
        binding.rcForecastCards.layoutManager = LinearLayoutManager(requireContext())

        forecastList?.let { adapter.submitList(it) }

        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.TransparentBackgroundDialog)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.60).toInt()

        dialog?.window?.setLayout(width, height)
    }

    companion object {
        private const val ARG_ITEMS_LIST = "forecast_items"

        fun newInstance(list: ArrayList<ForecastItem>): ForecastDialogFragment {
            return ForecastDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_ITEMS_LIST, list)
                }
            }
        }
    }
}