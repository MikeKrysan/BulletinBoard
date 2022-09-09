package com.MikeKrysan.myapplication.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.R

class RcViewDialogSpinner : RecyclerView.Adapter<RcViewDialogSpinner.SpViewHolder>() {    //Адаптер принимает ViewHolder, который мы сейчас создадим. Адаптер ждет ViewHolder, а мы передаем просто класс SpViewHolder,
    //поэтому классу SpViewHolder нужно унаследоваться от RecyclerView.ViewHolder. Все равно будет ругатся, пишет что это другой класс, а я пишу класс SpViewHolder напрямую, поэтому указываем
    //что класс SpViewHolder находится внутри класса RcViewDialogSpinner

    val mainList = ArrayList<String>()  //13.5.3 Создали массив, заполнять его будем в функции updateAdapter

    //13.5.2
    //методы адаптера которые позволяют нарисовать элементы, нужно знать, сколько элементов будет нарисовано
    //Рисуем элемент и создается ViewHolder:
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)   //13.7 надуваем наш textView, предварительно создав его
        return SpViewHolder(view)   //Создается новый viewHolder и туда передаем view
    }

    //После к этому элементу подключаем текст и т.д.
    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
    }

    //Узнаем, сколько элементов нужно нарисовать
    override fun getItemCount(): Int {
        return mainList.size    //13.5.5
    }

    //13.5.1
    class SpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {    //добавляем параметр itemView в конструктор. В наш ViewHolder нужно будет передавать view, который мы рисуем
        fun setData(text:String) {  //Список будет состоять из одного textView, который показывает название страны либо города
            val tvSpItem = itemView.findViewById<TextView>(R.id.tvSpItem) //13.5.6 Находим наш textView, когда создается наш viewHolder
            tvSpItem.text = text    //передаем текст, который передаем в функцию setData и его возьмем из списка text
        }
    }

    //13.5.4
    fun updateAdapter(list: ArrayList<String>){ //с помощью этой функции будем обновлять наш адаптер
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()    //нужно сказать нашему адаптеру, что данные изменились
    }


}

/**
 * Рисуется первый элемент (fun onCreateViewHolder()), создается view этого элемента по разметке которая у нас есть - sp_list_item.xml. Берется эта разметка, рисуестся, создается view.  У него уже есть идентификатор,
 * размер, где  он должен находится. Этот view передается в наш SpViewHolder и рисуется на экране. Но теперь SpViewHolder хранит на этот элемент ссылки (в классе SpViewHolder в переменной tvSpItem)
 * По этой ссылке мы может сделать что-то с этим элементом: например взять и показать там текст (tvSpItem.text = text). Текст показан когда все нарисовано, запускается onBindViewHolder() и нам возвращает holder наш
 * SpViewHolder, то-есть мы его создали в onCreateViewHolder(), он заполнился(запустился класс SpViewHolder) и когда запустилась функция onBindViewHolder() после отрисовки нам возвращает наш
 * holder, а внутри нашего holder-a  есть все что мы добавили (смотри класс  SpViewHolder, например функция setData).
 * Поэтому в onBindViewHolder() можно написать: holder.setData(), а значение взять из списка mainList
 */