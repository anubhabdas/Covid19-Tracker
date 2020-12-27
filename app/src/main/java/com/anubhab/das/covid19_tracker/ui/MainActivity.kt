package com.anubhab.das.covid19_tracker.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.anubhab.das.covid19_tracker.R
import com.anubhab.das.covid19_tracker.model.Response
import com.anubhab.das.covid19_tracker.ui.adapter.StateAdapter
import com.anubhab.das.covid19_tracker.model.StatewiseItem
import com.anubhab.das.covid19_tracker.model.Client
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eazegraph.lib.models.PieModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var stateAdapter: StateAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.isNestedScrollingEnabled = false
        list.layoutManager = LinearLayoutManager(this);
        fetchResult()
        swipeToRefresh.setOnRefreshListener {
            fetchResult()
        }

    }

    private fun fetchResult() {
        GlobalScope.launch {
            val response = withContext(Dispatchers.IO){ Client.api.clone().execute()};
            if(response.isSuccessful){
                swipeToRefresh.isRefreshing = false
                val data = Gson().fromJson(response.body?.string(), Response::class.java)
                launch(Dispatchers.Main) {
                    bindCombinedData(data.statewise[0])
                    bindPieData(data.statewise[0])
//                    bindMpPieData(data.statewise[0])
                    bindStateWiseData(data.statewise.subList(0, data.statewise.size) as MutableList<StatewiseItem>)
                }
            }
            else{
                Log.d("Data unsuccessful", "Data not received${response.code}")
            }
        }
    }

    private fun bindStateWiseData(subList: MutableList<StatewiseItem>) {
        stateAdapter =
            StateAdapter(subList)
        list.adapter = stateAdapter
    }

    private fun bindCombinedData(data: StatewiseItem) {
        val lastUpdatedTime: String? = data.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        lastUpdatedTv.text = "Last Updated\n ${getTimeAgo(simpleDateFormat.parse(lastUpdatedTime))}"

        confirmedTv.text = data.confirmed
        activeTv.text = data.active
        recoveredTv.text = data.recovered
        deceasedTv.text = data.deaths
    }

    private fun bindPieData(pieData: StatewiseItem) {

        piechart.clearChart()

        piechart.addPieSlice(PieModel("Confirmed", pieData.confirmed!!.toFloat(), Color.parseColor("#FF0000")))
        piechart.addPieSlice(PieModel("Active", pieData.active!!.toFloat(), Color.parseColor("#f5c311")))
        piechart.addPieSlice(PieModel("Recovered", pieData.recovered!!.toFloat(), Color.parseColor("#76e846")))
        piechart.addPieSlice(PieModel("Deaths", pieData.deaths!!.toFloat(), Color.parseColor("#dbdbdb")))
        piechart.startAnimation()
    }

    fun getTimeAgo(past: Date): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

        return when {
            seconds < 60 -> {
                "Few seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hour ${minutes % 60} min ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
            }
        }
    }
}