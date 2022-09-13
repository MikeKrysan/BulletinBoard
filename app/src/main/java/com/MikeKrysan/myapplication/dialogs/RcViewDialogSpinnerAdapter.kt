package com.MikeKrysan.myapplication.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.act.EditAddsAct

class RcViewDialogSpinnerAdapter(var context: Context, var dialog:AlertDialog) : RecyclerView.Adapter<RcViewDialogSpinnerAdapter.SpViewHolder>() {    //Адаптер принимает ViewHolder, который мы сейчас создадим. Адаптер ждет ViewHolder, а мы передаем просто класс SpViewHolder,   //14.7class RcViewDialogSpinnerAdapter(context: Context)  //14.8.2 -б)    //14.17
    //поэтому классу SpViewHolder нужно унаследоваться от RecyclerView.ViewHolder. Все равно будет ругатся, пишет что это другой класс, а я пишу класс SpViewHolder напрямую, поэтому указываем
    //что класс SpViewHolder находится внутри класса RcViewDialogSpinner

    val mainList = ArrayList<String>()  //13.5.3 Создали массив, заполнять его будем в функции updateAdapter

    //13.5.2
    //методы адаптера которые позволяют нарисовать элементы, нужно знать, сколько элементов будет нарисовано
    //Рисуем элемент и создается ViewHolder:
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item, parent, false)   //13.7 надуваем наш textView, предварительно создав его
        return SpViewHolder(view, context, dialog)   //Создается новый viewHolder и туда передаем view  //14.8.3 ViewHolder пока что ждет только view, а мы передали уже и context, следует добавить параметр Context в класс SpViewHolder  //14.17.1 передаем dialog во viewHolder
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
    class SpViewHolder(itemView: View, var context: Context, var dialog: AlertDialog) : RecyclerView.ViewHolder(itemView), View.OnClickListener {    //добавляем параметр itemView в конструктор. В наш ViewHolder нужно будет передавать view, который мы рисуем       //14.8 Добавляем интерфейс OnClickListener и добавляем его метод //14.8.4 Добавляем параметр Context в конструктор класса    //14.8.5***    //14.17.2 добавляем dialog  //****
        private var itemText = ""   //14.12
        fun setData(text:String) {  //Список будет состоять из одного textView, который показывает название страны либо города
            val tvSpItem = itemView.findViewById<TextView>(R.id.tvSpItem) //13.5.6 Находим наш textView, когда создается наш viewHolder
            tvSpItem.text = text    //передаем текст, который передаем в функцию setData и его возьмем из списка text
            itemText = text    //14.13
            itemView.setOnClickListener(this)   //14.15
        }

        override fun onClick(p0: View?) {       //14.8.1 Добавляем метод интрефейса
           //context //14.8.2 в этой функции мне и нужно взять текст из tvSpItem.text = text (см. выше) - из элемента на который нажал и показать его в textView activity_edit_adds.xml. И это можно сделать только через context**
            (context as EditAddsAct).rootElementForEditAddsAct.tvCountry.text =  itemText    //14.10 Я смело пишу так, потому что точно знаю, что context это и есть EditAddsAct. Если бы context был бы из другого активити, то в этом месте вышла бы ошибка   //14.11 //14.12 //14.14
            dialog.dismiss()    //14.17.3 теперь dialog доступен здесь, и при нажатии на кнопку страны, мы не только берем название(текст) страны, но и закрываем диалог

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
 *
 * * - Рисуется первый элемент (fun onCreateViewHolder()), создается view этого элемента по разметке которая у нас есть - sp_list_item.xml. Берется эта разметка, рисуестся, создается view.  У него уже есть идентификатор,
 * размер, где  он должен находится. Этот view передается в наш SpViewHolder и рисуется на экране. Но теперь SpViewHolder хранит на этот элемент ссылки (в классе SpViewHolder в переменной tvSpItem)
 * По этой ссылке мы может сделать что-то с этим элементом: например взять и показать там текст (tvSpItem.text = text). Текст показан когда все нарисовано, запускается onBindViewHolder() и нам возвращает holder наш
 * SpViewHolder, то-есть мы его создали в onCreateViewHolder(), он заполнился(запустился класс SpViewHolder) и когда запустилась функция onBindViewHolder() после отрисовки нам возвращает наш
 * holder, а внутри нашего holder-a  есть все что мы добавили (смотри класс  SpViewHolder, например функция setData).
 * Поэтому в onBindViewHolder() можно написать: holder.setData(), а значение взять из списка mainList
 *
 *  **- но сейчас context находится в конструкторе и недоступен в других функциях(подробнее как его достать читай в п. 14.8 описания в классе MainActivity)
 *
 *  *** - Добавляем переменную var = context: Context в конструктор класса SpViewHolder: class SpViewHolder(itemView: View, var context: Context) для того, чтобы context был доступен внутри класса
 *  До этого было так: class SpViewHolder(itemView: View, context: Context)
 *
 *  **** - (коментарий) В. Скажи пожалуйста почему в class SpViewHolder(itemView: View, var context: Context, var dialog: AlertDialog) мы в функциях можем сразу из конструктора использовать itemView,
 *  а для других параметров требуется ключевое слово?
 *  О. потому что мы наследуемся от RecyclerView.ViewHolder и там есть уже пустая переменная itemView по этому мы ее передаем в конструктор и от туда в класс от которого наследуемся
 *  RecyclerView.ViewHolder(itemView) таким образом мы это itemView который создали передали в тот что уже есть по этому он доступен на уровне всего класса так как из за наследования мы наследуем
 *  все что есть в RecyclerView.ViewHolder классе. Если вобще не передавать в SpViewHolde() ничего то все равно в нашем классе SpViewHolde будет доступен itemView но он пустой, это просто
 *  переменная типа View которая ждет что мы в нее передадим наш View. Другие переменные мы сами создали и их нет в классе от которого наследуемся. По правилу наследования мы наследуем все
 *  что есть в классе от которого наследуемся, и хоть мы не видим этих функций и переменных но они там есть и у нас есть к ним доступ если они указаны как public.
 *
 *  class Context - Это просто абстрактный класс который несет информацию об активити и помогает получить доступ к ресурсам, запускать intent и.т.д
 *
 *
 */