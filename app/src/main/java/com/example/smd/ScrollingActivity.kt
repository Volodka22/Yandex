package com.example.smd

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scrolling.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Time
import kotlin.collections.ArrayList


class ScrollingActivity : AppCompatActivity() {

    private var isFavourite = false
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private val listOfCompany = ArrayList<String>()
    private val listOfCompanyProfile = ArrayList<CompanyProfile>()

    private val token = "c0v61kf48v6pr2p76380"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }


        val baseUrl = "https://finnhub.io/api/v1/index/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CompanyService::class.java)

        val call = service.getCompanyProfilesList("^DJI", token)
        call.enqueue(object : Callback<CompanyResponse> {
            override fun onResponse(
                call: Call<CompanyResponse>,
                response: Response<CompanyResponse>
            ) {
                if (response.isSuccessful) {
                    val companyResponse = response.body()!!
                    for (company in companyResponse.companyArr) {
                        listOfCompany.add(company)
                        val baseUrl2 = "https://finnhub.io/api/v1/stock/"
                        val retrofit2 = Retrofit.Builder()
                            .baseUrl(baseUrl2)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()


                        val service2 = retrofit2.create(CompanyProfileService::class.java)

                        Log.d("anime", listOfCompany.size.toString())

                        val called = service2.getCompanyProfile(company, token)
                        called.enqueue(object : Callback<CompanyProfile> {
                            override fun onResponse(
                                call: Call<CompanyProfile>,
                                response: Response<CompanyProfile>
                            ) {


                                if (response.isSuccessful) {
                                    val companyProfileResponse = response.body()!!
                                    val baseUrl3 = "https://finnhub.io/api/v1/stock/"
                                    val retrofit3 = Retrofit.Builder()
                                        .baseUrl(baseUrl3)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()


                                    val service3 = retrofit3.create(CompanyCandleService::class.java)

                                    val curTime = System.currentTimeMillis()/1000

                                    Log.e("sdfssdsaas", curTime.toString())

                                    val called1 = service3.getCompanyCandleList(company, token, "D", (curTime - 60*60*24).toString(),
                                        curTime.toString())
                                    called1.enqueue(object : Callback<CompanyCandleResponse> {
                                        override fun onResponse(
                                            call: Call<CompanyCandleResponse>,
                                            response: Response<CompanyCandleResponse>
                                        ) {


                                            if (response.isSuccessful) {
                                                val response1 = response.body()!!
                                                companyProfileResponse.marketCapitalize = response1.candleList[0]
                                                companyProfileResponse.changeM = response1.candleList[1] - response1.candleList[0]
                                                companyProfileResponse.changeP = companyProfileResponse.changeM / response1.candleList[0] * 100
                                                Log.e("asdsad", companyProfileResponse.changeP.toString())
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<CompanyCandleResponse>,
                                            t: Throwable
                                        ) {
                                            Log.e("Trouble with connect", t.toString())
//                                TODO("Snackbar")
                                        }
                                    })
                                }
                            }

                            override fun onFailure(
                                call: Call<CompanyProfile>,
                                t: Throwable
                            ) {
                                Log.e("Trouble with connect", t.toString())
//                                TODO("Snackbar")
                            }
                        })

                    }
                }
            }

            override fun onFailure(call: Call<CompanyResponse>, t: Throwable) {
                Log.e("Trouble with connect", t.toString())
                TODO("Snackbar")
            }
        })



        favorite.setOnClickListener {
            favoriteLayout.visibility = View.GONE
            stocksLayout.visibility = View.VISIBLE
            isFavourite = false
        }

        stocks.setOnClickListener {
            stocksLayout.visibility = View.GONE
            favoriteLayout.visibility = View.VISIBLE
            isFavourite = true
        }

    }

    private fun setRecyclerView() {
        /*viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.applicationContext)

        viewAdapter = StocksRecyclerViewAdapter(a.toTypedArray())

        recyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)
                .apply {

                    setHasFixedSize(false)

                    layoutManager = viewManager

                    adapter = viewAdapter

                } */
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuInflater.inflate(R.menu.menu_scrolling, menu)
        // menuInflater.inflate(R.menu.search_menu, menu);
        return true
    }

}
