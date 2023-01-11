package com.MikeKrysan.myapplication.frag

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.act.EditAddsAct
import com.MikeKrysan.myapplication.utils.ImagePicker
import com.MikeKrysan.myapplication.utils.ItemTouchMoveCallback

class SelectImageRvAdapter: RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(), ItemTouchMoveCallback.ItemTouchAdapter {  //18.1  //19.5

//    val mainArray = ArrayList<SelectImageItem>()      //18.6.1 Создали массив. Пока что он пустой, его нужно будет передать в getItemCount(), потому что отсюда и будет браться размер, который нужно будет адаптеру заполнить в recyclerView   //<SelectImageItem> - массив будет хранить item - SelectImageItem, который содержит два элемента    //19.9.1 Даем доступ к адаптеру, убрав private
    val mainArray = ArrayList<String>()    //22.1.2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item, parent, false)
        return ImageHolder(view, parent.context)    //22.2
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])    //18.6 Мы берем setData из списка mainArray. Он пока пустой, но мы добавим сейчас функцию updateAdapter() и будем заполять список при помощи этой функции. Этой список мы будем заполнять, когда мы получили ссылки  на картинки и перешли на наш фрагмент
                            //Тогда мы делаем update адаптера и тогда показываются картинки и заголовки
    }

    override fun getItemCount(): Int {
        return mainArray.size //18.6.2
    }

    override fun onMove(startPos: Int, targetPos: Int) {    //19.5
        val targetItem = mainArray[targetPos]   //Записываем элемент, над которым находится перетаскиваемый нами элемент
        mainArray[targetPos] = mainArray[startPos]  //Элементы которые мы перетаскиваем в адаптере, в массиве не перетаскиваются, поэтому мы делаем замену в массиве, чтобы замена в адаптере соответствовала записи в массиве
        //val titleStart = mainArray[targetPos].title //20.12.1 Чтобы сохранить титл перед тем как он будет перезаписан, для новой картинки, которая станет заглавной   //22.1.2 закоментировал
        //mainArray[targetPos].title = targetItem.title   //20.12 На этой позиции перезаписал титл на старый титл           //22.1.2 Закоментировал
        mainArray[startPos] = targetItem    //поменяли местами наши элементы    //Титл который сохранил, возвращаю на свое место
        //mainArray[startPos].title = titleStart  //20.12.2 Картинки поменялись, названия фото остались теми же             //22.1.2 Закоментировал
        notifyItemMoved(startPos, targetPos)    //указываем откуда куда нужно перетащить элементы, чтобы перестроился адаптер
//        notifyDataSetChanged()    //20.13.1
    }

    override fun onClear() {            //20.13.2
        notifyDataSetChanged()
    }


//    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ImageHolder(itemView: View, val context : Context) : RecyclerView.ViewHolder(itemView) { //22.2
        lateinit var tvTitle : TextView
        lateinit var image : ImageView
        lateinit var imEditImage : ImageButton  //23.3 Находим созданную кнопуку в select_image_frag_item.xml
//        fun setData(item: SelectImageItem) {    //18.3

        fun setData(item: String) {    //22.1.2
            tvTitle = itemView.findViewById(R.id.tvTitle)   //18.5 Инициализируем переменные
            image = itemView.findViewById(R.id.imageContent)
            imEditImage = itemView.findViewById(R.id.imEditImage)   //23.3
            //23.3 Добавляем слушателя нажатий:
            imEditImage.setOnClickListener {
                 ImagePicker.getImages(context as EditAddsAct, 1, ImagePicker.REQUEST_CODE_GET_SINGLEIMAGE)   //23.5.1 Передаем контекст, который у нас есть. Как будто я передал мое активити
                context.editImagePos = adapterPosition  //23.5.2 Я нажал на кнопку, и контекст получит номер позиции, на который я нажал. И теперь будет перезаписываться запись в переменной editImagePos EditAddsAct
            }
//            tvTitle.text = item.title   //18.5.1 вот так работает Data класс
            tvTitle.text = context.resources.getStringArray(R.array.title_array) [adapterPosition]  //22.1.2 закоментировал
            image.setImageURI(Uri.parse(item))
        }
    }

//    fun updateAdapter(newList : List<SelectImageItem>) {    //18.7
//    fun updateAdapter(newList : List<SelectImageItem>, needClear : Boolean) {    //21.8.2
    fun updateAdapter(newList : List<String>, needClear : Boolean) {    //22.1.2
        //    mainArray.clear()   //18.7.1 Сперва очищаем
        if(needClear) mainArray.clear()
        mainArray.addAll(newList)   //18.7.2 После мы его заполняем всеми данными
        notifyDataSetChanged()  //18.7.3 Сообщаем адаптеру о том, что данные внутри изменились, чтобы он снова перезапустился
    }

}