package com.MikeKrysan.myapplication.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.MikeKrysan.myapplication.model.Ad
import com.MikeKrysan.myapplication.model.DbManager

class FirebaseViewModel: ViewModel() {
    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()

    fun loadAllAdsFirstPage(filter: String) {
        dbManager.getAllAdsFirstPage( filter, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsNextPage(time: String) {
        dbManager.getAllAdsNextPage(time, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsFromCat(cat: String, filter: String) {
        dbManager.getAllAdsFromCatFirstPage(cat, filter, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsFromCatNextPage(catTime: String) {
        dbManager.getAllAdsFromCatNextPage(catTime, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun onFavClick(ad: Ad) {
        dbManager.onFavClick(ad, object: DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                val pos = updatedList?.indexOf(ad)
                if(pos != -1) {
                    pos?.let{
                        val favCounter = if(ad.isFav) ad.favCounter.toInt() - 1 else ad.favCounter.toInt() + 1
                        updatedList[pos] = updatedList[pos].copy(isFav = !ad.isFav, favCounter =  favCounter.toString())    //!ad.isFav - берем значение, и записываем на противоположное. Если было true, то записываем false
                    }
                }
                liveAdsData.postValue(updatedList)
            }
        })
    }

    fun adViewed(ad: Ad) {
        dbManager.adViewed(ad)
    }

    fun loadMyAds() {
        dbManager.getMyAds(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadMyFavs() {
        dbManager.getMyFavs(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }


    fun deleteItem(ad : Ad) {
        dbManager.deleteAd(ad, object: DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                updatedList?.remove(ad)
                liveAdsData.postValue(updatedList)
            }
        })
    }
}