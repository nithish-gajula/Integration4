package com.example.integration4

import android.os.Bundle
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
import com.github.aachartmodel.aainfographics.aachartcreator.AADataElement
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException

class MyExpensesChartsFragment : Fragment() {

    private lateinit var aaChartViewColumn: AAChartView
    private lateinit var aaChartViewSpline: AAChartView
    private lateinit var aaChartViewPie: AAChartView
    private lateinit var aaChartViewArea: AAChartView
    private lateinit var chartsLayout: ScrollView
    private lateinit var warningTV: TextView
    private val categories = mutableListOf<String>()
    private val seriesArray = mutableListOf<Int>()
    private val pieData = mutableListOf<AADataElement>()
    private val contextTAG: String = "MyExpensesChartsFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_expenses_charts, container, false)

        aaChartViewColumn = view.findViewById(R.id.aaChartViewColumn_my_id)
        aaChartViewSpline = view.findViewById(R.id.aaChartViewSpline_my_id)
        aaChartViewPie = view.findViewById(R.id.aaChartViewPie_my_id)
        aaChartViewArea = view.findViewById(R.id.aaChartViewArea_my_id)
        chartsLayout = view.findViewById(R.id.charts_sv_my_id)
        warningTV = view.findViewById(R.id.warn_tv_my_id)

        val jsonObject = readRoomExpensesJsonFile()
        jsonObject?.let {
            setupChart(it)
            if (categories.isNotEmpty() && seriesArray.isNotEmpty()) {
                applyCustomCharts("Bar Chart", AAChartType.Column, aaChartViewColumn)
                applyCustomCharts("Line Chart", AAChartType.Spline, aaChartViewSpline)
                applyCustomCharts("Area Chart", AAChartType.Areaspline, aaChartViewArea)
                applyPieChart("Pie Chart", AAChartType.Pie, aaChartViewPie)

            }
        }

        return view
    }

    private fun simpleChartExample() {
        val months = arrayOf("Apr 2025", "May 2025", "Jun 2025", "Jul 2025")
        val expenses = arrayOf(900, 3000, 600, 1500)

        val chartModel = AAChartModel()
            .chartType(AAChartType.Column) // 📊 Column chart
            .title("Monthly Expenses")
            .subtitle("Your spending pattern")
            .yAxisTitle("Amount (₹)")
            .categories(months) // ✅ X-axis
            .dataLabelsEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Expenses")
                        .data(expenses.map { it.toDouble() }.toTypedArray()) // ✅ Y-axis
                )
            )
//        aaChartViewColumn_testing.aa_drawChartWithChartModel(chartModel)

        /**
        val example_bar_chart_configs =
            "{\"animationDuration\":500," +
                    "\"animationType\":\"Linear\"," +
                    "\"backgroundColor\":\"#ffffff\"," +
                    "\"borderRadius\":0.0," +
                    "\"categories\":[\"\",\"\",\"\",\"\"]," +
                    "\"chartType\":\"Bar\"," +
                    "\"colorsTheme\":[\"#fe117c\",\"#ffc069\",\"#06caf4\",\"#7dffc0\"]," +
                    "\"dataLabelsEnabled\":false," +
                    "\"gradientColorEnable\":false," +
                    "\"inverted\":false," +
                    "\"legendEnabled\":true," +
                    "\"markerRadius\":6.0," +
                    "\"markerSymbolStyle\":\"Normal\"," +
                    "\"polar\":false," +
                    "\"series\":[{\"data\":[3.0,6.0,4.0,7.0],\"name\":\"Series 1\"}]," +
                    "\"stacking\":\"False\"," +
                    "\"subtitle\":\"\"," +
                    "\"title\":\"Single Horizontal Bar Chart\"," +
                    "\"titleStyle\":{}," +
                    "\"xAxisGridLineWidth\":0.0," +
                    "\"xAxisLabelsEnabled\":true," +
                    "\"xAxisReversed\":false," +
                    "\"yAxisGridLineWidth\":1.0," +
                    "\"yAxisLabelsEnabled\":true," +
                    "\"yAxisReversed\":false," +
                    "\"zoomType\":\"None\"}"
        */


    }

    private fun readRoomExpensesJsonFile(): JSONObject? {
        return try {
            val file = File(requireContext().getExternalFilesDir(null), getString(R.string.roomExpensesFileName))
            val jsonString = file.bufferedReader().use { it.readText() }
            JSONObject(jsonString)
        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "Please visit Room expenses first", Toast.LENGTH_SHORT).show()
            LOGGING.ERROR(requireContext(), contextTAG, "MyExpensesChartsFragment Received roomExpenses File Not Found exception")
            warningTV.text = "Room Expenses file not found,\nPlease visit Room expenses first"
            warningTV.visibility = View.VISIBLE
            chartsLayout.visibility = View.GONE
            null
        } catch (e: Exception) {
            LOGGING.ERROR(requireContext(), contextTAG, "Unexpected error: ${e.message}")
            null
        }
    }

    private fun setupChart(jsonObject: JSONObject) {
        categories.clear()
        seriesArray.clear()

        val roomates_ids = jsonObject.getJSONArray("Roommates").getJSONArray(2)
        var user_index = -1
        for (i in 0 until roomates_ids.length()) {
            if (roomates_ids.getString(i) == GlobalAccess.userId) {
                user_index = i
            }
        }

        jsonObject.let {
            val keys = it.keys()
            while (keys.hasNext()) {
                val month = keys.next()
                if (month == "Roommates") continue // Skip "Roommates" key

                categories.add(month) // Add month to the categories list
                val monthData = it.getJSONArray(month)
                val personData = monthData.getJSONArray(user_index)
                var personSum = 0
                for (k in 0 until personData.length()) {
                    personSum += personData.getInt(k) // Get each expense and add to sum
                }
                seriesArray.add(personSum) // Add sum to the corresponding roommate's list
                pieData.add(AADataElement().name(month).y(personSum.toFloat()))
            }
        }
    }

    private fun applyCustomCharts(title: String, type: AAChartType, aaChartView: AAChartView) {

        val aaChartModel = AAChartModel()
            .chartType(type) // Set chart type
            .title(title)
            .subtitle("Spending pattern of ${GlobalAccess.userName}")
            .yAxisTitle("Amount (₹)")
            .categories(categories.toTypedArray()) // Use the months from the JSON
            .dataLabelsEnabled(true)
            .tooltipEnabled(true)
            .animationDuration(500)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Expenses")
                        .data(seriesArray.toTypedArray()) // ✅ Y-axis
//                        .color("#673AB7")
                )
            )
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }

    private fun applyPieChart(title: String, type: AAChartType, aaChartView: AAChartView) {

        val aaChartModel = AAChartModel()
            .chartType(type) // Set chart type
            .title(title)
            .subtitle("Spending pattern of ${GlobalAccess.userName}")
            .dataLabelsEnabled(true)
            .tooltipEnabled(true)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Expenses")
                        .data(pieData.toTypedArray()) // ✅ Y-axis
                )
            )
        aaChartView.aa_drawChartWithChartModel(aaChartModel)
    }
}