package com.example.integration4

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException

class RoomExpensesChartsFragment : Fragment() {

    private lateinit var aaChartViewColumn: AAChartView
    private lateinit var aaChartViewSpline: AAChartView
    private lateinit var aaChartViewArea: AAChartView
    private lateinit var chartsLayout: ScrollView
    private lateinit var warningTV: TextView
    private val categories = mutableListOf<String>()
    private val seriesArray = mutableListOf<AASeriesElement>()
    private val contextTAG: String = "RoomExpensesChartsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_room_expenses_charts, container, false)

        aaChartViewColumn = view.findViewById(R.id.aaChartViewColumn_room_id)
        aaChartViewSpline = view.findViewById(R.id.aaChartViewSpline_room_id)
        aaChartViewArea = view.findViewById(R.id.aaChartViewArea_room_id)
        chartsLayout = view.findViewById(R.id.charts_sv_room_id)
        warningTV = view.findViewById(R.id.warn_tv_room_id)

        val jsonObject = readRoomExpensesJsonFile()

        jsonObject?.let {
            setupChart(it)
            applyCustomCharts("Bar Chart", AAChartType.Column, aaChartViewColumn)
            applyCustomCharts("Line Chart", AAChartType.Spline, aaChartViewSpline)
            applyCustomCharts("Area Chart", AAChartType.Area, aaChartViewArea)
        }
        return view
    }

    private fun readRoomExpensesJsonFile(): JSONObject? {
        return try {
            val file = File(
                requireContext().getExternalFilesDir(null), getString(R.string.roomExpensesFileName)
            )

            val jsonString = file.bufferedReader().use { it.readText() }
            JSONObject(jsonString)

        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "Please visit Room expenses first", Toast.LENGTH_SHORT)
                .show()
            LOGGING.ERROR(
                requireContext(),
                contextTAG,
                "RoomExpensesChartsFragment Received RoomExpenses File Not Found exception"
            )
            warningTV.text = "Room expenses file not found,\nPlease visit Room expenses first"
            warningTV.visibility = View.VISIBLE
            chartsLayout.visibility = View.GONE

            null
        } catch (e: Exception) {
            LOGGING.ERROR(requireContext(), contextTAG, "Unexpected error: ${e.message}")
            null
        }
    }

    private fun setupChart(jsonObject: JSONObject) {
        // Clear previous data
        categories.clear()
        seriesArray.clear()

        // Extract roommate names and number of roommates
        val roommatesArray = jsonObject.getJSONArray("Roommates").getJSONArray(1)
        val roommateNames = mutableListOf<String>()
        for (i in 0 until roommatesArray.length()) {
            roommateNames.add(roommatesArray.getString(i))
        }

        // Initialize categories and expenses list for each roommate
        val roommatesExpenses = Array(roommateNames.size) { mutableListOf<Int>() }

        // Iterate through the months (keys) in the JSON object, skipping the "Roommates" key
        jsonObject.let {
            val keys = it.keys()
            while (keys.hasNext()) {
                val month = keys.next()
                if (month == "Roommates") continue // Skip "Roommates" key

                categories.add(month) // Add month to the categories list
                val monthData = it.getJSONArray(month)

                // For each roommate, sum their expenses for the current month
                for (i in 0 until roommateNames.size) {
                    val personData = monthData.getJSONArray(i)
                    var personSum = 0
                    for (k in 0 until personData.length()) {
                        personSum += personData.getInt(k) // Get each expense and add to sum
                    }
                    roommatesExpenses[i].add(personSum) // Add sum to the corresponding roommate's list
                }
            }
        }

        // Create dynamic series for AAChartModel based on the number of roommates
        for (i in 0 until roommateNames.size) {
            seriesArray.add(
                AASeriesElement().name(roommateNames[i]) // Set the name for the series
                    .data(roommatesExpenses[i].toTypedArray()) // Set the expenses data
            )
        }
    }

    private fun applyCustomCharts(title: String, type: AAChartType, aaChartView: AAChartView) {
        // Ensure categories and seriesArray have been populated before drawing chart
        if (categories.isNotEmpty() && seriesArray.isNotEmpty()) {
            val aaChartModel = AAChartModel()
                .chartType(type) // Set chart type
                .title(title)
                .subtitle("Comparing Expenses of Roommates")
                .categories(categories.toTypedArray()) // Use the months from the JSON
                .dataLabelsEnabled(false)
                .tooltipEnabled(true)
                .yAxisTitle("Expenditure")
                .series(seriesArray.toTypedArray()) // Use dynamically generated series

            // Assign the chart model to the AAChartView
            aaChartView.aa_drawChartWithChartModel(aaChartModel)
        } else {
            Log.e(contextTAG, "Categories or seriesArray is empty. Chart cannot be drawn.")
            warningTV.text = "Charts available for more than one roommates"
            warningTV.visibility = View.VISIBLE
            chartsLayout.visibility = View.GONE
        }
    }

}