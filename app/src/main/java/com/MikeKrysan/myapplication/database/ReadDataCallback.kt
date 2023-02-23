package com.MikeKrysan.myapplication.database

import com.MikeKrysan.myapplication.data.Ad

interface ReadDataCallback {
    fun readData(list : List<Ad>)
}