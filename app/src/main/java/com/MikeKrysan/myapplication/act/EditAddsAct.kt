package com.MikeKrysan.myapplication.act

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.MikeKrysan.myapplication.databinding.ActivityEditAddsBinding

class EditAddsAct : AppCompatActivity() {
    private lateinit var rootElementForEditAddsAct:ActivityEditAddsBinding//11.12.1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Чтобы получить доступ к элементам экрана, необходимо создать root-элемент
        rootElementForEditAddsAct = ActivityEditAddsBinding.inflate(layoutInflater)   //11.12.2
        setContentView(rootElementForEditAddsAct.root)    //11.12.3

    }
}