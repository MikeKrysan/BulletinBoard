package com.MikeKrysan.myapplication.frag

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.databinding.ListImageFragBinding
import com.MikeKrysan.myapplication.dialogHelper.ProgressDialog
import com.MikeKrysan.myapplication.utils.ImageManager
import com.MikeKrysan.myapplication.utils.ImagePicker
import com.MikeKrysan.myapplication.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFrag(private val fragCloseInterface: FragmentCloseInterface, private val newList:ArrayList<String>?): Fragment() { //17.2    //17.7.3 Добавляем конструктор нашему классу, который выводит фрагмент. Чтобы то, что мы будем передавть в этот конструктор было доступно на уровне всего класса нашего фрагмента, нужно указать val либо var //18.8 - newList:ArrayList

    lateinit var rootElement : ListImageFragBinding     //21.2.2

    val adapter = SelectImageRvAdapter() //18.9.1
    val draggCallback = ItemTouchMoveCallback(adapter) //19.3.2
    val touchHelper = ItemTouchHelper(draggCallback)     //19.2.1 Классу ItemTouchHelper() нужно передать калбек (ItemTouchHelperCallback). У нас его нет, поэтому нужно создать этот класс, создать интсанцию этого класса, и передать ее в класс ItemTouchHelper()
    private var job: Job? = null  //27.5

    //17.2.1 Создаем основые и обязательные функции для фрагмента:
    override fun onCreateView(  //В этой функции начинается отрисовка нашего фрагмента
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.list_image_frag, container, false)   //21.2.3
            rootElement = ListImageFragBinding.inflate(inflater)
            return rootElement.root
    }

    //17.2.2
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {   //В этой функции получаем все элементы, которые нарисовались
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()  //21.3.1
//        val bBack = view.findViewById<Button>(R.id.bBack)   //17.6
//        val rcView = view.findViewById<RecyclerView>(R.id.rcViewSelectImage)     //18.9   //21.2.1 Вместо findViewById используем viewBinding c подготовленной для него разметкой list_image_frag.xml
//        touchHelper.attachToRecyclerView(rcView)    //19.2.2
        touchHelper.attachToRecyclerView(rootElement.rcViewSelectImage)    //21.2.4

//        rcView.layoutManager = LinearLayoutManager(activity) //18.9.2 Передаем контекст - activity. Во фрагменте присутствует активити, получить его можно таким способом. Т.о мы получаем EditAddsAct, там где запустился этот фрагмент
//        rcView.adapter = adapter //18.9.3
        rootElement.rcViewSelectImage.layoutManager = LinearLayoutManager(activity)    //21.2.4
        rootElement.rcViewSelectImage.adapter = adapter //21.2.4
//        val updateList = ArrayList<SelectImageItem>()    //Создаем массив с типом данных, который нам будет нужен для заполнения массива    //22.1.1 Оптимазация кода
       // for(n in 0 until newList.size) {    //18.9.4    //22.1.1
//            updateList.add(SelectImageItem(n.toString(), newList[n]))   //1 вариант
//            val selectImageItem = SelectImageItem("0", "0")    //2 вариант перезаписи данных в дата-классе
//            selectImageItem.copy(title="890")
//            updateList.add(SelectImageItem(n.toString(), newList[n]))   //заполняем updateList, когда он заканчивается, мы передаем в адаптер уже заполненный список
//        }
        //27.4: я пишу здесь основной поток, потому что я хочу, чтобы после того, как эта функция закончится, после ее все запустилось на основном потоке   //27.5:
        if(newList != null) {
            resizeSelectedImages(newList, true)
//            Log.d("MyLog", "Result: $bitmapList")    //27.7
            }
        }
       // adapter.updateAdapter(newList, true)     //18.9.5    //21.8.3  //22.1.2     //26.2Пока что не будем передавать в адаптер
//        bBack.setOnClickListener{
//            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()    //17.6.1
//        }


    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {   //17.8
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)       //17.8.1 Так как мы передали интерфейс с EditAddsAct, то он и запустится также в функции onFragClose() класса EditAddsAct и тогда view станет видимым
        job?.cancel()    //27.6 Все операции, которые происходили в CoroutineScope - завершились
//        Log.d("MyLog", "Title 0 : ${adapter.mainArray[0].title}")    //18.9 Временная проверка
//        Log.d("MyLog", "Title 1 : ${adapter.mainArray[1].title}")
//        Log.d("MyLog", "Title 2 : ${adapter.mainArray[2].title}")
    }

    private fun resizeSelectedImages(newList: ArrayList<String>, needClear : Boolean) {

        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(newList)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)

        }
    }

    private fun setUpToolbar(){ //21.3
        rootElement.tb.inflateMenu(R.menu.menu_choose_image)
        val deleteItem = rootElement.tb.menu.findItem(R.id.id_delete_image)   //21.4 Создаем переменную, в нее ложим результат поиска.Мы берем меню которое только что надули и в нем ищем по идентификатору
        val addImageItem = rootElement.tb.menu.findItem(R.id.id_add_image)

        //21.4.2:
        rootElement.tb.setNavigationOnClickListener{
//            Log.d("MyLog", "Delete item")
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()    //21.5
        }

        //22.4.1:
        deleteItem.setOnMenuItemClickListener{
            adapter.updateAdapter(ArrayList(), true)  //21.6    //21.8.3
//            Log.d("MyLog", "Delete item")
            true
        }
        //22.4.1:
        addImageItem.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size   //21.7.2
            ImagePicker.getImages(activity as AppCompatActivity, imageCount, ImagePicker.REQUEST_CODE_GET_IMAGES)  //21.7 Ожидает AppCompatActivity а приходит FragmentActivity, делаем даункаст    //23.2.2
//            Log.d("MyLog", "Add item")
            true
        }
    }

    fun updateAdapter(newList:ArrayList<String>) {  //21.8.2
//        val updateList = ArrayList<SelectImageItem>()     //22.1.1
////        for(n in adapter.mainArray.size until newList.size ) {    //Если во фрагменте мы выбрали уже к примеру 2 картинки(adapter.mainArray.size = 2) и хотим добавить еще одну картинку (newList.size) - цикл не запустится так как не будут выполнены условия 2<=1, поэтому меняем условия работы цикла:
//        for(n in adapter.mainArray.size until newList.size + adapter.mainArray.size) {    //21.9 у нас может быть не нулявая позиция. Нам нужно начинать с позиции нашего массива.    //22.1.1
//            updateList.add(SelectImageItem(n.toString(), newList[n - adapter.mainArray.size]))    //22.1.1
//        }
//        adapter.updateAdapter(updateList, false)    //Указываем false. Список не очистится, просто добавятся к тем картинкам что уже есть новые
       resizeSelectedImages(newList, false)
//        adapter.updateAdapter(newList, false)   //22.1.1
     }

    fun setSingleImage(uri : String, pos : Int) {   //23.7.1

        val pBar = rootElement.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch{
            pBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(listOf(uri))
            pBar.visibility = View.GONE
            adapter.mainArray[pos] = bitmapList[0]  //Работает все как и раньше, просто вместо того, чтобы передавать ссылку, я эту ссылку превращаю в bitmap и передаю bitmap
//            adapter.notifyDataSetChanged()
            adapter.notifyItemChanged(pos)
        }
//        adapter.mainArray[pos] = uri    //На ту позицию, где я нажал, вставляю ссылку. Адаптер мне выдал позицию, на которую я нажал. Я получил картинку и перезаписал ее на этом месте
//        adapter.notifyDataSetChanged()  //Говорим адаптеру, что данные обновились
    }
}