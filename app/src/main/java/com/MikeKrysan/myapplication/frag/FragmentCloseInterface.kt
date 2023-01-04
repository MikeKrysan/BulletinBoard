package com.MikeKrysan.myapplication.frag

interface FragmentCloseInterface {
//    fun onFragClose(list : ArrayList<String>)   //20.9
    fun onFragClose(list : ArrayList<SelectImageItem>)  // 20.10.2 Если мы делаем без перегрузки, вторым методом
}