package com.MikeKrysan.myapplication.act

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.databinding.ActivityFilterBinding
import com.MikeKrysan.myapplication.dialogs.DialogSpinnerHelper
import com.MikeKrysan.myapplication.utils.CityHelper

class FilterActivity : AppCompatActivity() {
    lateinit var binding: ActivityFilterBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBarSettings()
        onClickSelectCountry()
        onClickSelectCity()
        onClickDone()
        getFilter()
    }

    //Для того, чтобы активити закрывалось, когда мы жмем на стрелку назад, пишем функцию:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)  finish() //берем из пакета android, потому что это не наша кнопка, она уже прописана
        return super.onOptionsItemSelected(item)
    }

    //Создадим функцию, которая будет ждать результата
    private fun getFilter() = with(binding) {
        val filter = intent.getStringExtra(FILTER_KEY)
        //вот таким образом проверяем фильтр:
        if(filter != null && filter != "empty") {
            //Если есть данный входящие, нужно их разделить на массив
            val filterArray = filter.split("_") //разделяем по нижнему подчеркиванию на отдельные элементы из массива. Превращаем в массив, все элементы находятся на своих позициях
            //но на некоторых позициях может быть слово "empty", то ничего заполнять не нужно. Если в стране и городе не значение по-умолчанию, то берем заполненные страну и город
//            if(filterArray[0] != getString(R.string.select_country) && filterArray[0]!= "empty") tvCountry.text = filterArray[0]
//            if(filterArray[1] != getString(R.string.select_city) && filterArray[1] != "empty") tvCity.text = filterArray[1]
            if(filterArray[0] != "empty") tvCountry.text = filterArray[0]
            if(filterArray[1] != "empty") tvCity.text = filterArray[1]
            if(filterArray[2] != "empty") edIndex.setText(filterArray[2])
            checkBoxWithSend.isChecked = filterArray[3].toBoolean()
        }
    }

    private fun onClickSelectCountry() = with(binding) {
        //лучше не пользоваться onClick через xml-файл:
        tvCountry.setOnClickListener{
            val listCountry = CityHelper.getAllCountries(this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity, listCountry, tvCountry)
            if(binding.tvCity.text.toString() != getString(R.string.select_city)) {
                tvCity.text = getString(R.string.select_city)
            }
        }
    }

    private fun onClickSelectCity() = with(binding) {
        tvCity.setOnClickListener {
            val selectedCountry = tvCountry.text.toString()
            if(selectedCountry != getString(R.string.select_country)){
                val listCity = CityHelper.getAllCities(selectedCountry, this@FilterActivity)
                dialog.showSpinnerDialog(this@FilterActivity, listCity, tvCity)
            }
            else {
                Toast.makeText(this@FilterActivity, R.string.country_not_selected, Toast.LENGTH_LONG).show()
            }
        }
    }

    //Функция для кнопки "Применить фильтр"
    private fun onClickDone() = with(binding) {
       btDone.setOnClickListener {
//            Log.d("MyLog", "Filter: ${createFilter()}")
           //Создаем Intent при нажатии на кнопку "Применить фильтр" чтобы передать данные на MainActivity
           val i = Intent().apply {
               putExtra(FILTER_KEY, createFilter())
           }
           setResult(RESULT_OK, i)  //возвращаем результат
           finish()
       }
    }

    //Функция, в которой будут собираться все данные вместе, для отправки на филтровку. В один специальный стринг. С помощью этого стринга мы и будем фильтровать
    private fun createFilter(): String = with(binding) {
        //создаем StringBuilder - специальный класс который будет собирать вместе объекты String
        val sBuilder = StringBuilder()
        //создадим массив и туда поместим все данные: страна, город, индекс, с отправкой или нет. С помощью цикла будем перебирать массив и смотреть, где пусто, где не выбрано поле
        val arrayTempFilter = listOf(tvCountry.text, tvCity.text, edIndex.text, checkBoxWithSend.isChecked.toString())   //в данной строке массива порядок очень важен!
        //в цикле я хочу получать и элементы и индексы даного элемента, чтобы в последнем элементе не добавлять нижнее подчеркивание
        for((i, s) in arrayTempFilter.withIndex()) {
            if(s != getString(R.string.select_country)  && s != getString(R.string.select_city) && s.isNotEmpty()) {   //мы не можем добавлять в список значение по-умолчанию "выберите страну". Мы фильтруем по конкретной стране
                sBuilder.append(s)   //добавляем значение s
                if(i != arrayTempFilter.size - 1) sBuilder.append("_")
                //если будет пусто, то запишем  empty, чтобы потом,когда выйдем из фильтра на mainActivity, а потом вернемся, чтобы у нас были сохраненные данные поиска. И чтобы не было пустых мест в массиве, чтобы он был всегда одного размера
            } else {
                sBuilder.append("empty")
                if(i != arrayTempFilter.size - 1) sBuilder.append("_")
            }
        }
        return sBuilder.toString()  //возвращает готовый собранный стринг
    }

    //Чтобы активировать стрелку выхода назад создадим функцию:
    fun actionBarSettings() {
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        const val FILTER_KEY = "filter_key"
    }

}