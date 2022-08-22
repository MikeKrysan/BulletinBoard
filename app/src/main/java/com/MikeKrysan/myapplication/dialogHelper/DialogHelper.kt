package com.MikeKrysan.myapplication.dialogHelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
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
    val accHelper = AccauntHelper(act)  //5.17 У нас теперь есть объект AccauntHelper - accHelper с помощью которого мы можем регистрироваться

    fun createSignDialog(index: Int) {    //Поскольку к данной функции будет идти обращение из класса MainActivity, то мы делаем ее публичной;  //5.5
        val builder = AlertDialog.Builder(actDH)     //5.2.2 Builder - специальный класс, который будет создавать наш диалог. Чтобы классу Builder инициализировать объект builder, ему нужно передать контекст
        //5.2.4 Builder инициализирован, теперь с его помощью создаем диалог (создается как обычный экран):
        val rootDialogElement = SignDialogBinding.inflate(actDH.layoutInflater)   //5.2.5 Для того, чтобы получить inflate, тоже нужен контекст
        val view = rootDialogElement.root   //5.2.6 Выдает ConstraintLayout нашего окна, и все, что он внутри содержит
        builder.setView(view)   //5.2.7 Создается диалог, но он еще не рисуется на экране.

        setDialogState(index, rootDialogElement)    //7.2.1 Вызвали созданную функцию


        val dialog = builder.create()   //6.6

        rootDialogElement.btSignUpIn.setOnClickListener {   //5.18
            setOnClickSignUpIn(index, rootDialogElement, dialog)    //7.3.1
        }
        rootDialogElement.btForgotP.setOnClickListener {   //7.4
            setOnClickResetPassword(rootDialogElement, dialog)
        }

        rootDialogElement.btGoogleSignIn.setOnClickListener {   //8.11
            accHelper.signInWithGoogle()
        }

//        builder.show()//5.2.8 Чтобы диалог отрисовался на экране, вызываем функцию
        dialog.show()   //6.6.2

    }

    private fun setOnClickResetPassword(rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {   //7.5 создали функцию для обработки сценария забытого пароля
        if(rootDialogElement.edSignEmail.text.isNotEmpty()) {//7.5.1 Убедимся, что email адресс не пустой
            actDH.myAuth.sendPasswordResetEmail(rootDialogElement.edSignEmail.text.toString()).addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    Toast.makeText(actDH, R.string.email_reset_password_was_sent, Toast.LENGTH_LONG).show()
                }

            }
            dialog?.dismiss()    //7.5.3 - теперь время запустить dismiss - сворачивание диалогового экрана
        }   else {
            rootDialogElement.tvDialogMessage.visibility = View.VISIBLE //7.5.2 Пользователю будет показан диалог для ввода email. Когда он введет ее, то выполнится первое условие проверки, так как окно почты уже не пустое
        }
    }

    private fun setOnClickSignUpIn(index: Int, rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {    //7.3
        dialog?.dismiss()    //6.6.1    //7.3.2 теперь в функции наш dialog может быть null, указываем поэтому вопросительный знак
        if (index == DialogConst.SIGN_UP_STATE) {
            accHelper.signUpWithEmail(rootDialogElement.edSignEmail.text.toString(), rootDialogElement.edSignPassword.text.toString())
        } else {
            accHelper.signInWithEmail(rootDialogElement.edSignEmail.text.toString(), rootDialogElement.edSignPassword.text.toString()) //6.7.1
        }
    }

    private fun setDialogState(index: Int, rootDialogElement: SignDialogBinding) {  //7.2 Переносим условия проверки в отдельную функцию
        if (index == DialogConst.SIGN_UP_STATE) {    //5.7 С помощью констант определяем диалоговое окна для входа или для регистрации
            rootDialogElement.tvSignTitle.text = actDH.resources.getString(R.string.ac_sign_up)     //5.8 textView диалогового окна покажет текст "Регистрация", когда зашли для регистрации
            rootDialogElement.btSignUpIn.text = actDH.resources.getString(R.string.sign_up_action)
        } else {
            rootDialogElement.tvSignTitle.text = actDH.resources.getString(R.string.ac_sign_in)
            rootDialogElement.btSignUpIn.text = actDH.resources.getString(R.string.sign_in_action)
            rootDialogElement.btForgotP.visibility = View.VISIBLE   //7.1
        }
    }

}