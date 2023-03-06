package com.MikeKrysan.myapplication.act

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.widget.Toast
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.adapters.ImageAdapter
import com.MikeKrysan.myapplication.databinding.ActivityDescriptionBinding
import com.MikeKrysan.myapplication.model.Ad
import com.MikeKrysan.myapplication.utils.ImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DescriptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionBinding
    lateinit var adapter: ImageAdapter
    private var ad: Ad? = null   //выносим перемменную ad и делаем ее глобальной

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        //С помощью binding находим кнопки email и позвонить
        binding.fbTel.setOnClickListener{ call()}
        binding.fbEmail.setOnClickListener{sendEmail()}

    }

    //инициализируем наш адаптер
    private fun init() {
        adapter = ImageAdapter()    //инициализируем переменную для адаптера
        //Передаем адаптер во ViewPager:
        binding.apply{
            viewPager.adapter  = adapter    // Присваиваем viewPager-у адаптер, который только что проинициализировали
        }
//        adapter.update()    //Мы не можем сделать update() потому что у нас нет этого списка. Мы передали на Firebase поток из битов bitmap. Нам будет нужна функция, которая получается от ссылок от Firebase bitmap-ы. А это мы будем делать с помощью библиотеки Picasso
        getIntenttFromMainAct() //получаем intent
        imageChangeCounter()
    }

    //функция, которая будет получать ссылки и класс Ad *
    private fun getIntenttFromMainAct() {
        ad = intent.getSerializableExtra("AD") as Ad    //получаем наше объявление. Берем из intent, который к нам приходит. Передаю с помощью ключевого слова "AD". Получить как Ad класс
        if(ad != null) updateUI(ad!!)
    }

    //Создаем функцию для обновления юзер-интерфейса:
    private fun updateUI(ad: Ad) {
        ImageManager.fillImageArray(ad, adapter)  //С принятого класса я хочу получить ссылки. Для этого создадим функцию fillImageArray()
        fillTextViews(ad)
    }

    //Функция для заполнения тектовой части
    private fun fillTextViews(ad: Ad) = with(binding) {
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvPrice.text = ad.price
        tvTel.text = ad.tel
        tvEmail.text = ad.email
        tvCountry.text = ad.country
        tvCity.text = ad.city
        tvIndex.text = ad.index
        tvWithSend.text = isWithSend(ad.withSend.toBoolean())   //На входе в функцию мы ждем boolean. Превращаем String() в Boolean()
    }

    //Функция для значка "с отправкой" : да/нет
    private fun isWithSend(withSend: Boolean): String {
        return if(withSend) {
            binding.root.resources.getString(R.string.with_send_true)   //Boolean превращается в String когда записываем на базу данных
        } else {
            binding.root.resources.getString(R.string.with_send_false)
        }
    }

    private fun call() {
        val callUri =  "tel:${ad?.tel}" //мы не просто звоним, а отправляем номер телефона на другое приложение с помощьью которого и будем звонить. Превращаем в URI наш номер телефона
        val iCall = Intent(Intent.ACTION_DIAL)  //Интент для звонка - для того чтобы открыть приложение для звонков
        iCall.data = callUri.toUri()    //помещаем данные
        startActivity(iCall)
    }

    private fun sendEmail() {
        //создаем интент, с помощью которого мы будем открывать приложение для отправки сообщения электронной почтой
        val iSendEmail = Intent(Intent.ACTION_SEND) //Приложение, которым мы хотим открыть сообщение, у него в манифесте будет прописано, что INTENT.ACTION_SEND может открыть даное приложенипе
        iSendEmail.type = "message/rfc822"
        iSendEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email)) //Указываем специальные константы, чтобы приложение, на которое мы будем отправлять адресс эл.почты, знало, в какое поле помещать этот текст
            putExtra(Intent.EXTRA_SUBJECT, binding.root.resources.getString(R.string.email_add))
            putExtra(Intent.EXTRA_TEXT, binding.root.resources.getString(R.string.email_extra_text))
        }
        //Если у пользователя нет приложения для отправки почты
        try{
            startActivity(Intent.createChooser(iSendEmail, "Open with"))   //мы можем выбрать приложение почты, которые у нас есть в телефоне
        } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, R.string.email_apps_does_not_exist, Toast.LENGTH_LONG).show()
        }
    }

    //функция для показа на какой картинке из существующих сейчас находится просмотр
    private fun imageChangeCounter() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){  //регистирируем специальный коллбек, который будет следить за показом актуального фото и отображением позиции. Данный колбек будет запускатся всякий раз, когда мы скролим картинки
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position+1}/${binding.viewPager.adapter?.itemCount}"      //Создаем переменную для заполнения
                binding.tvImageCounter.text =  imageCounter //заполняем переменную нужными данными
            }
        })
    }

}

// * - Ссылки будут приходить в intent из mainActivity, потому что на mainActivity загружается список с объявлениями. И когда пользователь нажимает на фото объявления, то закрывается главное активити
// и осуществляется переход на DescriptionActivity. Это делается с помощью intent. В intent мы также вложим само объявление (класс Ad) - там и будут ссылки и все описание.
// Класс Ad сериализован, то-есть мы можем его весь передавать, не нужно каждую переменную передавать