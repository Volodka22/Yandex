package com.example.smd

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.UnknownHostException


class ScrollingActivity : AppCompatActivity() {
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private var listOfCompany = ArrayList<String>()
    private val listOfCompanyProfile = ArrayList<CompanyProfile>()
    private val favourites = kotlin.collections.HashMap<String, Boolean>()
    private var allRead = false
    private var cor: Job? = null
    private var act = this

    companion object {
        private var isFavourite = false
        fun getIsFavourite(): Boolean {
            return isFavourite
        }
    }

    override fun onPause() {
        if (allRead) {
            (viewAdapter as StocksRecyclerViewAdapter).writeFavourites()
        }
        super.onPause()
    }

    override fun onDestroy() {
        cor?.cancel()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        zhdun.visibility = View.GONE
        supportActionBar!!.title = null
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.WHITE

        readFavourites()

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

        cor = CoroutineScope(Dispatchers.Main).launch {
            listOfCompany = try {
                getListOfCompany().companyArr
            } catch (e: retrofit2.HttpException) {
                wait()
                getListOfCompany().companyArr
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                wifiError()
                ArrayList()
            }
            Log.d("list of company", listOfCompany.toString())
            getCompanyProfile(listOfCompany)
            addCandles()
        }

    }

    private fun readFavourites() {
        val file = File(applicationContext.filesDir, getString(R.string.file_with_favourites))
        if (file.canRead()) {
            file.forEachLine {
                favourites[it] = true
            }
        }
        allRead = true
    }


    private suspend fun getListOfCompany(): CompanyResponse {
        val baseUrl = "https://finnhub.io/api/v1/index/"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CompanyService::class.java)

        return service.getCompanyProfilesListAsync("^DJI", getString(R.string.token))
    }

    private suspend fun wait() {
        zhdun.visibility = View.VISIBLE
        delay(1000 * 60)
        zhdun.visibility = View.GONE
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
            try {
                val res = service.getCompanyProfileAsync(comp, getString(R.string.token))
                listOfCompanyProfile.add(res)
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                wait()
                val res = service.getCompanyProfileAsync(comp, getString(R.string.token))
                listOfCompanyProfile.add(res)
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                wifiError()
            }

        }
        Log.d("volodya", listOfCompanyProfile.size.toString())
        return listOfCompanyProfile
    }

    private fun wifiError() {
        val snackbar = Snackbar.make(
            act,
            contextView,
            "Trouble with internet connection",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Restart") {
            act.recreate()
        }
        snackbar.show()
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
            val response = try {
                service.getCompanyCandleListAsync(
                    listOfCompanyProfile[i].ticker,
                    getString(R.string.token),
                    "D",
                    // mul 7 for weekend
                    (curTime - 60 * 60 * 24 * 7).toString(),
                    curTime.toString()
                )
            } catch (e: retrofit2.HttpException) {
                e.printStackTrace()
                wait()
                service.getCompanyCandleListAsync(
                    listOfCompanyProfile[i].ticker,
                    getString(R.string.token),
                    "D",
                    // mul 7 for weekend
                    (curTime - 60 * 60 * 24 * 7).toString(),
                    curTime.toString()
                )
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                wifiError()
                null
            } ?: break
            val penultimate = response.candleList[response.candleList.size - 2]
            listOfCompanyProfile[i].price =
                response.candleList.last()

            listOfCompanyProfile[i].changeM =
                listOfCompanyProfile[i].price - penultimate

            listOfCompanyProfile[i].changeP =
                listOfCompanyProfile[i].changeM / penultimate * 100

            listOfCompanyProfile[i].isFavourite =
                favourites[listOfCompanyProfile[i].ticker] == true
            Log.d("i", i.toString())
            (viewAdapter as StocksRecyclerViewAdapter).add(
                listOfCompanyProfile[i],
                isFavourite,
                searchView.query.toString()
            )
        }
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
