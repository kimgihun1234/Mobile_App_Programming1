package com.example.mobile_app

import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.example.mobile_app.databinding.FragmentInitialBinding


class InitialFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentInitialBinding.inflate(inflater,container,false)
        var get_walk_data = 0
        var initTime = 0L
        var pauseTime = 0L
        var tall = 170
        var button = 0
        binding.kmtxt.text = (get_walk_data*(tall*0.37)).toString()
        binding.start.setOnClickListener {
            if(button==0){
                binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
                binding.chronometer.start()
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
}