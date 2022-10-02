package com.MikeKrysan.myapplication.act

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.databinding.ActivityEditAddsBinding
import com.MikeKrysan.myapplication.dialogs.DialogSpinnerHelper
import com.MikeKrysan.myapplication.utils.CityHelper
import com.MikeKrysan.myapplication.utils.ImagePicker
import com.fxn.utility.PermUtil

class EditAddsAct : AppCompatActivity() {
    lateinit var rootElementForEditAddsAct:ActivityEditAddsBinding  //14.9 делаем rootElement public
    private val dialog = DialogSpinnerHelper() //14.2 Создаем диалог на уровне класса
    private var isImagesPermissionGranted = false   //16.3.1

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)     //16.3
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
                //If request is cancelled, the result arrays are empty.
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.getImages(this)
                } else {
                    isImagesPermissionGranted = false
                    Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    //OnClicks
    fun onClickSelectCountry(view: View) {  //14.5 Есть слушатель нажатий, но он пока ни к чему не подключен. Чтобы его подключить, в активити на textView находим onClick и назначаем ему только что созданый onClickSelectCountry
        val listCountry = CityHelper.getAllCountries(this)  //13.1
        dialog.showSpinnerDialog(this, listCountry, rootElementForEditAddsAct.tvCountry) //15.5.1
        if(rootElementForEditAddsAct.tvCity.text.toString() != getString(R.string.select_city)) {   //15.6
            rootElementForEditAddsAct.tvCity.text = getString(R.string.select_city)  //Получаем с помощью функции getSting ресурс. Если мы напишем сразу напрямую ресурс R.string.select_country без getStrings, то будем показано число в textView
            //У некоторых функций уже есть внутри функция getString поэтому иногда мы просто передаем R.string.resources  и у нас корректно показываем текст. Но в данном случае мы напрямую текст передаем в textView, поэтому сперва нужно получить сам текст а не идентификатор
        }
    }

    fun onClickSelectCity(view: View) {  //15.3
        val selectedCountry = rootElementForEditAddsAct.tvCountry.text.toString()   //Пока текста нет в textView для выбора города, будем показывать, что нужно выбрать обязательно страну чтобы выбрать город
            if(selectedCountry != getString(R.string.select_country)){  //Если то, что находится в textView не равно значению по-умолчанию, значит человек уже выбрал страну, и мы можем запускать код ниже
                val listCity = CityHelper.getAllCities(selectedCountry, this)    //передали название страны и контекст
                dialog.showSpinnerDialog(this, listCity, rootElementForEditAddsAct.tvCity)  //15.5.2
            }
            else {    // иначе выводим тост "выберите пожалуйста страну!"
//                Toast.makeText(this, R.string.country_not_selected, Toast.LENGTH_LONG ).show()
                Toast.makeText(this, "No country selected", Toast.LENGTH_LONG).show()
            }
    }

    fun onClickGetImages(view: View) {  //16.5
        ImagePicker.getImages(this, )   //16.6
    }




}