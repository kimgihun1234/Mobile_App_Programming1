package com.example.mobile_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import com.example.mobile_app.databinding.FragmentInitialBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.NonDisposableHandle.parent
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

val Mets = 3.5f
//운동강도 Mets = 3.5
// 느리게 걷기:2, 조금 빨리 걷기: 3~3.5, 매우빨리걷기: 6

class InitialFragment : Fragment() {
    private var _binding: FragmentInitialBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentInitialBinding.inflate(inflater,container,false)
        (activity as AccessDB).inputHash(hash)
        (activity as AccessDB).inputFloatHash(kmhash)
        var get_walk_data : Int ?= null
        get_walk_data = arguments?.getInt("key")
        if(get_walk_data==null){
            get_walk_data = 0
        }
        var arr = Array<Float>(7) { 0f }
        var initTime = 0L
        var pauseTime = 0L
        var tall : Int ?=null
        var weight : Int ?=null
        tall = arguments?.getInt("tall")
        if(tall==null){
            tall = 0
        }
        weight = arguments?.getInt("weight")
        if(weight==null){
            weight = 0
        }
        var button = 0
        binding.walktxt.text = get_walk_data.toString()
        val fmt = DecimalFormat("#.##")
        fmt.roundingMode = RoundingMode.HALF_UP
        val kmtxt = fmt.format(get_walk_data.toFloat()*(tall*0.45)/100000)
        val kcal= fmt.format(0.6*weight*kmtxt.toFloat())
        binding.kmtxt.text = kmtxt.toString()
        binding.kcaltxt.text = kcal.toString()
        binding.start.setOnClickListener {
            if(button==0){
                binding.start.text="stop"
                binding.start.isEnabled = true
                var isstart = "1"
                setFragmentResult("requestKey", bundleOf("bundleKey" to isstart))
                button=1
            }
            else{
                binding.start.text="start"
                binding.start.isEnabled = true
                var isstart = "0"
                setFragmentResult("requestKey", bundleOf("bundleKey" to isstart))
                button=0
            }
        }
        (activity as AccessDB).inputHash(hash)
        var startDate="0"
        var endDate="0"
        val cal = Calendar.getInstance()
        cal.time = Date()
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        endDate = df.format(cal.time)
        cal.add(Calendar.DATE, -7)
        startDate = df.format(cal.time)
        val c1 = Calendar.getInstance()
        val c2 = Calendar.getInstance()
        var temp = "0"
        c1.setTime( df.parse(startDate) )
        c2.setTime( df.parse(endDate) )
        var sum=0f
        for(i in 0..6) {
            if(i==0){
                temp = SimpleDateFormat("yyyy-MM-dd").format(c1.time).toString()
                if(hash.containsKey(temp)){
                    sum+=arr[i]
                }
                c1.add(Calendar.DATE, 1);
            } else {
                temp = SimpleDateFormat("yyyy-MM-dd").format(c1.time).toString()
                if (hash.containsKey(temp)) {
                    arr[i] = hash.getValue(temp).toFloat()
                    sum += arr[i]
                } else {
                    arr[i] = 0f
                }
                c1.add(Calendar.DATE, 1);
            }
        }
        binding.average.text = " ————————  주간 평균:${(sum/7).toInt()}  ————————"
        val entry_chart = ArrayList<BarEntry>() // 데이터를 담을 Arraylist
        for (i in 0..5) {
            entry_chart.add(BarEntry((i+1).toFloat(), arr[i+1]))
        }
        var barChart = binding.chart as BarChart
        val barData = BarData() // 차트에 담길 데이터

        val barDataSet =
            BarDataSet(entry_chart, " ") // 데이터가 담긴 Arraylist 를 BarDataSet 으로 변환한다.
        barDataSet.color = Color.rgb(199,177,153) // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
        barDataSet.valueTextSize=13f
        barData.addDataSet(barDataSet) // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.
        barChart!!.data = barData // 차트에 위의 DataSet 을 넣는다.
        barChart!!.invalidate() // 차트 업데이트
        barChart!!.setTouchEnabled(false) // 차트 터치 불가능하게
        barChart!!.setDrawGridBackground(false)
        barChart!!.setDrawBorders(false)
        barChart!!.axisLeft.setDrawGridLines(false)
        barChart!!.axisRight.setDrawGridLines(false)
        barChart!!.axisRight.isEnabled=false
        barChart!!.axisLeft.isEnabled=false
        barChart!!.xAxis.isEnabled=false
        barChart!!.xAxis.setDrawAxisLine(false)
        barChart!!.setMaxVisibleValueCount(7)
        barChart!!.description.isEnabled=false
        barChart!!.legend.isEnabled=false

        return binding.root
    }
}