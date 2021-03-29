package com.example.smd.ui.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smd.R
import com.github.aachartmodel.aainfographics.aachartcreator.*

class PlotFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_plot, container, false)
    }

    fun createPlot(dates: Array<String>, values: Array<Any>) {
        Log.d("values", values.toString())
        Log.d("cat", dates.toString())
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .title("Last year")
            .backgroundColor(Color.WHITE)
            .legendEnabled(false)
            .zoomType(AAChartZoomType.XY)
            .yAxisTitle("Price")
            .categories(
                dates
            )
            .series(
                arrayOf(
                    AASeriesElement()
                        .data(
                            values
                        )
                        .name("Price")
                )
            )

        val aaChartView = requireView().findViewById<AAChartView>(R.id.aa_chart_view)
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }
}