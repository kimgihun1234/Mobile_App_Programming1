package com.example.myapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import com.example.myapp.databinding.FragmentInitBinding
import com.example.myapp.databinding.FragmentRewardBinding


class RewardFragment : Fragment() {

    lateinit var binding:FragmentRewardBinding
    var first=0
    var totaltime=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentRewardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var totaldis:Int=0  //총 거리
        setFragmentResultListener("distancekey") { key, bundle ->
            bundle.getInt("distance")?.let { value ->
                totaldis+=value
            }
        }
        setFragmentResultListener("firstkey") { key, bundle ->
            bundle.getInt("first")?.let { value ->
                first+=value
            }
        }
        setFragmentResultListener("timekey") { key, bundle ->
            bundle.getInt("time")?.let { value ->
                totaltime+=value
            }
        }
        if(totaldis>=3000){
            binding.beginner2.visibility=View.VISIBLE
        }
        if (totaldis>=5000){
            binding.intermediate2.visibility=View.VISIBLE
        }
        if(totaldis>=10000){
            binding.expert2.visibility=View.VISIBLE
        }
        if(first>=1){
            binding.start2.visibility=View.VISIBLE
        }
        if(totaltime>=60){
            binding.run1hour2.visibility=View.VISIBLE
        }
        if(totaltime>=120){
            binding.run2hour2.visibility=View.VISIBLE
        }
    }

}