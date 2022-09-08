package com.MikeKrysan.myapplication.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

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
            val jsonObject = JSONObject(jsonFile)//Указываем, какой файл мы хотим превратить в JSONObject .Получаем данные в формате json, чтобы не пришлось парсить в виде strin.
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
}