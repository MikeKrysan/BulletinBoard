package com.MikeKrysan.myapplication.utils

import android.content.Context
import com.MikeKrysan.myapplication.R
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

object CityHelper {
    //данная функция предназначена для того, чтобы взять массив информации в байтах из файла "countriesToCities.json" и превратить в string, для того чтобы можно было получить отдельно страны и города
    fun getAllCountries(context: Context):ArrayList<String> {   //функция будет возвращать список типа ArrayList; Чтобы добраться до папки assets, нужен контекст
        var tempArray = ArrayList<String>()     //создаем массив. По-очереди берем все элементы из json файла с городами и странами
        try{    //заполняем массив. Попытаемся считать с помощью inputStream()

            val inputStream : InputStream = context.assets.open("countriesToCities.json")   //Мы будем брать информацию из файла по частям в байтах, потом то мы будем сохранять ее в другом массиве заданного размера и преобразовывать в строку
            val size:Int = inputStream.available()  //сколько информации свободной нам доступно
            val bytesArray = ByteArray(size)    //указываем размер создаваемого массива
            inputStream.read(bytesArray)    //указываем массив, куда считываем strings. inputStream считает информацию из "countriesToCities.json" в байтах, и передаст в массив bytesArray
            val jsonFile = String(bytesArray)    //И теперь нам нужно данные превратить в Strings
            val jsonObject = JSONObject(jsonFile)//Указываем, какой файл мы хотим превратить в JSONObject .Получаем данные в формате json, чтобы не пришлось парсить в виде string.
            val countriesNames = jsonObject.names()    //Чтобы получить доступ к названиям стран, нам нужно получить доступ к объектам в json-файле

             if(countriesNames != null) {    //Создаем проверку, в случае, если наш cityNames.length будет null

                 for (n in 0 until countriesNames.length()) { //Прогоняем цикл, чтобы можно было выбирать города в стране
                    tempArray.add(countriesNames.getString(n))
                 }
             }

            } catch (e:IOException) {

            }
        return tempArray
    }


    fun filterListData(list: ArrayList<String>, searchText: String?): ArrayList<String> {   //13.9 передаем список, который мы хотим отфильтровать. Функция будет возвращать ArrayList<String> уже новый, отфильтрованный(когда вводишь первые буквы и показывает совпадение стран с такими первыми буквами)
        val tempList = ArrayList<String>()      //13.9.1 Передаем список со всеми странами или городами. По буквам начинает фильтровать. Если есть совпадение в списке list, то добавляет его в наш новый временный массив и возвращает массив уже не со всеми странами, а отфильтрованный
        tempList.clear()
        //Фильтруем с помощью цикла:
        if(searchText ==null) { //13.11 Если SearchText равен null, то дальше программа не пройдет, не будет ничего фильтровать. В tempList запишется один элемент "No result" и возвратит tempList. В списке появляется один элемент, где написано - нет результата
            tempList.add("No result")
            return tempList //возвращаем значение "Нет результатов"
        }
        for(selection: String in list) {
            if(selection.lowercase(Locale.ROOT).startsWith(searchText.lowercase(Locale.ROOT))) {
                tempList.add(selection)
            }
        }
        if(tempList.size == 0)  tempList.add("No result")  //Если нет совпадений
        return tempList
    }

}