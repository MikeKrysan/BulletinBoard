package com.MikeKrysan.myapplication.act

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
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
    }

    //функция, которая будет получать ссылки и класс Ad *
    private fun getIntenttFromMainAct() {
        val ad = intent.getSerializableExtra("AD") as Ad    //получаем наше объявление. Берем из intent, который к нам приходит. Передаю с помощью ключевого слова "AD". Получить как Ad класс
        updateUI(ad)
    }

    //Создаем функцию для обновления юзер-интерфейса:
    private fun updateUI(ad: Ad) {
        fillImageArray(ad)  //С принятого класса я хочу получить ссылки. Для этого создадим функцию fillImageArray()
        fillTextViews(ad)
    }

    //Функция для заполнения тектовой части
    private fun fillTextViews(ad: Ad) = with(binding) {
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvPrice.text = ad.price
        tvTel.text = ad.tel
//        tvEmail.text = adEmail    //нужно добавить
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

    //Функция для заполнения картинок. Функция запустится на второстепенном потоке, после окончания работы на нем, должна запустится обновление нашего адаптера во ViewPager на главном потоке
    private fun fillImageArray(ad: Ad) {
        val listUris = listOf(ad.mainImage, ad.image2, ad.image3)   //Из объявления Ad достаем ссылки на картинки
        //создаем корутин, указывем, на каком потоке будет работать. Указываю, что на основном. Запускаем корутину функцией launch{}:
        CoroutineScope(Dispatchers.Main).launch {
            val bitMapList = ImageManager.getBitmapFromUris(listUris)
            adapter.update(bitMapList as ArrayList<Bitmap>)
        }
    }

}

// * - Ссылки будут приходить в intent из mainActivity, потому что на mainActivity загружается список с объявлениями. И когда пользователь нажимает на фото объявления, то закрывается главное активити
// и осуществляется переход на DescriptionActivity. Это делается с помощью intent. В intent мы также вложим само объявление (класс Ad) - там и будут ссылки и все описание.
// Класс Ad сериализован, то-есть мы можем его весь передавать, не нужно каждую переменную передавать