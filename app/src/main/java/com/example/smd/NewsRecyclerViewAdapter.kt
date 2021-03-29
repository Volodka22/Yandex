package com.example.smd

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NewsRecyclerViewAdapter(context: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<NewsRecyclerViewAdapter.MyViewHolder>() {

    private val allNews = ArrayList<News>()
    private val sup = context

    inner class MyViewHolder internal constructor(view: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        internal val headlineView: TextView = view.findViewById(R.id.headline)
        internal val imgView: ImageView = view.findViewById(R.id.img)
        internal val sourceView: TextView = view.findViewById(R.id.source)
        internal val summaryView: TextView = view.findViewById(R.id.summary)
        internal val goTo: MaterialButton = view.findViewById(R.id.go_to)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.news_item, parent, false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = allNews[position]
        holder.summaryView.text = news.summary
        holder.headlineView.text = news.headline
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = Date(news.datetime * 1000)

        val source = "From ${news.source} ${sdf.format(date)}"
        holder.sourceView.text = source

        if (news.image.isNotEmpty()) {
            holder.imgView.visibility = View.VISIBLE
            Picasso.get().load(news.image).into(holder.imgView)
        } else {
            holder.imgView.visibility = View.GONE
        }

        holder.goTo.setOnClickListener {
            val uri: Uri =
                Uri.parse(news.url) // missing 'http://' will cause crashed

            val intent = Intent(Intent.ACTION_VIEW, uri)
            sup.startActivity(intent)
        }
    }

    fun add(news: News) {
        allNews.add(news)
        notifyItemInserted(allNews.size - 1)
        notifyDataSetChanged()
    }


    override fun getItemCount() = allNews.size
}