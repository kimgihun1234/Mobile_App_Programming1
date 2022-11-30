package com.example.myapp

import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import com.example.myapp.databinding.FragmentInitBinding


class InitialFragment : Fragment() {

    lateinit var binding: FragmentInitBinding

    var get_walk_data = 0
    var initTime = 0L
    var pauseTime = 0L
    var tall = 170
    var button = 0
    var first = 1 //첫 운동 수행
    var totaltime=0L //총 누적 시간
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInitBinding.inflate(inflater,container,false)


        binding.kmtxt.text = (get_walk_data*(tall*0.37)).toString()
        binding.start.setOnClickListener {
            if(button==0){
                binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
                binding.chronometer.start()
                totaltime += SystemClock.elapsedRealtime() + pauseTime
                binding.walktxt.text=get_walk_data.toString()
                binding.start.text="stop"
                binding.start.isEnabled = true
                button=1
            }
            else{
                pauseTime = binding.chronometer.base - SystemClock.elapsedRealtime()
                binding.start.text="start"
                binding.chronometer.stop()
                binding.start.isEnabled = true
                button=0
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val bundle = bundleOf("distance" to get_walk_data*(tall*0.37))
            setFragmentResult("distancekey", bundle)
            val first = bundleOf("first" to first)
            setFragmentResult("firstkey",first)
            val totaltime = bundleOf("time" to totaltime)
            setFragmentResult("timekey",totaltime)
        }
    }

}