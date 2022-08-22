package com.MikeKrysan.myapplication.accaunthelper

import android.util.Log
import android.widget.Toast
import com.MikeKrysan.myapplication.MainActivity
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.constants.FirebaseAuthConstants
import com.MikeKrysan.myapplication.dialogHelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class AccauntHelper(act:MainActivity) {     //5.12.2 Cоздаем конструктор, чтобы мы могли взять Firebase authentication из MainActivity, чтобы регистрироваться
    private val actAcH = act    //делаем доступным объект из конструктора для всего класса
    private lateinit var googleSignInClient:GoogleSignInClient  //8.6 Присвоим возвращенного клиента переменной

    fun signUpWithEmail(email:String, password:String) {    //5.12.1 Создаем функции для регистрации. Как только пользователь введет почту и пароль в диалоговом окне и нажмет кнопку зарегистрироваться, эта функция получит эти данные
        if(email.isNotEmpty() && password.isNotEmpty()) {//isNotEmpty даст на выходе либо true либо false; проверяем, все ли данные введены, чтобы была возможность зарегистрироваться
            actAcH.myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->  //5.13.1 Нам нужно взять Firebase authentication, чтобы регистрироваться, и это мы можем передать с помощью MainActivity, ведь там мы и будем брать authentication а сюда его просто передавать.
                // После того, как была проинициализирована переменная myAuth, далее я могу получить доступ чтобы регистрироваться и т.д. Функция ждет от нас два String и обязательно добавляем слушателя

                if (task.isSuccessful) {    //5.15 Если успешная регистрация, запускается эта часть программы. Для успешной регистрации необходимо вводить пароль не менее чем 6 символов
                    sendEmailVerification(task.result?.user!!) //5.16 Отсюда отправляем верификацию емейла. !! - берем на себя ответсвенность, что во входящей строке не будет null, потому что kotlin не может проверить входящую с сервера информацию
                    actAcH.uiUpdate(task.result?.user)   //6.3.1 В данном случае нам нужно, чтобы user был null, в случае, когда пользователь еще не зарегистрирован. Мы зарегистрировались, и автоматически входим в аккаунт. В хедере бокового меню указываем емейл пользователя
                }   else {
//                    Toast.makeText(actAcH, actAcH.resources.getString(R.string.sign_up_error), Toast.LENGTH_LONG).show()
                    Log.d("MyLog", "Exception: " + task.exception)  //9.1 для того чтобы обработать ошибку, нужно ее отловить: сначала ищем какие ошибки может выдать AS, далее мы ее обрабатываем if else выводя сообщение или закрывая что-то в программе к примеру
                    //мы отследили две ошибки:
                    //1 Exception: com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.
                    //2 Exception: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.
                    if(task.exception is FirebaseAuthUserCollisionException) {
                        val exception = task.exception as FirebaseAuthUserCollisionException    //делаем даунакаст. Мы теперь точно знаем, что это ошибка этого класса. Теперь мы можем делать следущее:
//                        Log.d("MyLog", "Exception: ${exception.errorCode}")      //9.1.1 Проверяем по константе. Фильтруем ошибку в классе -  Exception: ERROR_EMAIL_ALREADY_IN_USE
                        if(exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                            Toast.makeText(actAcH, FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE, Toast.LENGTH_LONG).show()
                            //Здесь соединяем логику регистрации гугл-аккаунта с регистрацией по почте
                        }
                    } else if(task.exception is FirebaseAuthInvalidCredentialsException) {      //9.1.2 обрабатываем ошибку на неправельно введенный емайл
                        val exception = task.exception as FirebaseAuthInvalidCredentialsException
//                        Log.d("MyLog", "Exception: ${exception.errorCode}")
                        if(exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                            Toast.makeText(actAcH, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show()
                        }
                    }
                    if(task.exception is FirebaseAuthWeakPasswordException) {   //9.4
                        val exception = task.exception as FirebaseAuthWeakPasswordException
//                        Log.d("MyLog", "Exception: ${exception.errorCode}")
                        if(exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                            Toast.makeText(actAcH, actAcH.resources.getString(R.string.error_weak_password), Toast.LENGTH_LONG).show()
                        }
                    }
                    else {
                        Toast.makeText(actAcH, actAcH.resources.getString(R.string.sign_in_error), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun signInWithEmail(email:String, password:String) {   //6.7
        if (email.isNotEmpty() && password.isNotEmpty()) {
            actAcH.myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        //Мы входим поэтому уже не нужно снова подтверждать наш email адрес через почту
                        actAcH.uiUpdate(task.result?.user)
                    } else {                                                                //9.1.3 Обработка неправильно введенного емейла при входе
//                        Log.d("MyLog", "Exception: " + task.exception)  //9.3 Действуем так же само: сразу находим общую обибку (класс ошибок)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Log.d("MyLog", "Exception: ${task.exception}")  //9.3.2 Отслеживаем далее на общие ошибки
                            val exception = task.exception as FirebaseAuthInvalidCredentialsException
//                            Log.d("MyLog", "Exception: ${exception.errorCode}")   //9.3.1- Неправильный емайл или пароль, или такого емейла не существует
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                                Toast.makeText(actAcH, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show()
                            } else if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                                Toast.makeText(actAcH, FirebaseAuthConstants.ERROR_WRONG_PASSWORD, Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                }
        }
    }


    private fun getSignInClient():GoogleSignInClient{  //8.2 функция должна вернуть googleSignInClient. Но мы не можем получить все необходимые классы из библиотеки, потому как у нас нет этой библиотеки. Нужно ее добавить
        //GoogleSignInClient - данный класс позволяет отправить специальный intent(сообщение) к системе, что наш акканут находится не внутри нашего приложения, а находится на смартфоне. Из нашего приложения нужно отравить запрос в систему и ждать результата, когда система ответит
        //Но сообщение это нам нужно настроить с помощью специальных опций, с помощью GoogleSignInOptions:
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(actAcH.getString(R.string.default_web_client_id)).requestEmail().build()     //8.4 запрашиваем и токен и имейл
        return GoogleSignIn.getClient(actAcH, gso)  //8.5
    }

    fun signInWithGoogle(){     //8.7
        googleSignInClient = getSignInClient()
        val intent = googleSignInClient.signInIntent   //8.7.1 Создаем intent
        actAcH.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE )   //8.9
    }

    fun signInFirebaseWithGoogle(token:String){ //8.12
        val credential =  GoogleAuthProvider.getCredential(token, null) //даем полномочия Firebase регистрировать акаунт гугл для нашего приложения
        actAcH.myAuth.signInWithCredential(credential).addOnCompleteListener{   task->
            if(task.isSuccessful) {
                Toast.makeText(actAcH, "Sign in done", Toast.LENGTH_LONG).show()
                actAcH.uiUpdate(task.result?.user)  //8.14
            } else {
                Log.d("MyLog", "Google Sign In Exception: ${task.exception}")   //9.1.4 Отлавливаем возможные ошибки регистрации через гугл аккаунт(сбой сети например)
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