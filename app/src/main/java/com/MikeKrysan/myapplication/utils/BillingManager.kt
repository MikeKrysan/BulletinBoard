package com.MikeKrysan.myapplication.utils

import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*

//Будем пользоваться контекстом переданного класа:
class BillingManager(val act:AppCompatActivity) {
    private var billingClient: BillingClient? = null

    init {
        setUpBillingClient()
    }

    //функция, в которой мы настраиваем billingClient
    private fun setUpBillingClient() {
        billingClient = BillingClient.newBuilder(act).setListener(getPurchaseListener()).enablePendingPurchases().build()
    }

    fun startConnection() {
        //все настроено
        billingClient?.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {

            }
            //прошло соединение с playMarket
            override fun onBillingSetupFinished(result: BillingResult) {
                getItem()
            }
        })
    }

    private fun getItem() {
        val skuList = ArrayList<String>()     //создаем список с нашими покупками
        skuList.add(REMOVE_ADS)
        val skuDetails = SkuDetailsParams.newBuilder()   //информация о покупке. На плеймаркет нужно будет передать не просто стринг, а информацию о нашей покупке
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)    //В список передаем информацию о покупке и что это за продукты - встроенные покупки(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(skuDetails.build()) {   //запустили запрос
            result, list ->
            run {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    if(!list.isNullOrEmpty()) {
                        val billingFlowParams =  BillingFlowParams.newBuilder().setSkuDetails(list[0]).build()  //собранная переменная в которой проверено, что есть такая покупка на плеймаркете
                        billingClient?.launchBillingFlow(act, billingFlowParams)  //запуск лаунчера покупки, который вызовет уже готовый настроенный диалог
                    }
                }
            }
        }
    }

    //функция, которая будет слушать пользователя, который хочет сделать покупку
    private fun getPurchaseListener(): PurchasesUpdatedListener {
        //PurchasesUpdatedListener - класс который следит за состоянием карты клиента, его возможностью оплаты
        return PurchasesUpdatedListener {
            result, list ->
            run {
                if (result.responseCode == BillingClient.BillingResponseCode.OK)        //Мы проверяем все ли прошло успешно(работает ли карта у пользователя, не заблокирована она и т.д). Мы пока не проверяем оплачено или нет
                    list?.get(0).let{  }    //Если список не пустой, то-есть все прошло успешно, покупатель может реализовать покупку. Здесь покупка будет одобрена
            }
        }
    }

    companion object {
        const val REMOVE_ADS = "remove_ads"
    }
}

