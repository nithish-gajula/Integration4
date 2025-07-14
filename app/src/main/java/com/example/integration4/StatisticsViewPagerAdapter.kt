package com.example.integration4

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class StatisticsViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyExpensesChartsFragment()
            1 -> RoomExpensesChartsFragment()
            else -> MyExpensesChartsFragment()
        }
    }
}