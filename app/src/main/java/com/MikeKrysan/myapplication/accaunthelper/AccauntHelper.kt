package com.MikeKrysan.myapplication.accaunthelper

import android.widget.Toast
import com.MikeKrysan.myapplication.MainActivity
import com.MikeKrysan.myapplication.R
import com.google.firebase.auth.FirebaseUser

class AccauntHelper(act:MainActivity) {     //5.12.2 Cоздаем конструктор, чтобы мы могли взять Firebase authentication из MainActivity, чтобы регистрироваться
    private val actAcH = act    //делаем доступным объект из конструктора для всего класса
    fun signUpWithEmail(email:String, password:String) {    //5.12.1 Создаем функции для регистрации. Как только пользователь введет почту и пароль в диалоговом окне и нажмет кнопку зарегистрироваться, эта функция получит эти данные
        if(email.isNotEmpty() && password.isNotEmpty()) {//isNotEmpty даст на выходе либо true либо false; проверяем, все ли данные введены, чтобы была возможность зарегистрироваться
            actAcH.myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->  //5.13.1 Нам нужно взять Firebase authentication, чтобы регистрироваться, и это мы можем передать с помощью MainActivity, ведь там мы и будем брать authentication а сюда его просто передавать.
                // После того, как была проинициализирована переменная myAuth, далее я могу получить доступ чтобы регистрироваться и т.д. Функция ждет от нас два String и обязательно добавляем слушателя

                if (task.isSuccessful) {    //5.15 Если успешная регистрация, запускается эта часть программы. Для успешной регистрации необходимо вводить пароль не менее чем 6 символов
                    sendEmailVerification(task.result?.user!!) //5.16 Отсюда отправляем верификацию емейла. !! - берем на себя ответсвенность, что во входящей строке не будет null, потому что kotlin не может проверить входящую с сервера информацию
                }   else {
                    Toast.makeText(actAcH, actAcH.resources.getString(R.string.sign_up_error), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //Чтобы нас не спамили, или не регистрировались боты, мы вводим функцию, с помощью которой на почту пользователю будет приходить запрос на подтверждение верификации
    private fun sendEmailVerification(user: FirebaseUser) {  //5.14; С помощью FirebaseUser будет отправлятся письмо на почту пользователя. Откуда возьмем пользователя? После того как он успешно зарегистрировался, у него появляется user - FirebaseAuth в себе несет юзера(myAuth). Если пользователь не зарегистрировался, то user будет null
        user.sendEmailVerification().addOnCompleteListener {    task->
            if(task.isSuccessful) {
                Toast.makeText(actAcH, actAcH.resources.getString(R.string.send_verification_done), Toast.LENGTH_LONG).show()  //Всплывает тост, что пользователю на почту отправлено уведомление для подтверждения регистрации
            }   else {
                Toast.makeText(actAcH, actAcH.resources.getString(R.string.send_verification_error), Toast.LENGTH_LONG).show()    //Показываем, что ошибка отправки подтверждения на почту
            }
        }
    }
}