package com.MikeKrysan.myapplication.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.databinding.ActivityEditAddsBinding
import com.MikeKrysan.myapplication.utils.CityHelper

class EditAddsAct : AppCompatActivity() {
    private lateinit var rootElementForEditAddsAct:ActivityEditAddsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElementForEditAddsAct = ActivityEditAddsBinding.inflate(layoutInflater)
        setContentView(rootElementForEditAddsAct.root)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, CityHelper.getAllCountries(this))    //12.4 Адаптер создан
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)      //12.5 Теперь адаптер нужно прикрепить к нашему спиннеру. И еще здесь нужно указать setDropDownViewResource (установить ресурс раскрывающегося списка)
        rootElementForEditAddsAct.spCountry.adapter = adapter//Берем спиннер из рут-элемента
    }
}