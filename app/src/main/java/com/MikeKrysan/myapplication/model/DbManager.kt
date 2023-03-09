package com.MikeKrysan.myapplication.model

import com.MikeKrysan.myapplication.utils.FilterManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DbManager {
    val db = Firebase.database.getReference(MAIN_NODE)
    val dbStorage = Firebase.storage.getReference(MAIN_NODE)
    val auth = Firebase.auth

    fun publishAd(ad: Ad, finishListener: FinishWorkListener) {
        if (auth.uid != null) db.child(ad.key ?: "empty")
            .child(auth.uid!!).child(AD_NODE)
            .setValue(ad).addOnCompleteListener {
//                if(it.isSuccessful)   //можно проверить сообщением тост, передались ли данные на базу данных
//                val adFilter = AdFilter(ad.time, "${ad.category}_${ad.time}")// Создаем переменные для времени и категории. Создаем шаблон
                val adFilter = FilterManager.createFilter(ad)
                //Как только закончили публиковать объявление, публикуем также фильтр:
                db.child(ad.key ?: "empty").child(FILTER_NODE)
                    .setValue(adFilter).addOnCompleteListener {
                        finishListener.onFinish()
                    }
            }
    }

    fun adViewed(ad: Ad) {
        var counter = ad.viewsCounter!!.toInt()
        counter++
        if(auth.uid != null)db.child(ad.key ?: "empty")
            .child(INFO_NODE).setValue(InfoItem(counter.toString(), ad.emailCounter, ad.callsCounter))
    }
    //функция, которая будет определять, какую из двух функций необходимо запустить: addToFavs() или removeFromFavs
    fun onFavClick(ad: Ad, listener: FinishWorkListener) {
        if(ad.isFav) {
            removeFromFavs(ad, listener)
        } else {
            addToFavs(ad, listener)
        }
    }

    private fun addToFavs(ad: Ad, listener: FinishWorkListener) {
        ad.key?.let {
            auth.uid?.let {
                    uid -> db.child(it)
                .child(FAVS_NODE)
                .child(uid)
                .setValue(uid).addOnCompleteListener {
                        if(it.isSuccessful) listener.onFinish()
                    }
            }
        }
    }

    private fun removeFromFavs(ad: Ad, listener: FinishWorkListener) {
        ad.key?.let {
            auth.uid?.let {
                    uid -> db.child(it)
                .child(FAVS_NODE)
                .child(uid)
                .removeValue().addOnCompleteListener {
                    if(it.isSuccessful) listener.onFinish()
                }
            }
        }
    }

    fun getMyAds(readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getMyFavs(readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild("/favs/${auth.uid}").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    //функция, которая будет брать первую порцию объявлений с базы данных и показывать последние объявления
    fun getAllAdsFirstPage(readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild(AD_FILTER_TIME).limitToLast(ADS_LIMIT) //Закрытый путь проверки безопасности, мы проверяем только те объявления, которые мы создали сами - поменяли на время. startAfter - запускаем последующие объявления, не включая последнее
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAdsNextPage(time: String, readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild(AD_FILTER_TIME).endBefore(time).limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }

    //функция, в которой мы берем первую страницу в выбранной категории, и сортируем от свежего к более старому объявлению
    fun getAllAdsFromCatFirstPage(cat: String, readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild(AD_FILTER_CAT_TIME).startAt(cat).endAt(cat + "_\uf8ff").limitToLast(ADS_LIMIT)   //uf8ff - специальный символ, который дополняет то, чего нет. Время я не знаю, отсортируется в порядке возрастания по времени (временный код)
        readDataFromDb(query, readDataCallback)
    }

    //Функция, которая будет брать не первую страницу категории объявлений, а следующую
    fun getAllAdsFromCatNextPage(catTime: String, readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild(AD_FILTER_CAT_TIME).endBefore(catTime).limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }

    fun deleteAd(ad: Ad, listener: FinishWorkListener) {
        if(ad.key == null || ad.uid == null) return
        db.child(ad.key).child(ad.uid).removeValue().addOnCompleteListener{
            if(it.isSuccessful) listener.onFinish()
//            else Toast.makeText(this, "Не удалось удалить объявление", ...)
        }
    }

    private fun readDataFromDb(query: Query, readDataCallback: ReadDataCallback?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adArray = ArrayList<Ad>()
                for(item in snapshot.children) {

                    var ad: Ad? = null
                    item.children.forEach {
                        if(ad == null) ad = it.child(AD_NODE).getValue(Ad::class.java)
                    }
                    val infoItem = item.child(INFO_NODE).getValue(InfoItem::class.java)

                    val favCounter = item.child(FAVS_NODE).childrenCount
                    val isFav = auth.uid?.let { item.child(FAVS_NODE).child(it).getValue(String::class.java) }   //делаем безопасный запуск по идентификатору. Если не null, то все что находится в let запуститься и туда передастся it, а it это и есть наш идентификатор. Т.о. я пытаюсь взять наи идентификатор
                    ad?.isFav = isFav != null   //Если isFav null, значит строкой выше мы не нашли идентификатора, и это значит, что сюда запишется false. Если выше в строке не null, то здесь запишеться true
//                    Log.d("MyLog", "Counter favs: $favCounter")
                    ad?.favCounter = favCounter.toString()


                    ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                    ad?.emailCounter = infoItem?.emailsCounter ?: "0"
                    ad?.callsCounter = infoItem?.callsCounter ?: "0"
                    if(ad != null) adArray.add(ad!!)
//                    Log.d("MyLog", "Data: ${ad?.tel}")
                }
                readDataCallback?.readData(adArray)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    interface ReadDataCallback {
        fun readData(list : ArrayList<Ad>)
    }

    interface FinishWorkListener {
        fun onFinish()
    }

    companion object{
        const val AD_NODE = "ad"
        const val FILTER_NODE = "adFilter"
        const val MAIN_NODE = "main"
        const val INFO_NODE = "info"
        const val FAVS_NODE = "favs"
        const val ADS_LIMIT = 2
        const val AD_FILTER_TIME = "/adFilter/time"
        const val AD_FILTER_CAT_TIME = "/adFilter/cat_time"

    }

}