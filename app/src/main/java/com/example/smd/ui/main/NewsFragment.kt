package com.example.smd.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smd.News
import com.example.smd.NewsRecyclerViewAdapter
import com.example.smd.R
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : Fragment() {

    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    fun addNews(news: ArrayList<News>) {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        viewAdapter = NewsRecyclerViewAdapter(requireContext())

        recyclerView =
            newsRecyclerView
                .apply {

                    setHasFixedSize(false)

                    layoutManager = viewManager

                    adapter = viewAdapter

                }
        for (i in news) {
            (viewAdapter as NewsRecyclerViewAdapter).add(i)
        }
    }
}