package com.MikeKrysan.myapplication.act

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.MikeKrysan.myapplication.MainActivity
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.adapters.ImageAdapter
import com.MikeKrysan.myapplication.model.Ad
import com.MikeKrysan.myapplication.model.DbManager
import com.MikeKrysan.myapplication.databinding.ActivityEditAddsBinding
import com.MikeKrysan.myapplication.dialogs.DialogSpinnerHelper
import com.MikeKrysan.myapplication.frag.FragmentCloseInterface
import com.MikeKrysan.myapplication.frag.ImageListFrag
import com.MikeKrysan.myapplication.utils.CityHelper
import com.MikeKrysan.myapplication.utils.ImagePicker

class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    var chooseImageFrag : ImageListFrag? = null     //21.8 *
    lateinit var rootElement:ActivityEditAddsBinding  //14.9 делаем rootElement public
    private val dialog = DialogSpinnerHelper() //14.2 Создаем диалог на уровне класса
    private var isImagesPermissionGranted = false   //16.3.1
    lateinit var imageAdapter : ImageAdapter    //20.6 Создали переменную на уровне класса для того чтобы она была доступна для любой функции. Адаптер мы будем обновлять, поэтому доступ к адаптеру нам нужен любой функции
    private val dbManager = DbManager()
    var launcherMultiSelectImage: ActivityResultLauncher<Intent>? = null
    var launcherSingleSelectImage: ActivityResultLauncher<Intent>? = null
    var editImagePos = 0  //23.4
    private var isEditState = false
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAddsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)

        init()  //14.1
        checkEditState()

//        val listCountry = CityHelper.getAllCountries(this)  //13.1

//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, CityHelper.getAllCountries(this))    //12.4 Адаптер создан
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)      //12.5 Теперь адаптер нужно прикрепить к нашему спиннеру. И еще здесь нужно указать setDropDownViewResource (установить ресурс раскрывающегося списка)
//        rootElementForEditAddsAct.spCountry.adapter = adapter//Берем спиннер из рут-элемента

//        val dialog  = DialogSpinnerHelper()     //13.4 Создаем объект класса DialogSpinnerHelper
//        dialog.showSpinnerDialog(this, listCountry) //Окно появилось, но нет поиска и списка, потому что нет адаптера, и никакой recyclerView к адаптеру не подключен
    }

    private fun checkEditState() {
        isEditState = isEditState() //true по-умолчанию
        if(isEditState) {
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad
            if(ad != null) fillViews(ad!!)
        }
    }

    //Функция, которая будет проверять, мы зашли для создадния нового объявления, либо для редактирования:
    private fun isEditState(): Boolean {
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun fillViews(ad : Ad) = with(rootElement) {
        tvCountry.text = ad.country
        tvCity.text = ad.city
        editTel.setText(ad.tel)
        edIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSend.toBoolean()
        tvCat.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)
    }

    private fun init() {    //14.1
        imageAdapter = ImageAdapter()   //20.7.1 Инициализируем imageAdapter
        rootElement.vpImages.adapter = imageAdapter   //20.7
        launcherMultiSelectImage = ImagePicker.getLauncherForMultiSelectImages(this)
        launcherSingleSelectImage = ImagePicker.getLauncherForSingleImage(this)
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {   //16.8
//        super.onActivityResult(requestCode, resultCode, data)
//        ImagePicker.showSelectedImages(resultCode, requestCode, data, this)
//
////        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES) {
////
////            if (data != null) {
//////                val returnValue: ArrayList<String> = data.getStringArrayListExtra(Pix.IMAGE_RESULTS) as ArrayList<String>   //1-й вариант. Автоматически переделанный студией код из java в kotlin
////
////                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)   //2-й вариант. Здесь котлин сам определяет что за тип данных мы получаем
//////                    Log.d("MyLog", "Image: ${returnValues?.get(0)}")      //16.9
//////                    Log.d("MyLog", "Image: ${returnValues?.get(1)}")
//////                    Log.d("MyLog", "Image: ${returnValues?.get(2)}")
//////                    if(returnValues?.size!! > 1) {                                                      //18.10 Start
//////                        rootElementForEditAddsAct.scrollViewMain.visibility = View.GONE
//////                        val fm = supportFragmentManager.beginTransaction()
//////                        fm.replace(R.id.place_holder, ImageListFrag(this, returnValues))
//////                        fm.commit()                                                                     //18.10 End
//////                    }
////                if (returnValues?.size!! > 1 && chooseImageFrag == null) {                                  //21.8.1
////
//////                    chooseImageFrag = ImageListFrag(                                                  //22.3.1start закоментили. Код вынесли в отдельную функцию
//////                        this,
//////                        returnValues
//////                    ) //21.8.2 Создаем наш фрагмент, записываем ссылку в chooseImageFrag
//////                    rootElementForEditAddsAct.scrollViewMain.visibility = View.GONE
//////                    val fm = supportFragmentManager.beginTransaction()
//////                    fm.replace(
//////                        R.id.place_holder,
//////                        chooseImageFrag!!
//////                    )   //21.8.2 Теперь передаем ссылку фрагмента взяв ответственность на себя, что там не null
//////                    fm.commit()                                                                           //22.3.1 end закоментили
////
////                    openChooseImageFrag(returnValues)
////
////                } else if (returnValues.size == 1 && chooseImageFrag == null) { //24.4
////
//////                    imageAdapter.update(returnValues) //25.3
////                    val tempList = ImageManager.getImageSize(returnValues[0])
//////                    Log.d("MyLog", "Image width : ${tempList[0]}")
//////                    Log.d("MyLog", "Image height : ${tempList[1]}")
////
////                } else if (chooseImageFrag != null) { //21.8.1 Если аноноимный класс не равен null, то мы выбираем картинки из фрагмента, логично нам не нужно его еще раз создавать, нужно взять и обновить адаптер.
////
////                    chooseImageFrag?.updateAdapter(returnValues)//21.10
////                }
////            }
////        } else if(resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_SINGLEIMAGE) {   //23.6
////            if(data != null) {
////                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
////                chooseImageFrag?.setSingleImage(uris?.get(0)!!,editImagePos)    //Обновляем позицию из нашего адаптера. Говорю котлин, что там точно не null
////            }
////        }
//    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//
//        when (requestCode) {
//            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
//                //If request is cancelled, the result arrays are empty.
//                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                    ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)  //17.9.2 * imageCounter    //23.2.2
////                      isImagesPermissionGranted = true
//                } else {
////                    isImagesPermissionGranted = false
//                    Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show()
//                }
//                return
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)     //16.3
//    }

    //OnClicks
    fun onClickSelectCountry(view: View) {  //14.5 Есть слушатель нажатий, но он пока ни к чему не подключен. Чтобы его подключить, в активити на textView находим onClick и назначаем ему только что созданый onClickSelectCountry
        val listCountry = CityHelper.getAllCountries(this)  //13.1
        dialog.showSpinnerDialog(this, listCountry, rootElement.tvCountry) //15.5.1
        if(rootElement.tvCity.text.toString() != getString(R.string.select_city)) {   //15.6
            rootElement.tvCity.text = getString(R.string.select_city)  //Получаем с помощью функции getSting ресурс. Если мы напишем сразу напрямую ресурс R.string.select_country без getStrings, то будем показано число в textView
            //У некоторых функций уже есть внутри функция getString поэтому иногда мы просто передаем R.string.resources  и у нас корректно показываем текст. Но в данном случае мы напрямую текст передаем в textView, поэтому сперва нужно получить сам текст а не идентификатор
        }
    }


    fun onClickSelectCity(view: View) {  //15.3
            val selectedCountry = rootElement.tvCountry.text.toString()   //Пока текста нет в textView для выбора города, будем показывать, что нужно выбрать обязательно страну чтобы выбрать город
            if(selectedCountry != getString(R.string.select_country)){  //Если то, что находится в textView не равно значению по-умолчанию, значит человек уже выбрал страну, и мы можем запускать код ниже
                val listCity = CityHelper.getAllCities(selectedCountry, this)    //передали название страны и контекст
                dialog.showSpinnerDialog(this, listCity, rootElement.tvCity)  //15.5.2
            }
            else {    // иначе выводим тост "выберите пожалуйста страну!"
//                Toast.makeText(this, R.string.country_not_selected, Toast.LENGTH_LONG ).show()
                Toast.makeText(this, "No country selected", Toast.LENGTH_LONG).show()
            }
    }

    fun onClickSelectCat(view: View) {

            val listCat = resources.getStringArray(R.array.category).toMutableList() as ArrayList
            dialog.showSpinnerDialog(this, listCat, rootElement.tvCat)

    }

    fun onClickGetImages(view: View) {  //16.5
//        ImagePicker.getImages(this )   //16.6
//        rootElementForEditAddsAct.scrollViewMain.visibility = View.GONE //17.4
//        val fm = supportFragmentManager.beginTransaction()  //17.4 Пока что мы не будем проверять выбор картинок, а проверим наш фрагмент
//        fm.replace(R.id.place_holder, ImageListFrag(this))//заменяем контейнер, который мы создали (id=placeholder) на фрагмент //17.7.2 (this) - пока что фрагмент не ждет интерфейса, потому что конструктор к нашему элементу мы не добавили
//        fm.commit()     //чтобы все изменения применились, запускаем commit

        if(imageAdapter.mainArray.size == 0) {    //22.3.2   imageAdapter.mainArray - это и есть массив с выбранными картинками из окна выбора фото. Если нет фото-выбираем, а если есть-открываем фрагмент с выбранными картинками
//            ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)     //23.2.2
            ImagePicker.launcher(this, launcherMultiSelectImage, 3)
        } else {
            openChooseImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
        }
//        ImagePicker.getImages(this, 3)  //18.11 Чтобы запустилась проверка, поставим код временно из функции onRequestPermissionsResult()
    }

    fun onClickPublish(view: View) {
        val adTemp = fillAd()
        if(isEditState) {
            dbManager.publishAd(adTemp.copy(key = ad?.key), onPublishFinish())
        } else {
            dbManager.publishAd(adTemp, onPublishFinish())
        }
//        finish()
    }

    private fun onPublishFinish():DbManager.FinishWorkListener {
        return object: DbManager.FinishWorkListener {
            override fun onFinish() {
                finish()
            }

        }
    }

    private fun fillAd() : Ad {
        val ad: Ad
        rootElement.apply {
            ad = Ad(tvCountry.text.toString(),
                    tvCity.text.toString(),
                    editTel.text.toString(),
                    edIndex.text.toString(),
                    checkBoxWithSend.isChecked.toString(),
                    tvCat.text.toString(),
                    edTitle.text.toString(),
                    edPrice.text.toString(),
                    edDescription.text.toString(),
                    dbManager.db.push().key,
                    "0",
                    dbManager.auth.uid)
        }
        return ad
    }

//    override fun onFragClose(list : ArrayList<SelectImageItem>) {    //20.10.4
    override fun onFragClose(list : ArrayList<Bitmap>) {   //22.2
        rootElement.scrollViewMain.visibility = View.VISIBLE  //17.7.1Интерфейс нужно передать через фрагмент в контсруктор. Когда создаестя фрагмент, внутрь его передадим этот интерфейс, поэтому если там мы запускаем наш интерфейс, то он и здесь запуститься
        imageAdapter.update(list)   //20.11
        chooseImageFrag = null  //21.11
    }


    fun openChooseImageFrag(newList : ArrayList<String>? ) { //22.3.1 Передавать в функцию я буду  список с картинками, который будет в моем фрагменте
        chooseImageFrag = ImageListFrag(this, newList)  //
        rootElement.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFrag!!)
        fm.commit()
    }

}

//* Делаем возможным быть анонимному классу null. Если он null, мы еще не создали фрагмент, и тогда можно смело запускать условие if(returnValues?.size!! > 1)
// функции onActivityResult и создавать новый фрагмент. Но если он не null, значит он уже был создан. Это значит, что если функция onActivityResult запустилась,
// тобишь мы ее запустили из этого фрагмента
