package com.MikeKrysan.myapplication.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.MikeKrysan.myapplication.R

class ImageListFrag(val fragCloseInterface: FragmentCloseInterface): Fragment() { //17.2    //17.7.3 Добавляем конструктор нашему классу, который выводит фрагмент. Чтобы то, что мы будем передавть в этот конструктор было доступно на уровне всего класса нашего фрагмента, нужно указать val либо var

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
        bBack.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()    //17.6.1
        }
    }

    override fun onDetach() {   //17.8
        super.onDetach()
        fragCloseInterface.onFragClose()       //17.8.1 Так как мы передали интерфейс с EditAddsAct, то он и запустится также в функции onFragClose() класса EditAddsAct и тогда view станет видимым
    }
}