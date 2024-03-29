package com.example.smd

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round


class StocksRecyclerViewAdapter(
    context: Context
) :
    androidx.recyclerview.widget.RecyclerView.Adapter<StocksRecyclerViewAdapter.MyViewHolder>() {

    private val favourites = ArrayList<String>()
    private var curStocks = ArrayList<CompanyProfile>()
    private val allStocks = ArrayList<CompanyProfile>()
    private val sup = context

    inner class MyViewHolder internal constructor(view: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        internal val nameView: TextView = view.findViewById(R.id.name)
        internal val logoView: ImageView = view.findViewById(R.id.logo)
        internal val tickerView: TextView = view.findViewById(R.id.ticker)
        internal val priceView: TextView = view.findViewById(R.id.price)
        internal val isFavorite: ImageButton = view.findViewById(R.id.ifFavorite)
        internal val chgView: TextView = view.findViewById(R.id.chg)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.stocks_card, parent, false)
        return MyViewHolder(view)
    }


    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    private fun setStar(holder: MyViewHolder, position: Int) {
        val d = if (curStocks[position].isFavourite) R.drawable.is_favourite
        else R.drawable.is_not_favourite
        holder.isFavorite.setImageDrawable(
            ResourcesCompat.getDrawable(sup.resources, d, null)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stock = curStocks[position]
        holder.nameView.text = stock.name
        holder.tickerView.text = stock.ticker
        val price = "${stock.price} ${stock.currency}"
        holder.priceView.text = price
        var chg = ""
        if (stock.changeM > 0) {
            chg += "+"
            holder.chgView.setTextColor(Color.GREEN)
        } else {
            holder.chgView.setTextColor(Color.RED)
        }
        chg += ("${stock.changeM.round(2)} ${stock.currency} (${stock.changeP.round(2)}%)")
        holder.chgView.text = chg

        if (stock.logo.isNotEmpty()) {
            holder.logoView.visibility = View.VISIBLE
            Picasso.get().load(stock.logo).into(holder.logoView)
        } else {
            holder.logoView.visibility = View.INVISIBLE
        }
        setStar(holder, position)
        holder.isFavorite.setOnClickListener {
            val pos = allStocks.indexOf(curStocks[position])
            allStocks[pos].isFavourite = !allStocks[pos].isFavourite
            if (allStocks[pos].isFavourite) {
                favourites.add(allStocks[pos].ticker)
            } else {
                favourites.remove(allStocks[pos].ticker)
            }
            curStocks[position] = allStocks[pos]
            setStar(holder, position)
            if (ScrollingActivity.getIsFavourite() && !allStocks[pos].isFavourite) {
                delete(position)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(sup, StocksActivity::class.java).apply {
                putExtra("name", stock.name)
                putExtra("ticker", stock.ticker)
            }
            sup.startActivity(intent)
        }

    }

    private fun delete(pos: Int) {
        curStocks.removeAt(pos)
        notifyDataSetChanged()
    }

    private fun checkOnFavourite(isFavourite: Boolean, it: CompanyProfile): Boolean {
        return (!isFavourite) || it.isFavourite
    }

    private fun checkIntro(a: String, b: String): Boolean {
        return a.toLowerCase(Locale.getDefault())
            .contains(b.toLowerCase(Locale.getDefault()))
    }

    private fun checkProfile(isFavourite: Boolean, text: String, it: CompanyProfile): Boolean {
        var ans = checkOnFavourite(isFavourite, it)
        if (text.isNotEmpty()) {
            ans = ans && (checkIntro(it.name, text) || checkIntro(it.ticker, text))
        }
        return ans
    }

    fun add(comp: CompanyProfile, isFavourite: Boolean, text: String) {
        allStocks.add(comp)
        if (checkProfile(isFavourite, text, comp)) {
            curStocks.add(comp)
        }
        if (comp.isFavourite) {
            favourites.add(comp.ticker)
        }
        notifyDataSetChanged()
    }

    fun filter(text: String, isFavourite: Boolean) {
        curStocks.clear()

        allStocks.forEach {
            if (checkProfile(isFavourite, text, it)) {
                curStocks.add(it)
            }
        }
        notifyDataSetChanged()
    }

    fun writeFavourites() {
        var fileContent = ""
        for (comp in favourites) {
            fileContent += comp + "\n"
        }
        Log.e("Bye", fileContent)
        val file = File(sup.filesDir, sup.resources.getString(R.string.file_with_favourites))
        file.writeText(fileContent)
    }

    override fun getItemCount() = curStocks.size


}