package com.example.mobile_app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarEntry
import java.util.ArrayList

interface AccessDB {
    public fun insert(date:String, count:Int, km:Float)
    public fun inputHash(hash: HashMap<String, Int>)
    public fun FindKmSum():Float
    public fun inputFloatHash(hash: HashMap<String, Float>)
    public fun toast(message: String)
    public fun FindCountSum() : Int
    public fun FindSnsValue() : Int
    public fun FindidxSum() : Int
}