package com.MikeKrysan.myapplication.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.databinding.ActivityFilterBinding

class FilterActivity : AppCompatActivity() {
    lateinit var binding: ActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBarSettings()
    }

    //Для того, чтобы активити закрывалось, когда мы жмем на стрелку назад, пишем функцию:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)  finish() //берем из пакета android, потому что это не наша кнопка, она уже прописана
        return super.onOptionsItemSelected(item)
    }

    //Чтобы активировать стрелку выхода назад создадим функцию:
    fun actionBarSettings() {
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }
}