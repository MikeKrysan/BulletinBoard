package com.MikeKrysan.myapplication.dialogHelper

import android.app.AlertDialog
import com.MikeKrysan.myapplication.MainActivity
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.accaunthelper.AccauntHelper
import com.MikeKrysan.myapplication.databinding.SignDialogBinding

/**
 * 5.2 Создали класс DialogHelper.В этот класс мы будем передавать MainActivity
 * 5.2.1 Создаем конструктор, в который будем передавать с MainActivity мое активити
 */

class DialogHelper(act: MainActivity) {
    private val actDH = act//5.2.3 Из конструктора объект builder инициализировать нельзя, его нужно передать из контруктора на уровень класса
    private val accHelper = AccauntHelper(act)  //5.17 У нас теперь есть объект AccauntHelper - accHelper с помощью которого мы можем регистрироваться

    fun createSignDialog(index: Int) {    //Поскольку к данной функции будет идти обращение из класса MainActivity, то мы делаем ее публичной;  //5.5
        val builder = AlertDialog.Builder(actDH)     //5.2.2 Builder - специальный класс, который будет создавать наш диалог. Чтобы классу Builder инициализировать объект builder, ему нужно передать контекст
        //5.2.4 Builder инициализирован, теперь с его помощью создаем диалог (создается как обычный экран):
        val rootDialogElement = SignDialogBinding.inflate(actDH.layoutInflater)   //5.2.5 Для того, чтобы получить inflate, тоже нужен контекст
        val view = rootDialogElement.root   //5.2.6 Выдает ConstraintLayout нашего окна, и все, что он внутри содержит

        if (index == DialogConst.SIGN_UP_STATE) {    //5.7 С помощью констант определяем диалоговое окна для входа или для регистрации
            rootDialogElement.tvSignTitle.text = actDH.resources.getString(R.string.ac_sign_up)     //5.8 textView диалогового окна покажет текст "Регистрация", когда зашли для регистрации
            rootDialogElement.btSignUpIn.text = actDH.resources.getString(R.string.sign_up_action)
        } else {
            rootDialogElement.tvSignTitle.text = actDH.resources.getString(R.string.ac_sign_in)
            rootDialogElement.btSignUpIn.text = actDH.resources.getString(R.string.sign_in_action)
        }

        rootDialogElement.btSignUpIn.setOnClickListener {   //5.18
            if (index == DialogConst.SIGN_UP_STATE) {
                accHelper.signUpWithEmail(rootDialogElement.edSignEmail.text.toString(), rootDialogElement.edSignPassword.text.toString())
            } else {

            }
        }
        builder.setView(view)   //5.2.7 Создается диалог, но он еще не рисуется на экране.
        builder.show()//5.2.8 Чтобы диалог отрисовался на экране, вызываем функцию

    }

}