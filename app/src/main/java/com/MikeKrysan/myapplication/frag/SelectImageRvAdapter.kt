package com.MikeKrysan.myapplication.frag

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.R

class SelectImageRvAdapter: RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>() {  //18.1

    private val mainArray = ArrayList<SelectImageItem>()      //18.6.1 Создали массив. Пока что он пустой, его нужно будет передать в getItemCount(), потому что отсюда и будет браться размер, который нужно будет адаптеру заполнить в recyclerView   //<SelectImageItem> - массив будет хранить item - SelectImageItem, который содержит два элемента

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item, parent, false)
        return ImageHolder(view)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])    //18.6 Мы берем setData из списка mainArray. Он пока пустой, но мы добавим сейчас функцию updateAdapter() и будем заполять список при помощи этой функции. Этой список мы будем заполнять, когда мы получили ссылки  на картинки и перешли на наш фрагмент
                            //Тогда мы делаем update адаптера и тогда показываются картинки и заголовки
    }

    override fun getItemCount(): Int {
        return mainArray.size //18.6.2
    }

    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var tvTitle : TextView
        lateinit var image : ImageView
        fun setData(item: SelectImageItem) {    //18.3
            tvTitle = itemView.findViewById(R.id.tvTitle)   //18.5 Инициализируем переменные
            image = itemView.findViewById(R.id.imageContent)
            tvTitle.text = item.title   //18.5.1 вот так работает Data класс
            image.setImageURI(Uri.parse(item.imageUri))
        }
    }

    fun updateAdapter(newList : List<SelectImageItem>) {    //18.7
        mainArray.clear()   //18.7.1 Сперва очищаем
        mainArray.addAll(newList)   //18.7.2 После мы его заполняем всеми данными
        notifyDataSetChanged()  //18.7.3 Сообщаем адаптеру о том, что данные внутри изменились, чтобы он снова перезапустился
    }

}