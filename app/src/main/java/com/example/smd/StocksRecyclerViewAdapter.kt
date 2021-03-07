package com.example.smd

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

class StocksRecyclerViewAdapter(
    stocks: ArrayList<CompanyProfile>,
    context: Context
) :
    androidx.recyclerview.widget.RecyclerView.Adapter<StocksRecyclerViewAdapter.MyViewHolder>() {

    private var curStocks = stocks
    private val allStocks = ArrayList(stocks)
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
            Picasso.get().load(stock.logo).into(holder.logoView)
        }
        setStar(holder, position)
        holder.isFavorite.setOnClickListener {
            val pos = allStocks.indexOf(curStocks[position])
            allStocks[pos].isFavourite = !allStocks[pos].isFavourite
            curStocks[position] = allStocks[pos]
            setStar(holder, position)
            if (ScrollingActivity.getIsFavourite() && !allStocks[pos].isFavourite) {
                delete(position)
            }
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

    fun filter(text: String, isFavourite: Boolean) {
        curStocks.clear()
        if (text.isEmpty()) {
            allStocks.forEach {
                if (checkOnFavourite(isFavourite, it)) {
                    curStocks.add(it)
                }
            }
        } else {
            allStocks.forEach {
                if ((checkIntro(it.name, text) || checkIntro(
                        it.ticker,
                        text
                    )) && (((!isFavourite) || it.isFavourite))
                ) {
                    curStocks.add(it)
                }
            }
        }
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = curStocks.size


}