package com.example.integration4

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import java.io.FileInputStream
import java.io.FileNotFoundException

class StatisticsFragment : Fragment() {

    private lateinit var aaChartViewColumn: AAChartView
    private lateinit var aaChartViewSpline: AAChartView
    private lateinit var aaChartViewPie: AAChartView
    private lateinit var chartsLayout: ScrollView
    private lateinit var warningTV: TextView
    private val categories = mutableListOf<String>()
    private val seriesArray = mutableListOf<AASeriesElement>()
    private val contextTAG: String = "StatisticsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        aaChartViewColumn = view.findViewById(R.id.aaChartViewColumn)
        aaChartViewSpline = view.findViewById(R.id.aaChartViewSpline)
        aaChartViewPie = view.findViewById(R.id.aaChartViewPie)
        chartsLayout = view.findViewById(R.id.chartsSV)
        warningTV = view.findViewById(R.id.warningTV)

        val jsonObject = readRoomExpensesJsonFile()

        jsonObject?.let {
            setupChart(it)
            applyCustomCharts("Bar Chart", AAChartType.Column, aaChartViewColumn)
            applyCustomCharts("Line Chart", AAChartType.Spline, aaChartViewSpline)
            // TODO: Implement pie chart using it
            applyPieChart("Pie Chart", AAChartType.Pie, aaChartViewPie, it, GlobalAccess.userName)
//            applyPieChart12()
        }


        return view
    }

    private fun readRoomExpensesJsonFile(): JSONObject? {
        return try {
            val file = File(
                requireContext().getExternalFilesDir(null),
                getString(R.string.roomExpensesFileName)
            )

            val jsonString = file.bufferedReader().use { it.readText() }
            JSONObject(jsonString)

        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "Please visit Room expenses first", Toast.LENGTH_SHORT)
                .show()
            LOGGING.ERROR(
                requireContext(),
                contextTAG,
                "Statistics Received RoomExpenses File Not Found exception"
            )
            warningTV.text = "Room expenses not found,\nPlease visit Room expenses first"
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
                AASeriesElement()
                    .name(roommateNames[i]) // Set the name for the series
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
                .dataLabelsEnabled(true)
                .tooltipEnabled(false)
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

    private fun applyPieChart12() {

        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Pie) // Set chart type
            .title("Pie Chart")
            .subtitle("Expenses of nithish gajula")
            .dataLabelsEnabled(true)
            .tooltipEnabled(false)
            .yAxisTitle("Expenditure")
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Tokyo")
                        .yAxis(4),
                    AASeriesElement()
                        .name("NewYork")
                        .yAxis(4),
                    AASeriesElement()
                        .name("London")
                        .yAxis(4),
                    AASeriesElement()
                        .name("Berlin")
                        .yAxis(4),
                )
            )

        // Assign the chart model to the AAChartView
        aaChartViewPie.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun applyPieChart(
        title: String,
        type: AAChartType,
        aaChartView: AAChartView,
        jsonObject: JSONObject,
        userName: String
    ) {

        // Extract the roommates array from JSON
        val roommatesArray = jsonObject.getJSONArray("Roommates").getJSONArray(1)

        // Find the index of the user
        var userIndex = -1

        for (i in 0 until roommatesArray.length()) {
            if (roommatesArray.getString(i) == userName) {
                userIndex = i
                Log.d(contextTAG, "Found userIndex $userIndex")
            }
        }

        // If user not found, return or log an error
        if (userIndex == -1) {
            Log.e("ERROR", "User not found in roommates list")
            return
        }

        // Variables to store chart data
        val categories = mutableListOf<String>()  // Months
        val seriesArray = mutableListOf<Any>()    // Expenses

        // Iterate over each month and get user-specific expenses
        for (key in jsonObject.keys()) {
            if (key != "Roommates") {
                val monthArray = jsonObject.getJSONArray(key)

                Log.d(contextTAG, "monthArray at $key - $monthArray")

                // Get both the user's individual expense and the shared one, if any
                val individualExpenses =
                    monthArray.optJSONArray(1)?.optDouble(userIndex, 0.0) ?: 0.0
                val sharedExpenses = monthArray.optJSONArray(0)?.optDouble(userIndex, 0.0) ?: 0.0

                // Sum both individual and shared expenses
                val totalExpenses = individualExpenses + sharedExpenses

                Log.d(contextTAG, "userExpenses at $key - $totalExpenses")

                // Add month and corresponding user expense to chart data
                categories.add(key)  // e.g., "Apr 2024", "May 2024"
                seriesArray.add(totalExpenses)
            }
        }

        Log.d(contextTAG, "categories - $categories")
        Log.d(contextTAG, "seriesArray - $seriesArray")

        if (categories.isNotEmpty() && seriesArray.isNotEmpty()) {
            val aaChartModel = AAChartModel()
                .chartType(type) // Set chart type
                .title(title)
                .subtitle("Expenses of $userName")
                .categories(categories.toTypedArray()) // Use the months from the JSON
                .dataLabelsEnabled(true)
                .tooltipEnabled(false)
                .yAxisTitle("Expenditure")
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Expenses")
                            .data(seriesArray.toTypedArray())
                    )
                )

            // Assign the chart model to the AAChartView
            aaChartView.aa_drawChartWithChartModel(aaChartModel)
        } else {
            Log.e(contextTAG, "Categories or seriesArray is empty. Chart cannot be drawn.")
        }
    }


}
