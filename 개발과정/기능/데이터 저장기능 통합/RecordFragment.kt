package com.example.mobile_app

import android.app.Activity
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


var hash = HashMap<String, Int>()

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

        (activity as AccessDB).insert("2022-11-25", 1800)
        (activity as AccessDB).insert("2022-11-26", 3500)
        (activity as AccessDB).insert("2022-11-27", 6700)
        (activity as AccessDB).insert("2022-11-28", 8000)
        (activity as AccessDB).insert("2022-11-29", 9000)
        (activity as AccessDB).insert("2022-11-30", 3000)
        (activity as AccessDB).insert("2022-12-09", 4500)
        (activity as AccessDB).inputHash(hash)
        binding.starttext.text=day
        binding.endtext.text=day
        binding.walktxt.text= hash.get(day).toString()

        //날짜 클릭해서 받는 부분
        binding.btn.setOnClickListener {
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
                    var sum = 0
                    val maxDate = Calendar.getInstance()
                    maxDate.set(2022,12,17);
                    calendar.timeInMillis = selection?.first ?: 0
                    startDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time).toString()
                    binding.starttext.text = startDate
                    calendar.timeInMillis = selection?.second ?: 0
                    endDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time).toString()
                    binding.endtext.text=endDate
                    sum = walkadd(startDate,endDate)
                    binding.walktxt.text=sum.toString()
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
}