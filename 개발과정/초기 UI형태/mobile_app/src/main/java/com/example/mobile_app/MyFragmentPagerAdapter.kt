package com.example.mobile_app

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class MyFragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    val fragments: List<Fragment>
    init{
        fragments = listOf(RewardFragment(),InitialFragment(),RecordFragment())
        Log.d("kkang","fragments size: ${fragments.size}")
    }
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment =fragments[position]
}
