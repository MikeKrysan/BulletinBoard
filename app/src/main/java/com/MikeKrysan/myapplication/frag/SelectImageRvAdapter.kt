package com.MikeKrysan.myapplication.frag


import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.act.EditAdsAct
import com.MikeKrysan.myapplication.databinding.SelectImageFragItemBinding
import com.MikeKrysan.myapplication.utils.AdapterCallback
import com.MikeKrysan.myapplication.utils.ImageManager
import com.MikeKrysan.myapplication.utils.ImagePicker
import com.MikeKrysan.myapplication.utils.ItemTouchMoveCallback
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.coroutineContext

class SelectImageRvAdapter(val adapterCallback: AdapterCallback): RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(), ItemTouchMoveCallback.ItemTouchAdapter {  //18.1  //19.5

//    val mainArray = ArrayList<SelectImageItem>()      //18.6.1 Создали массив. Пока что он пустой, его нужно будет передать в getItemCount(), потому что отсюда и будет браться размер, который нужно будет адаптеру заполнить в recyclerView   //<SelectImageItem> - массив будет хранить item - SelectImageItem, который содержит два элемента    //19.9.1 Даем доступ к адаптеру, убрав private
    val mainArray = ArrayList<Bitmap>()    //22.1.2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item, parent, false)
//        return ImageHolder(view, parent.context, this)    //22.2    //24.3 Конструктор ждет еще одни элемент, потому что мы его добавили классу ImageHolder (передаем инстанции класса, который сейчас работает, то-есть SelectImageRvAdapter)
        val viewBinding =SelectImageFragItemBinding.inflate(LayoutInflater.from(parent.context), parent, false )
        return ImageHolder(viewBinding, parent.context, this)

    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])    //18.6 Мы берем setData из списка mainArray. Он пока пустой, но мы добавим сейчас функцию updateAdapter() и будем заполять список при помощи этой функции. Этой список мы будем заполнять, когда мы получили ссылки  на картинки и перешли на наш фрагмент
                            //Тогда мы делаем update адаптера и тогда показываются картинки и заголовки
    }

    override fun onViewDetachedFromWindow(holder: ImageHolder) {
        super.onViewDetachedFromWindow(holder)
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
        notifyItemMoved(startPos, targetPos)    //указываем откуда куда нужно перетащить элементы, чтобы перестроился адаптер
//        notifyDataSetChanged()    //20.13.1
    }

    override fun onClear() {            //20.13.2
        notifyDataSetChanged()
    }


//    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ImageHolder(private val viewBinding: SelectImageFragItemBinding, val context : Context, val adapter : SelectImageRvAdapter) : RecyclerView.ViewHolder(viewBinding.root) { //22.2   //24.3
//    class ImageHolder(itemView: View, val context: Context, val adapter: SelectImageRvAdapter): RecyclerView.ViewHolder(itemView) {
//        lateinit var tvTitle : TextView
//        lateinit var image : ImageView
//        lateinit var imEditImage : ImageButton  //23.3 Находим созданную кнопуку в select_image_frag_item.xml
//        lateinit var imDeleteImage : ImageButton    //24.2
//        lateinit var pBar : ProgressBar
//        fun setData(item: SelectImageItem) {    //18.3

        fun setData(bitMap: Bitmap) {    //22.1.2
//            tvTitle = itemView.findViewById(R.id.tvTitle)   //18.5 Инициализируем переменные
//            image = itemView.findViewById(R.id.imageContent)
//            imEditImage = itemView.findViewById(R.id.imEditImage)   //23.3
//            imDeleteImage = itemView.findViewById(R.id.imDelete)    //24.2
//            pBar = itemView.findViewById(R.id.pBar)

            //23.3 Добавляем слушателя нажатий:
//            imEditImage.setOnClickListener{
            viewBinding.imEditImage.setOnClickListener {
//                pBar.visibility = View.VISIBLE
//                ImagePicker.getImages(context as EditAddsAct, 1, ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGE)   //23.5.1 Передаем контекст, который у нас есть. Как будто я передал мое активити
//                context.editImagePos = adapterPosition //23.5.2 Я нажал на кнопку, и контекст получит номер позиции, на который я нажал. И теперь будет перезаписываться запись в переменной editImagePos EditAddsAct
                    ImagePicker.getSingleImage(context as EditAdsAct)
                    context.editImagePos = adapterPosition
            }

            //24.3
//            imDeleteImage.setOnClickListener{
            viewBinding.imDelete.setOnClickListener {
                adapter.mainArray.removeAt(adapterPosition) //24.3 Через adapterPosition мы знаем, какой элемент выбран для удаления
                adapter.notifyItemRemoved(adapterPosition)
                //adapter.notifyDataSetChanged()  //24.5 Эта функция говорит также о том, что данные внутри изменились, но анимации не будет
                for(n in 0 until adapter.mainArray.size) adapter.notifyItemChanged(n)
                adapter.adapterCallback.onItemDelete()

            }
//            tvTitle.text = item.title   //18.5.1 вот так работает Data класс
//            tvTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            viewBinding.tvTitle.text = context.resources.getStringArray(R.array.title_array) [adapterPosition]  //22.1.2 закоментировал
            ImageManager.chooseScaleType(viewBinding.imageView, bitMap)
//            image.setImageBitmap(bitMap)
            viewBinding.imageView.setImageBitmap(bitMap)
        }
    }

//    fun updateAdapter(newList : List<SelectImageItem>) {    //18.7
//    fun updateAdapter(newList : List<SelectImageItem>, needClear : Boolean) {    //21.8.2
    fun updateAdapter(newList : List<Bitmap>, needClear : Boolean) {    //22.1.2
        //    mainArray.clear()   //18.7.1 Сперва очищаем
        if(needClear) mainArray.clear()
        mainArray.addAll(newList)   //18.7.2 После мы его заполняем всеми данными
        notifyDataSetChanged()  //18.7.3 Сообщаем адаптеру о том, что данные внутри изменились, чтобы он снова перезапустился
    }

}