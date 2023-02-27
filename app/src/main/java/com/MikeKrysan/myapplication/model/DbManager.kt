package com.MikeKrysan.myapplication.model

import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val db = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publishAd(ad: Ad, finishListener: FinishWorkListener) {
        if(auth.uid != null)db.child(ad.key ?: "empty")
            .child(auth.uid!!).child("ad")
            .setValue(ad).addOnCompleteListener{
//                if(it.isSuccessful)   //можно проверить сообщением тост, передались ли данные на базу данных
                finishListener.onFinish()
            }
    }

    fun getMyAds(readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAds(readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild(auth.uid + "/ad/price")
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
                    val ad = item.children.iterator().next().child("ad").getValue(Ad::class.java)
                    if(ad != null) adArray.add(ad)
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

}