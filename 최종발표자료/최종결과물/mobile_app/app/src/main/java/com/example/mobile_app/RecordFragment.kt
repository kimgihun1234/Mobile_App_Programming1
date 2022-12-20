package com.example.mobile_app

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import com.example.mobile_app.databinding.FragmentRecordBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


var hash = HashMap<String, Int>()
var kmhash = HashMap<String, Float>()

class RecordFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //초기화 해주는 부분
        val binding = FragmentRecordBinding.inflate(layoutInflater)
        val now = System.currentTimeMillis()
        val date = Date(now)
        val day = SimpleDateFormat("yyyy-MM-dd").format(date).toString()
        var rangedate = SimpleDateFormat("yyyyMMdd").format(date).toInt()
        binding.starttext.text=day
        binding.endtext.text=day
        binding.walktxt.text= "0"
        binding.kmtxt.text="0.00"

        //날짜 클릭해서 받는 부분
        binding.btn.setOnClickListener {
            (activity as AccessDB).inputHash(hash)
            (activity as AccessDB).inputFloatHash(kmhash)
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("검색 기간을 골라주세요")
                    .build()
            dateRangePicker.show(childFragmentManager, "date_picker")
            dateRangePicker.addOnPositiveButtonClickListener(object : MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> {
                override fun onPositiveButtonClick(selection: Pair<Long, Long>?) {
                    val calendar = Calendar.getInstance()
                    var startDate="0"
                    var endDate="0"
                    var startcheck=0
                    var endcheck =0
                    var sum = 0
                    var kmsum = 0.0f

                    calendar.timeInMillis = selection?.first ?: 0
                    startDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time).toString()
                    startcheck=SimpleDateFormat("yyyyMMdd").format(calendar.time).toInt()
                    calendar.timeInMillis = selection?.second ?: 0
                    endcheck = SimpleDateFormat("yyyyMMdd").format(calendar.time).toInt()
                    endDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time).toString()

                    if(startcheck <= rangedate && endcheck <= rangedate){
                        binding.starttext.text = startDate
                        binding.endtext.text=endDate
                        sum = walkadd(startDate,endDate)
                        kmsum = kmadd(startDate, endDate)
                        binding.walktxt.text=sum.toString()
                        binding.kmtxt.text=kmsum.toString()
                        binding.wtt.text="걸음"
                        binding.ktt.text="km"
                    }
                    else{
                        binding.starttext.text = ""
                        binding.endtext.text=""
                        binding.walktxt.text="잘못된 날짜를"
                        binding.kmtxt.text="입력하셨습니다"
                        binding.wtt.text=""
                        binding.ktt.text=""
                    }
                }
            })
        }
        return binding.root
    }
    fun walkadd(startdate: String, enddate : String): Int{
        var sum = 0
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val d1 = df.parse( startdate );
        val d2 = df.parse( enddate );
        val c1 = Calendar.getInstance();
        val c2 = Calendar.getInstance();
        var temp = "0"
        c1.setTime( d1 );
        c2.setTime( d2 );
        while( c1.compareTo( c2 ) !=1 ){
            temp = SimpleDateFormat("yyyy-MM-dd").format(c1.time).toString()
            if(hash.containsKey(temp)){
                sum+= hash.getValue(temp)
            }else{
                sum+=0
            }
            c1.add(Calendar.DATE, 1);
        }
        return sum
    }
    fun kmadd(startdate: String, enddate : String): Float{
        var sum = 0.0f
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val d1 = df.parse( startdate );
        val d2 = df.parse( enddate );
        val c1 = Calendar.getInstance();
        val c2 = Calendar.getInstance();
        var temp = "0"
        c1.setTime( d1 );
        c2.setTime( d2 );
        while( c1.compareTo( c2 ) !=1 ){
            temp = SimpleDateFormat("yyyy-MM-dd").format(c1.time).toString()
            if(kmhash.containsKey(temp)){
                sum+= kmhash.getValue(temp)
            }else{
                sum+=0
            }
            c1.add(Calendar.DATE, 1);
        }
        return sum
    }
}