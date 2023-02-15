package com.MikeKrysan.myapplication.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.R

class ImageAdapter :  RecyclerView.Adapter<ImageAdapter.ImageHolder>(){     //20.2

//    val  mainArray = ArrayList<SelectImageItem>()   //20.3   //20.10
    val  mainArray = ArrayList<Bitmap>()   //22.2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_adapter_item, parent, false)
        return ImageHolder(view)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) { //В данной функции мы достаем элементы из массива и заполняем viewHolder
//        holder.setData(mainArray[position].imageUri)     //20.10.1
        holder.setData(mainArray[position])     //22.2
    }

    override fun getItemCount(): Int {
        return mainArray.size   //20.3.1 Передаем сюда размер данного списка
    }

    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView){  //* Это наш viewHolder который содержит информацию о каждом элементе
        lateinit var imItem : ImageView     //Нам нужно будет найти на нашем элементе во viewPager view который мы создадим наш imageView который мы будем заполнять с помощью ссылки с функции setData()

        fun setData(bitmap:Bitmap){
            imItem = itemView.findViewById(R.id.imItem) //как только запустили функцию, находим наш imageItem. Находим со view, который мы передадим
//            imItem.setImageURI(Uri.parse(bitmap))    //заполяем элемент, передаем cсылку, но это string, а от нас ждет uri, поэтому парсим используя класс Uri
            imItem.setImageBitmap(bitmap)
        }
    }

//    fun update(newList : ArrayList<SelectImageItem>){    //20.4 //20.10.3
    fun update(newList: ArrayList<Bitmap>){   //22.2
        mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()  //20.5
    }

}

/*
* Класс ImageHolder наследуется от RecyclerView.ViewHolder() куда нужно передать itemView - это название мы даем сами. А приходит itemView из конструктора, значит создаем конструктор
* в нашем классе ImageHolder и пишем itemView:View
 */