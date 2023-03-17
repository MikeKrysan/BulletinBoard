package com.MikeKrysan.myapplication.utils

import android.content.Context
import android.provider.Settings.Global.getString
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.MikeKrysan.myapplication.R
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

    //функция для сохранения sharedPreference
    private fun savePurchase(isPurchased: Boolean) {
        val pref = act.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(REMOVE_ADS_PREF, isPurchased)
        editor.apply()
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

    //функция для подтверждения покупки
    private fun nonConsumableItem(purchase: Purchase) {
        //если продукт куплен, то его нужно подтвердить
        if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            //если продукт не подтвержден, то запустим подтверждение данного продукта
            if(!purchase.isAcknowledged) {
                val acParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()  //подтверждение покупки пользователя
                billingClient?.acknowledgePurchase(acParams) {  result ->
                    //мы успешно подтвердили данную покупку и теперь пользоватеь является владельцем данной покупки
                    if(result.responseCode == BillingClient.BillingResponseCode.OK) {
                        savePurchase(true)
                        Toast.makeText(act, act.resources.getString(R.string.thanks_you_for_your_purchase), Toast.LENGTH_SHORT).show()
                    } else {
                        savePurchase(false)
                        Toast.makeText(act, act.resources.getString(R.string.failed_to_complete_the_purchase), Toast.LENGTH_SHORT).show()
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
                    list?.get(0)?.let{ nonConsumableItem(it) }    //Если список не пустой, то-есть все прошло успешно, покупатель может реализовать покупку. Здесь покупка будет одобрена
            }
        }
    }

    fun closeConnection() {
        billingClient?.endConnection()
    }

    companion object {
        const val REMOVE_ADS = "remove_ads"
        const val MAIN_PREF = "main_pref"   //название таблици, которую мы сохраняем
        const val REMOVE_ADS_PREF = "remove_ads_pref"   //название элемента, который мы сохраняем
    }
}

