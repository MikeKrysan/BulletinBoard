package com.MikeKrysan.myapplication.act

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.MikeKrysan.myapplication.MainActivity
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.adapters.ImageAdapter
import com.MikeKrysan.myapplication.databinding.ActivityEditAddsBinding
import com.MikeKrysan.myapplication.dialogs.DialogSpinnerHelper
import com.MikeKrysan.myapplication.frag.FragmentCloseInterface
import com.MikeKrysan.myapplication.frag.ImageListFrag
import com.MikeKrysan.myapplication.model.Ad
import com.MikeKrysan.myapplication.model.DbManager
import com.MikeKrysan.myapplication.utils.CityHelper
import com.MikeKrysan.myapplication.utils.ImageManager
import com.MikeKrysan.myapplication.utils.ImagePicker
import com.google.android.gms.tasks.OnCompleteListener
import java.io.ByteArrayOutputStream

class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    var chooseImageFrag : ImageListFrag? = null     //21.8 *
    lateinit var binding:ActivityEditAddsBinding  //14.9 делаем rootElement public
    private val dialog = DialogSpinnerHelper() //14.2 Создаем диалог на уровне класса
    private var isImagesPermissionGranted = false   //16.3.1
    lateinit var imageAdapter : ImageAdapter    //20.6 Создали переменную на уровне класса для того чтобы она была доступна для любой функции. Адаптер мы будем обновлять, поэтому доступ к адаптеру нам нужен любой функции
    private val dbManager = DbManager()
    var editImagePos = 0  //23.4
    private var imageIndex = 0
    private var isEditState = false
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAddsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()  //14.1
        checkEditState()
        imageChangeCounter()

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

    private fun fillViews(ad : Ad) = with(binding) {
        tvCountry.text = ad.country
        tvCity.text = ad.city
        editTel.setText(ad.tel)
        edIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSend.toBoolean()
        tvCat.text = ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)
        updateImageCounter(0)
        ImageManager.fillImageArray(ad, imageAdapter)
    }

    private fun init() {    //14.1
        imageAdapter = ImageAdapter()   //20.7.1 Инициализируем imageAdapter
        binding.vpImages.adapter = imageAdapter   //20.7
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
        dialog.showSpinnerDialog(this, listCountry, binding.tvCountry) //15.5.1
        if(binding.tvCity.text.toString() != getString(R.string.select_city)) {   //15.6
            binding.tvCity.text = getString(R.string.select_city)  //Получаем с помощью функции getSting ресурс. Если мы напишем сразу напрямую ресурс R.string.select_country без getStrings, то будем показано число в textView
            //У некоторых функций уже есть внутри функция getString поэтому иногда мы просто передаем R.string.resources  и у нас корректно показываем текст. Но в данном случае мы напрямую текст передаем в textView, поэтому сперва нужно получить сам текст а не идентификатор
        }
    }


    fun onClickSelectCity(view: View) {  //15.3
            val selectedCountry = binding.tvCountry.text.toString()   //Пока текста нет в textView для выбора города, будем показывать, что нужно выбрать обязательно страну чтобы выбрать город
            if(selectedCountry != getString(R.string.select_country)){  //Если то, что находится в textView не равно значению по-умолчанию, значит человек уже выбрал страну, и мы можем запускать код ниже
                val listCity = CityHelper.getAllCities(selectedCountry, this)    //передали название страны и контекст
                dialog.showSpinnerDialog(this, listCity, binding.tvCity)  //15.5.2
            }
            else {    // иначе выводим тост "выберите пожалуйста страну!"
//                Toast.makeText(this, R.string.country_not_selected, Toast.LENGTH_LONG ).show()
                Toast.makeText(this, R.string.country_not_selected, Toast.LENGTH_LONG).show()
            }
    }

    fun onClickSelectCat(view: View) {

            val listCat = resources.getStringArray(R.array.category).toMutableList() as ArrayList
            dialog.showSpinnerDialog(this, listCat, binding.tvCat)

    }

    fun onClickGetImages(view: View) {  //16.5
//        ImagePicker.getImages(this )   //16.6
//        rootElementForEditAddsAct.scrollViewMain.visibility = View.GONE //17.4
//        val fm = supportFragmentManager.beginTransaction()  //17.4 Пока что мы не будем проверять выбор картинок, а проверим наш фрагмент
//        fm.replace(R.id.place_holder, ImageListFrag(this))//заменяем контейнер, который мы создали (id=placeholder) на фрагмент //17.7.2 (this) - пока что фрагмент не ждет интерфейса, потому что конструктор к нашему элементу мы не добавили
//        fm.commit()     //чтобы все изменения применились, запускаем commit

        if(imageAdapter.mainArray.size == 0) {    //22.3.2   imageAdapter.mainArray - это и есть массив с выбранными картинками из окна выбора фото. Если нет фото-выбираем, а если есть-открываем фрагмент с выбранными картинками
//            ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)     //23.2.2
            ImagePicker.getMultiImages(this, 3)
        } else {
            openChooseImageFrag(null)
            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
        }
//        ImagePicker.getImages(this, 3)  //18.11 Чтобы запустилась проверка, поставим код временно из функции onRequestPermissionsResult()
    }

    fun onClickPublish(view: View) {
        if(isFieldsEmpty()) {
            showToast(resources.getString(R.string.attention_all_fields_must_be_filled))
            return
        }
        binding.progressLayout.visibility = View.VISIBLE    //прогресс-бар при публикации становится виден
        ad = fillAd()       //здесь генерируется новый ключ, который взят из fillAd()
//        //если редактирование:
//        if(isEditState) {
//             dbManager.publishAd(ad!!, onPublishFinish()) //сохраняются и редактируются все текстовые ссылки в объявлении. Для картинок работает отдельная логика
//            //если публикация
//        } else {
            uploadImages()  //Сначала заргужаем картинки. Текстовую часть загрузим после//Теперь все будет обновляться, и картинки также
//        finish()
    }

    //функция, которая проверяет все ли обязательные поля заполнены при создании объявления (поля помеченные звездочкой)
    private fun isFieldsEmpty(): Boolean = with(binding) {
        return tvCountry.text.toString() == getString(R.string.select_country)
                || tvCity.text.toString() == getString(R.string.select_city)
                || tvCat.text.toString() == getString(R.string.select_category)
                || edTitle.text.isEmpty()
                || edPrice.text.isEmpty()
                || edIndex.text.isEmpty()
                || edDescription.text.isEmpty()
                || editTel.text.isEmpty()
//                || imageAdapter.mainArray.size == 0 //Если мы хотим, чтобы пользователь вставлял в объявление хотя-бы одну картинку
    }


    private fun onPublishFinish():DbManager.FinishWorkListener {
        return object: DbManager.FinishWorkListener {
            override fun onFinish(isDone: Boolean) {
                binding.progressLayout.visibility = View.GONE   //прогресс-бар при окончании публикации исчезает
                if(isDone)  finish()
            }
        }
    }

    //Функция для считывания данных объявления с базы данных
    private fun fillAd() : Ad {
        val adTemp: Ad
        binding.apply {
            adTemp = Ad(tvCountry.text.toString(),
                    tvCity.text.toString(),
                    editTel.text.toString(),
                    edIndex.text.toString(),
                    checkBoxWithSend.isChecked.toString(),
                    tvCat.text.toString(),
                    edTitle.text.toString(),
                    edPrice.text.toString(),
                    edDescription.text.toString(),
                    editEmail.text.toString(),
                    ad?.mainImage?: "empty",
                    ad?.image2 ?: "empty",
                    ad?.image3 ?:"empty",
                    ad?.key ?: dbManager.db.push().key,
                    "0",
                    dbManager.auth.uid,
//                    System.currentTimeMillis().toString())  //Берем системное время, считается с 1970 года //При редактировании объявления время меняется, и оно поднимается вверх по списку в приложении
                    ad?.time ?: System.currentTimeMillis().toString())
        }
        return adTemp
    }

//    override fun onFragClose(list : ArrayList<SelectImageItem>) {    //20.10.4
    override fun onFragClose(list : ArrayList<Bitmap>) {   //22.2
        binding.scrollViewMain.visibility = View.VISIBLE  //17.7.1Интерфейс нужно передать через фрагмент в контсруктор. Когда создаестя фрагмент, внутрь его передадим этот интерфейс, поэтому если там мы запускаем наш интерфейс, то он и здесь запуститься
        imageAdapter.update(list)   //20.11
        chooseImageFrag = null  //21.11
        updateImageCounter(binding.vpImages.currentItem)
    }


    fun openChooseImageFrag(newList : ArrayList<Uri>? ) { //22.3.1 Передавать в функцию я буду  список с картинками, который будет в моем фрагменте
        chooseImageFrag = ImageListFrag(this)
        if(newList != null) chooseImageFrag?.resizeSelectedImages(newList, true, this)
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFrag!!)
        fm.commit()
    }

    //взятие картинки из приложения и загрузка в базу
    //Жмем первый раз на кнопку "Опубликовать объявление"
    private fun uploadImages() {
//        if(imageAdapter.mainArray.size == imageIndex) { //я выбрал 2 картинки, к примеру. Сейчас imageIndex = 0, потому что я первый раз нажал
        if (imageIndex == 3) {
            dbManager.publishAd(ad!!, onPublishFinish())
            return
        }
        val oldUrl = getUrlFromAd()
        if (imageAdapter.mainArray.size > imageIndex) {
            val byteArray = prepareImageByteArray(imageAdapter.mainArray[imageIndex])    //берем картинку с позиции 0 и превращаем ее в byteArray
            if (oldUrl.startsWith("http")) {    //обновляем картинку, или меняем на новую
                updateImage(byteArray, oldUrl) {
                    nextImage(it.result.toString())
                }
            } else {    //если картинки не было на данной позиции - записываем новую картинку
                //Интерфейс можно передавать не в аргументы функции, а в фигурные скобки
                //Загружаем эту картинку на Firebase
                uploadImage(byteArray) {
                    nextImage(it.result.toString()) //как только она загрузится, приходит ссылка этой картинки в виде it.result
                }
            }

        } else {    //ситуация, когда в ImageAdapter нет больше картинок, но мы не прошли все позиции. Например, у нас было три картинки, а после редактирования осталась одна. Но нам нужно проверить еще другие две позиции, ведь все еще находятся ссылка на старые картинки
            if (oldUrl.startsWith("http")) {   //все ссылки от Firebase на наши картинки начинаются на "http". Во viewPager отредактированное объявление пользователем больше нет картинок, но старые картинки есть. Их нужно удалить, иначе они будут накапливаться в базе данных
                //происходит удаление старой картинки на firebase по обновлению картинок пользователем на смартфоне
                deleteImageByUrl(oldUrl) {
                    nextImage("empty")
                }
            } else {
                nextImage("empty")
            }
        }
    }

    //функция для удаления старых картинок, которые убрал пользователь при редактировании объявлений
    private fun deleteImageByUrl(oldUrl: String, listener: OnCompleteListener<Void>) {
        dbManager.dbStorage.storage
            .getReferenceFromUrl(oldUrl)
            .delete().addOnCompleteListener(listener)
    }

    private fun nextImage(uri: String) {
        setImageUriToAd(uri)    //ссылку, которую мы получили на картинку в uploadImages, нужно записать в наше объявление, ведь мы хотим ее сохранить вместе с текстом
        imageIndex++    //прежде чем увеличить счетчик на 1, мы выбираем setImageUriToAd()
        uploadImages()  // *  .Далее, если есть картинки, которые мы добавили к объявлению, функция сама себя перезапускает, поскольку есть еще картинки
    }

    //функция, которая нужна для того, чтобы брать данные из массива функции uploadImages и загружала в mainImage, image2, image3 класса Ad
    private fun setImageUriToAd(uri: String) {
        when(imageIndex) {                          //это условие проверяет позицию, на которую мы только что загрузили картинку
            0 -> ad = ad?.copy(mainImage = uri) //ссылка, которую мы хотим опубликовать впервые запишется в mainImage
            1 -> ad = ad?.copy(image2 = uri)
            2 -> ad = ad?.copy(image3 = uri)
        }
    }

    //Функция-счетчик для того чтобы брать ссылки для картинки, соответсвующие счетчику. Это необходимо при редактировании картинок в объявлении. Будут перезаписываться все позиции картинок объявления
    private fun getUrlFromAd(): String {
        return listOf(ad?.mainImage!!, ad?.image2!!, ad?.image3!!)[imageIndex]
    }

    //подготвка картинки к загрузке в базу
    private fun prepareImageByteArray(bitMap: Bitmap): ByteArray {
        val outStream = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG, 20, outStream)
        return outStream.toByteArray()
    }

    //загрузка картинки в базу
    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>) {
        val imStorageRef = dbManager.dbStorage  //Ссылка на то место, где мы хотим сохранить данную картинку
            .child(dbManager.auth.uid!!)    //берем аутоидентификатор пользователя, и под ним уже все записываем
            .child("image_${System.currentTimeMillis()}")   //создаем узел, где будут хранится картинки пользователя. Присваиваем название картинки, взяв время, чтобы одна и та же картинка не перазаписывалась, и не выдумывая каждый раз названия картинки
        val upTask = imStorageRef.putBytes(byteArray)   //uploadTask  - загрузка картинки. На путь и название ранее созданные добавляем байты
        upTask.continueWithTask{    //узнаю, когда завершилась загрузка
            task -> imStorageRef.downloadUrl    //мы получаем нашу ссылку
        }.addOnCompleteListener(listener)   //проверяем, получили картинку или нет. Но я не хочу, чтобы эта проверка запускалась в этой функции, я хочу передать интерфейс, и запустить там, где я буду запускать функцию uploadImages()
    }

    private fun updateImage(byteArray: ByteArray, url:String, listener: OnCompleteListener<Uri>) {
        val imStorageRef = dbManager.dbStorage.storage.getReferenceFromUrl(url)
        val upTask = imStorageRef.putBytes(byteArray)
        upTask.continueWithTask{
                task -> imStorageRef.downloadUrl
        }.addOnCompleteListener(listener)
    }

    private fun imageChangeCounter() {
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){  //регистирируем специальный коллбек, который будет следить за показом актуального фото и отображением позиции. Данный колбек будет запускатся всякий раз, когда мы скролим картинки
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateImageCounter(position)
            }
        })
    }

    //функция для обновления счетчика фото
    private fun updateImageCounter(counter: Int) {
        var index = 1
        val itemCount = binding.vpImages.adapter?.itemCount
        if(itemCount == 0) index = 0
        val imageCounter = "${counter+index}/$itemCount"      //Создаем переменную для заполнения
        binding.tvImageCounter.text =  imageCounter //заполняем переменную нужными данными
    }

}

//* Делаем возможным быть анонимному классу null. Если он null, мы еще не создали фрагмент, и тогда можно смело запускать условие if(returnValues?.size!! > 1)
// функции onActivityResult и создавать новый фрагмент. Но если он не null, значит он уже был создан. Это значит, что если функция onActivityResult запустилась,
// тобишь мы ее запустили из этого фрагмента

//Добавление объявления в базу данных состоит из двух этапов:
//1) Загружаем картинку на Firebase Storage.
//2) Нам выдает ссылку, и ссылку(текстовую часть) загружаем уже в realtime Database
//Но мы не сразу публикуем объявление, пока все картинки не загрузятся, пока все ссылки не получим, мы не опубликуем наше объявление. Пока что все записывается в класс Ad

/*
Редактирование картинок происходит следующим образом:
    Когда мы открываем экран для редактирования- открывается EditAdsAct и сюда мы передаем объект ad класса Ad и в нем есть информация, картинки, которые там есть, все текстовые описания
    Он изначально null, но если мы заходим для редактирования, то мы получаем intent из MainActivity, класс Ad уже не null, он заполнен. Но мы не знаем сколько картинок приходит. Вот этим
    и отличается создание нового объявления от редактирования. При создании ad = null, а когда мы редактируем, мы открыли экран для редактиования, передали всю информацию. По картинкам и нужно
    решать что с ними делать: если мы вернулись на экран после выбора картинок где предположим было три ссылки на картинки а по возвращению осталась только одна, - мы жмем опубликовать, нужно
    две ссылки удалить а одну оставить. А если выбрали другие картинки, их нужно перезаписать. Все это нужно сделать основываясь на классе Ad
    В функции onclickPublish() вызывалась функция uploadImages(). До 100 урока редактировля текст в объявлении. Теперь нужно сделать редактирование картинок
    Функция upLoadImages() запускается столько раз, сколько есть картинок в ImageAdapter. Нужно учитывать что там было до редактирования
    Меняем условие if(imageIndex == 3) - теперь функция будет запускаться столько раз, сколько может быть картинок в объявлении
    Мы будем брать старую ссылку от старой картинки и перезаписывать старую картинку
    Необходимо создать функцию getUrlFromAd, которая будет по-очереди брать картинки. Тогда есть imageIndex - cчетчик, который запускается каждый раз и также с помощью этого счетчика мы будем брать ссылки
 */