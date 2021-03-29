package com.example.smd

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.smd.ui.main.NewsFragment
import com.example.smd.ui.main.PlotFragment
import com.example.smd.ui.main.SectionsPagerAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_stocks.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class StocksActivity : FragmentActivity() {

    private var cor: Job? = null
    private var listOfNews = ArrayList<News>()
    private var listOfCandle = ArrayList<Double>()
    private lateinit var title: String
    private lateinit var ticker: String
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val okHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stocks)
        title = intent.getStringExtra("name")
        ticker = intent.getStringExtra("ticker")
        name.text = title
        val sectionsPagerAdapter = SectionsPagerAdapter(this, title)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 2
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = this.resources.getStringArray(R.array.tabTitles)[position]
        }.attach()
        cor = CoroutineScope(Dispatchers.Main).launch {
            listOfNews = try {
                getListOfNews()
            } catch (e: retrofit2.HttpException) {
                wait()
                getListOfNews()
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                wifiError()
                ArrayList()
            }
            val newsFragment =
                supportFragmentManager.fragments[0] as NewsFragment
            newsFragment.addNews(listOfNews)


            getListOfPrice()

            val dates = ArrayList<String>()

            for (i in 0 until listOfCandle.size) {
                dates.add(sdf.format(System.currentTimeMillis() - i * 24L * 60 * 60 * 1000))
                Log.d("time", sdf.format(System.currentTimeMillis() - i * 24L * 60 * 60 * 1000))
            }

            dates.reverse()

            val plotFragment =
                supportFragmentManager.fragments[1] as PlotFragment

            plotFragment.createPlot(dates.toTypedArray(), listOfCandle.toTypedArray())


        }
    }

    private suspend fun getListOfPrice() {
        val baseUrl = "https://finnhub.io/api/v1/stock/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service =
            retrofit.create(CompanyCandleService::class.java)

        val curTime = System.currentTimeMillis() / 1000

        val response = try {
            service.getCompanyCandleListAsync(
                ticker,
                getString(R.string.token),
                "D",
                // mul 7 for weekend
                (curTime - 60 * 60 * 24 * 500).toString(),
                curTime.toString()
            )
        } catch (e: retrofit2.HttpException) {
            e.printStackTrace()
            wait()
            service.getCompanyCandleListAsync(
                ticker,
                getString(R.string.token),
                "D",
                // mul 7 for weekend
                (curTime - 60 * 60 * 24 * 500).toString(),
                curTime.toString()
            )
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            wifiError()
            Log.e("size", listOfCandle.size.toString())
            CompanyCandleResponse()
        }

        listOfCandle = response.candleList
    }

    private suspend fun getListOfNews(): ArrayList<News> {
        val baseUrl = "https://finnhub.io/api/v1/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val service = retrofit.create(NewsService::class.java)
        val currentDate = sdf.format(Date())
        val yearAgo = sdf.format(Date(sdf.parse(currentDate).time - 365L * 24 * 60 * 60 * 1000))
        Log.d("curD", yearAgo)
        return service.getCompanyCandleListAsync(
            ticker,
            resources.getString(R.string.token),
            yearAgo,
            currentDate
        )
    }

    private suspend fun wait() {
        zhdun.visibility = View.VISIBLE
        app_bar.visibility = View.GONE
        delay(1000 * 60)
        zhdun.visibility = View.GONE
        app_bar.visibility = View.VISIBLE
    }

    private fun wifiError() {
        val snackBar = Snackbar.make(
            this,
            stockView,
            "Trouble with internet connection",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("Restart") {
            this.recreate()
        }
        snackBar.show()
    }

    override fun onDestroy() {
        cor?.cancel()
        super.onDestroy()
    }

}