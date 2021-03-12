package com.example.smd

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList


class ScrollingActivity : AppCompatActivity() {


    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private var listOfCompany = ArrayList<String>()
    private val listOfCompanyProfile = ArrayList<CompanyProfile>()

    private val token = "c15osb748v6rh9qkjsqg"

    companion object {
        private var isFavourite = false
        fun getIsFavourite(): Boolean {
            return isFavourite
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }

        setRecyclerView()

        searchInit()

        favorite.setOnClickListener {
            favoriteLayout.visibility = View.GONE
            stocksLayout.visibility = View.VISIBLE
            isFavourite = true
            (viewAdapter as StocksRecyclerViewAdapter).filter(
                searchView.query.toString(),
                isFavourite
            )
        }

        stocks.setOnClickListener {
            stocksLayout.visibility = View.GONE
            favoriteLayout.visibility = View.VISIBLE
            isFavourite = false
            (viewAdapter as StocksRecyclerViewAdapter).filter(
                searchView.query.toString(),
                isFavourite
            )
        }

        CoroutineScope(Dispatchers.Main).launch {
            listOfCompany = getListOfCompany().companyArr
            Log.d("list of company", listOfCompany.toString())
            getCompanyProfile(listOfCompany)
            addCandles()
        }

    }


    private suspend fun getListOfCompany(): CompanyResponse {
        val baseUrl = "https://finnhub.io/api/v1/index/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CompanyService::class.java)

        return service.getCompanyProfilesListAsync("^DJI", token)
    }

    private suspend fun getCompanyProfile(listOfCompany: ArrayList<String>): ArrayList<CompanyProfile> {
        Log.d("gg", listOfCompany.toString())
        val baseUrl = "https://finnhub.io/api/v1/stock/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(CompanyProfileService::class.java)

        for (comp in listOfCompany) {
            delay(300)
            val res = service.getCompanyProfileAsync(comp, token)
            listOfCompanyProfile.add(res)
        }
        Log.d("volodya", listOfCompanyProfile.size.toString())
        return listOfCompanyProfile
    }

    private suspend fun addCandles() {
        val baseUrl = "https://finnhub.io/api/v1/stock/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service =
            retrofit.create(CompanyCandleService::class.java)

        val curTime = System.currentTimeMillis() / 1000


        for (i in 0 until listOfCompanyProfile.size) {
            delay(300)
            val response = service.getCompanyCandleListAsync(
                listOfCompanyProfile[i].ticker,
                token,
                "D",
                // mul 7 for weekend
                (curTime - 60 * 60 * 24 * 7).toString(),
                curTime.toString()
            )
            val penultimate = response.candleList[response.candleList.size - 2]
            listOfCompanyProfile[i].price =
                response.candleList.last()
            listOfCompanyProfile[i].changeM =
                listOfCompanyProfile[i].price - penultimate
            listOfCompanyProfile[i].changeP =
                listOfCompanyProfile[i].changeM / penultimate * 100

            (viewAdapter as StocksRecyclerViewAdapter).add(
                listOfCompanyProfile[i],
                isFavourite,
                searchView.query.toString()
            )

            Log.d("i", i.toString())
        }
        Log.d("sizeArray", listOfCompanyProfile.size.toString())


    }


    private fun searchInit() {

        searchView.setOnClickListener {
            (viewAdapter as StocksRecyclerViewAdapter).filter(
                searchView.query.toString(),
                isFavourite
            )
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                (viewAdapter as StocksRecyclerViewAdapter).filter(query, isFavourite)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                (viewAdapter as StocksRecyclerViewAdapter).filter(newText, isFavourite)
                Log.d("chg", "opppa")
                return false
            }
        })
    }

    private fun setRecyclerView() {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.applicationContext)

        viewAdapter = StocksRecyclerViewAdapter(this.applicationContext)

        recyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
                .apply {

                    setHasFixedSize(false)

                    layoutManager = viewManager

                    adapter = viewAdapter

                }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

}
