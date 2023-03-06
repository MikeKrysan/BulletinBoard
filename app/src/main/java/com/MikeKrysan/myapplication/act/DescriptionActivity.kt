package com.MikeKrysan.myapplication.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.MikeKrysan.myapplication.databinding.ActivityDescriptionBinding

class DescriptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}