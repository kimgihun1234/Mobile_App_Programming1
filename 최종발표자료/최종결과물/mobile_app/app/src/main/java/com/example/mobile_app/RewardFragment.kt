package com.example.mobile_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mobile_app.databinding.FragmentInitialBinding
import com.example.mobile_app.databinding.FragmentRewardBinding

class RewardFragment : Fragment() {
    private var _binding: FragmentRewardBinding?=null
    private val binding get()=_binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentRewardBinding.inflate(inflater,container,false)
        val km = (activity as AccessDB).FindKmSum()
        val cntsum = (activity as AccessDB).FindCountSum()
        val idxsum = (activity as AccessDB).FindidxSum()
        val snsvalue = (activity as AccessDB).FindSnsValue()
        binding.start.setVisibility(View.VISIBLE)
        binding.startQuestion.setVisibility(View.INVISIBLE)
        if(km>=10){
            binding.expert.setVisibility(View.VISIBLE)
            binding.expertQuestion.setVisibility(View.INVISIBLE)
        }
        if(km>=5){
            binding.intermediate.setVisibility(View.VISIBLE)
            binding.intermediateQuestion.setVisibility(View.INVISIBLE)
        }
        if(km>=3){
            binding.beginner.setVisibility(View.VISIBLE)
            binding.beginnerQuestion.setVisibility(View.INVISIBLE)
        }
        if(snsvalue==1){
            binding.sns.setVisibility(View.VISIBLE)
            binding.snsQuestion.setVisibility(View.INVISIBLE)
        }
        if(cntsum>=50000){
            binding.fivewalk.setVisibility(View.VISIBLE)
            binding.fivewalkQuestion.setVisibility(View.INVISIBLE)
        }
        if(cntsum>=100000){
            binding.tenwalk.setVisibility(View.VISIBLE)
            binding.tenwalkQuestion.setVisibility(View.INVISIBLE)
        }
        if(idxsum>=15){
            binding.honor.setVisibility(View.VISIBLE)
            binding.honorQuestion.setVisibility(View.INVISIBLE)
        }
        return binding.root
    }

}