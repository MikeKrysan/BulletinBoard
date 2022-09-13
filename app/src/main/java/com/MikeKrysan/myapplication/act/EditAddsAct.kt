package com.MikeKrysan.myapplication.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.databinding.ActivityEditAddsBinding
import com.MikeKrysan.myapplication.dialogs.DialogSpinnerHelper
import com.MikeKrysan.myapplication.utils.CityHelper
import com.MikeKrysan.myapplication.utils.CityHelper.getAllCountries

class EditAddsAct : AppCompatActivity() {
    lateinit var rootElementForEditAddsAct:ActivityEditAddsBinding  //14.9 делаем rootElement public
    private val dialog = DialogSpinnerHelper() //14.2 Создаем диалог на уровне класса

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElementForEditAddsAct = ActivityEditAddsBinding.inflate(layoutInflater)
        setContentView(rootElementForEditAddsAct.root)

        init()  //14.1

//        val listCountry = CityHelper.getAllCountries(this)  //13.1

//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, CityHelper.getAllCountries(this))    //12.4 Адаптер создан
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)      //12.5 Теперь адаптер нужно прикрепить к нашему спиннеру. И еще здесь нужно указать setDropDownViewResource (установить ресурс раскрывающегося списка)
//        rootElementForEditAddsAct.spCountry.adapter = adapter//Берем спиннер из рут-элемента

//        val dialog  = DialogSpinnerHelper()     //13.4 Создаем объект класса DialogSpinnerHelper
//        dialog.showSpinnerDialog(this, listCountry) //Окно появилось, но нет поиска и списка, потому что нет адаптера, и никакой recyclerView к адаптеру не подключен

    }

    private fun init() {    //14.1

    }

    //OnClicks
    fun onClickSelectCountry(view: View) {  //14.5 Есть слушатель нажатий, но он пока ни к чему не подключен. Чтобы его подключить, в активити на textView находим onClick и назначаем ему только что созданый onClickSelectCountry
        val listCountry = CityHelper.getAllCountries(this)  //13.1
        dialog.showSpinnerDialog(this, listCountry)
    }
}