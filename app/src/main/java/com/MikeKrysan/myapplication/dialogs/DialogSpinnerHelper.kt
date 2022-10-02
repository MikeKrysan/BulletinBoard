package com.MikeKrysan.myapplication.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.utils.CityHelper

class DialogSpinnerHelper {

    fun showSpinnerDialog(context: Context, list:ArrayList<String>, tvSelection: TextView) {   //15.5.3
        val builder = AlertDialog.Builder(context)  //Создаем билдер для создания диалога
        val dialog = builder.create()   //14.16
        val rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null)      //Берем разметку из spinner_layout.xml и надуваем ее. Мы находимся не непосредственно на
                                                                                                    // активити, а в диалоге, где нет layoutInflater. Поэтому нам нужно его откуда-то взять: LayoutInflater.from(context)
//        val adapter = RcViewDialogSpinnerAdapter(context, dialog, tvSelection)     //13.8   //14.7   //15.5
        val adapter = RcViewDialogSpinnerAdapter(tvSelection, dialog)      //15.5.4   //15.5.6
        val rcView = rootView.findViewById<RecyclerView>(R.id.rcSpView)     //13.8.1 ищем RecyclerView элемент в spinnerlayout.xml
        val sv = rootView.findViewById<SearchView>(R.id.svSpinner)     //13.10.1
        rcView.layoutManager = LinearLayoutManager(context)     //13.8.2 Нужно указать layoutManager, то-есть,как он будет выглядеть. Указываем, чтобы ишел как простой список по-вертикали
        rcView.adapter = adapter    //13.8.3 Теперь нужно этому recyclerView присвоить этот адаптер. Пока что там будет пустота, потому что изначально  в адаптере mainList класса RcViewDialogSpinner ничего нет
        //Чтобы его заполнить нужно запустить функцию updateAdapter() класса RcViewDialogSpinner и передать сюда список. Когда mainList обновится и наш адаптер тоже обновится и покажет нам список элемнтов.
        //Как это можно сделать? запустить функцию из CityHelper и получить данные всех стран и городов. Но мы ее уже передаем в DialogSpinnerHelper(list)

        dialog.setView(rootView)   //Чтобы builder мог отрисоватся, необходим view

        adapter.updateAdapter(list) //13.8.4 Запускаем и смотрим, появился список или нет. Появился список со всеми странами, но если начать писать в поиске, то фильтрация не работает, потому что нет проверки что мы пишем

        setSearchView(adapter, list, sv)  //13.10

        dialog.show()

        

    }

    //13.10
    private fun setSearchView(adapter: RcViewDialogSpinnerAdapter, list: ArrayList<String>, sv: SearchView?) {
        sv?.setOnQueryTextListener(object :SearchView.OnQueryTextListener{  //13.10.2 Для SearchView добавляем слушателя изменений текста. Имплементируем два обязательных метода класса OnQueryTextListener
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempList = CityHelper.filterListData(list, newText)  //13.10.3;  *
                adapter.updateAdapter(tempList) // 13.10.4 Теперь временный массив мы передаем в адаптер новым списком. Запускаем, проверяем
                return true
            }
        })
    }
}

/**
 *  * - filterListData() из CityHelper передает массив. Этот массив мы будем записывать во временный массив и передавать в адаптер
 */