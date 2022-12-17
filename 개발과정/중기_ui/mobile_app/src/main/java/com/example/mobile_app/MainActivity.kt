package com.example.mobile_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mobile_app.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    class MyFragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
        val fragments: List<Fragment>
        init {
            fragments= listOf(InitialFragment(), RecordFragment(),RewardFragment())
        }
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = MyFragmentPagerAdapter(this)
        binding.viewpager.adapter = adapter
        val tabLayout: TabLayout = binding.tabs
        val tab1: TabLayout.Tab = tabLayout.newTab()
        tab1.text="오늘"
        tabLayout.addTab(tab1)
        val tab2: TabLayout.Tab = tabLayout.newTab()
        tab2.text="기록"
        tabLayout.addTab(tab2)
        val tab3: TabLayout.Tab = tabLayout.newTab()
        tab3.text="보상 뱃지"
        tabLayout.addTab(tab3)
        TabLayoutMediator(binding.tabs, binding.viewpager){ tab, position ->
            if(position==0){
                tab.text = "오늘"
            }else if(position==1){
                tab.text = "기록"
            }
            else{
                tab.text = "보상 뱃지"
            }
        }.attach()
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()
                when(tab?.text){
                    "오늘"->binding.collasingtoolbar.title="오늘"
                    "기록"->binding.collasingtoolbar.title="기록"
                    "보상 뱃지"->binding.collasingtoolbar.title="보상 뱃지"
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}