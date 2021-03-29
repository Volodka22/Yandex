package com.example.smd.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.smd.R


class SectionsPagerAdapter(fragmentActivity: FragmentActivity, ticker: String) :
    FragmentStateAdapter(fragmentActivity) {
    private val localName = ticker
    private val sup = fragmentActivity

    override fun getItemCount(): Int = sup.resources.getStringArray(R.array.tabTitles).size

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> NewsFragment()
            1 -> PlotFragment()
            else -> throw RuntimeException()
        }
        fragment.arguments = Bundle().apply {
            putString("ticker", localName)
        }
        return fragment
    }


}