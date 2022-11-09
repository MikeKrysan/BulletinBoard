package com.MikeKrysan.myapplication.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.utils.ItemTouchMoveCallback

class ImageListFrag(private val fragCloseInterface: FragmentCloseInterface, private val newList:ArrayList<String>): Fragment() { //17.2    //17.7.3 Добавляем конструктор нашему классу, который выводит фрагмент. Чтобы то, что мы будем передавть в этот конструктор было доступно на уровне всего класса нашего фрагмента, нужно указать val либо var //18.8 - newList:ArrayList

    val adapter = SelectImageRvAdapter() //18.9.1
    val draggCallback = ItemTouchMoveCallback(adapter) //19.3.2
    val touchHelper = ItemTouchHelper(draggCallback)     //19.2.1 Классу ItemTouchHelper() нужно передать калбек (ItemTouchHelperCallback). У нас его нет, поэтому нужно создать этот класс, создать интсанцию этого класса, и передать ее в класс ItemTouchHelper()

    //17.2.1 Создаем основые и обязательные функции для фрагмента:
    override fun onCreateView(  //В этой функции начинается отрисовка нашего фрагмента
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_image_frag, container, false)
    }

    //17.2.2
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {   //В этой функции получаем все элементы, которые нарисовались
        super.onViewCreated(view, savedInstanceState)
        val bBack = view.findViewById<Button>(R.id.bBack)   //17.6
        val rcView = view.findViewById<RecyclerView>(R.id.rcViewSelectImage)     //18.9
        touchHelper.attachToRecyclerView(rcView)    //19.2.2
        rcView.layoutManager = LinearLayoutManager(activity) //18.9.2 Передаем контекст - activity. Во фрагменте присутствует активити, получить его можно таким способом. Т.о мы получаем EditAddsAct, там где запустился этот фрагмент
        rcView.adapter = adapter //18.9.3
        val updateList = ArrayList<SelectImageItem>()    //Создаем массив с типом данных, который нам будет нужен для заполнения массива
        for(n in 0 until newList.size) {    //18.9.4
//            updateList.add(SelectImageItem(n.toString(), newList[n]))   //1 вариант
//            val selectImageItem = SelectImageItem("0", "0")    //2 вариант перезаписи данных в дата-классе
//            selectImageItem.copy(title="890")
            updateList.add(SelectImageItem(n.toString(), newList[n]))   //заполняем updateList, когда он заканчивается, мы передаем в адаптер уже заполненный список
        }
        adapter.updateAdapter(updateList)     //18.9.5
        bBack.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()    //17.6.1
        }
    }

    override fun onDetach() {   //17.8
        super.onDetach()
        fragCloseInterface.onFragClose()       //17.8.1 Так как мы передали интерфейс с EditAddsAct, то он и запустится также в функции onFragClose() класса EditAddsAct и тогда view станет видимым
        Log.d("MyLog", "Title 0 : ${adapter.mainArray[0].title}")    //18.9 Временная проверка
        Log.d("MyLog", "Title 1 : ${adapter.mainArray[1].title}")
        Log.d("MyLog", "Title 2 : ${adapter.mainArray[2].title}")
    }
}