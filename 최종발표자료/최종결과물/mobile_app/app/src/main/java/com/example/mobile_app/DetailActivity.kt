package com.example.mobile_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobile_app.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.done.setOnClickListener {
            val intent=Intent(this,MainActivity::class.java)
            intent.putExtra("height",binding.height.text.toString())
            intent.putExtra("weight",binding.weight.text.toString())
            intent.putExtra("name",binding.name.text.toString())
            startActivity(intent)
            finish()
        }
    }
}