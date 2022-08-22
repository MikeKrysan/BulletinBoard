package com.MikeKrysan.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.MikeKrysan.myapplication.databinding.ActivityMainBinding
import com.MikeKrysan.myapplication.dialogHelper.DialogConst
import com.MikeKrysan.myapplication.dialogHelper.DialogHelper
import com.MikeKrysan.myapplication.dialogHelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.main_content.*

/**
 * Урок 1
 * 1.1 Создали папку menu в resources
 * 1.2 В разметке drawer_main_menu.xml в теге <menu > добавили tools
 * 1.3 Добавляем текст меню в values -> strings
 *
 * Урок 2
 * 2. Встраиваем выдвижное меню в activity_main.xml:
 * 2.1 Меняем ConstraintLayout на DrawerLayout в activity_main.xml
 * 2.1.1 Добавляем тег android:fitsSystemWindows="true"
 * 2.1.2 Присваиваем идентификатор для drawerlayout: android:id="@+id/drawerLayout"
 * 2.2 Добавляем NavigationView в разметку activity_main.xml
 * 2.2.1 добавляем идентификатор: navView
 * 2.2.2 app:menu="@menu/drawer_main_menu" - Указываем, какое меню мы хотим, чтобы указывалось внутри нашего контейнера NavigationView
 * 2.2.3 android:layout_gravity="start" - чтобы меню не растягивалось на весь экран (контейнер NavigationView) и выдвигалось слева направо
 * 2.2.4 android:fitsSystemWindows="true" - этот элемент будет задвинут по умолчанию слева
 * 2.2.5 в drawerLayout прописываем tools:openDrawer="start" - элемент выдвижного меню id=navView будет отображен
 *
 * 2.3 Добавим header для выдвижного меню. В нем будет картинка и почта, по которой я зарегистрировался. Картинка будет показыватся после того, как будет сделана регистрация через гугл аккаунт
 * Если регистрация произведена по почте, то картинка отображатся не будет
 * 2.3.1 Создаем layout_resources_file в папке layot и називаем его nav_header_main.xml
 * 2.3.2 Header включаем в NavigationView, который находится на activity_main : app:headerLayout="@layout/nav_header_main". Таким образом вживился nav_header_main.xml в navigationView
 *
 * 2.4. Если на данном этапе запустить приложение, то action bar будет впереди выдвигающегося меню, это будет неудобно для пользователя. Для этого спрячем action bar и status tool bar за выдвигающееся меню.
 * 2.4.1 В values->themes->themes создаем свою тему, назвав name="AppTheme.NoActionBar"
 * 2.4.2 <item name="windowActionBar">false</item> - скрываем ActionBar. Но AS попытается напечатать текст названия приложения, который прописан в манифесте, поэтому добавляем еще одно условие: <item name="windowNoTitle">true</item>
 * 2.4.3 Чтобы исчез ItemBar, нужно прописать: <item name="android:windowFullscreen">true</item>
 * 2.4.4 Делаем то же для темной темы, скопировав со светлой
 * 2.4.5 Чтобы применить созданную тему, идем в манифест и чтобы применить ко всему приложению свою тему, я пишу android:theme="@style/AppTheme.NoActionBar"
 *
 * 2.5 Добавим наш собственный ActionBar, и чтобы он был за нашим выдвижным меню:
 * 2.5.1 Создаем новое активити и даем ему название main_content.xml - это активити, на котором будет в ActionBar кнопка вызова главного меню и сам список наших объявлений
 * 2.5.2 Чтобы подключить main_content.xml к основной разметке activity_main.xml, необходимо добавить в разметку activity_main <include layout="@layout/main_content"...
 *
 * 2.6. Создаем кнопку в ActionBar для открытия меню
 * 2.6.1 Чтобы обратится напрямую к view по их идентификаторам в новых версиях котлин необходимо в build.gradle  прописать: apply plugin: 'kotlin-android-extensions'. Иначе нужно создавать переменные и в них помещать findViewById
 * 2.6.2 Создаем функцию для инициализации view fun init()
 * 2.6.3 Создадим переменную toggle. Класс ActionBarDrawerToggle не может принимать строковое значение(ожидает ресурс Int), поэтому создадим два ресурса в файле strings.xml open и close
 * 2.6.4 Указываем, что кнопка toggle будет открывать наше меню (driverLayout
 *
 * 2.7. Прослушаем нажатия кнопок нашего меню NavigationView, можно добавить для всего класса интерфейс OnNavigationItemSelectedListener
 * 2.7.1 Как нам сказать, что при нажатии на кнопки в меню NavigationView мы передаем события в интерфейс OnNavigationItemSelectedListener? navView.setNavigationItemSelectedListener(this) - данные о событии нажатия передаются в наш класс, так как интерфейс встроен в класс
 * 2.7.2 Когда мы нажали на элемент из главного меню, мы можем проверить id этого элемента
 *
 *
 * Урок 3 - подключение проекта из AS в проект, созданный на FireBase
 * 3. Tools -> FireBase -> Authentication -> Email and password authentication -> Connect to FireBase -> выбираю нужный проект, далее и проект подключен
 * 3.1 Нужно добавить библиотеки к каждой функции, которую я хочу использовать. FireBase -> Add Firebase Authentication to your app -> Accept changes. Либо же вручную добавить в buidl.gradle(Module) -> dependendcies -> implimentation (implementation 'com.google.firebase:firebase-auth:19.2.0')
 * 3.2 Мы уже подключены к базе данных, теперь добавляем библиотеку Realtime Database -> Add the Realtime Database to your app -> Accept changes
 * 3.3 Добавляем библиотеку Storage
 * 3.4 Если мы перенесли проект из AS на другой пк. То проект может выдать ошибки, для их избежания необходимо: зайти в вид проекта Project -> app -> google-services.json - этот файл является скачанным с проекта Firebase
 *  Если мы сделали изменения на FireBase  и нам в AS выдает ошибку, то нам нужно обновить этот файл. Иногда этого файла нет. Чтобы взять этот файл, идем в Firebase->Project Overview->Project settings->General->файл google-services.json скачать и заменить или добавить в проект
 *
 * Урок 4 - Инициализируем View по принципу ViewBinding
 * View Binding помагает нам создать дополнительный класс, который представляет собой разметку экрана и в себе содержит ссылки на все элементы, и через этот класс мы получаем доступ к любому элементу
 * 4.1 В build.gradle(Module) -> android прописываем:
 *      buildFeatures {
 *          viewBinding true
 *          }
 *          }
 *  а строку apply plugin: 'kotlin-android-extensions' убираем
 *  4.2 Удаляем синтетики из импорта
 *  4.3 Теперь доступ к элементам у нас лишен. Как получить к ним доступ, чтобы не использовать findViewById?
 *  Теперь каждый экран, который мы создаем, мы можем превратить в класс, который будет содержать все элементы экрана
 *  4.4 Чтобы получить доступ к toolbar, нужно обратится к экрану main_content.xml и этот экран мы вживляем в activity_main через include и чтобы получить доступ к нему, нужно создать идентификатор
 *  android:id="@+id/mainContent" в activity_main.xml , а include несет в себе main_content, так, в реальности мы получаем доступ к main_content.xml, а там у нас и находится toolbar
 *
 *  Урок 5. Создаем AlertDialog для регистрации и начинаем работать с Authentication. В данном уроке вход пока по Email
 *  5.1 Cоздаем экран разметки для диалога при регистрации и при входе: sign_dialog.xml
 *  5.2 Создаем класс для регистрации/входа, предварительно поместив его в пакет dialogHelper ->...
 *  5.3 Инициализируем DialogHelper в MainActivity
 *  5.4 Воспользуемся объектом dialogHelper при нажатии кнопки "Зарегистрироваться" и "Войти"
 *  5.5 будем изменять textView Регистрация и кнопка "Зарегистрироватся" в зависимости от того, мы входим, или регистрируемся в окне. Мы можем передать в функцию createSignDialog класса DialogHelper индекс
 *  5.6 Создаем контсанты в пакете dialogHelper для индекса функции createSignDialog(index:Int)
 *  5.7 -> DialogHelper..
 *  5.8 -> DialogHelper..
 *  5.9 -> MainActivity -> onNavigationItemSelected() ->..
 *  5.10 -> MainActivity -> onNavigationItemSelected() ->..
 *  5.11 Создаем функции, которые помогут нам регистрироваться. Firebase->Authentication->и там включаем регистрацию по имейлу и паролю->save
 *  5.12 Создаем класс AccauntHelper - он будет позволять нам регистрироваться, входить, выходить по нашему акаунту. Поместим его в пакет accaunthelper. ->..
 *  5.13 Инициализируем объект myAuth через FirebaseAuht
 *  5.14 Создаем функцию для отправки подтверждающего письма в AccauntHelper sendEmailVerification()
 *  5.15 AccauntHelper ->..
 *  5.16 AccauntHelper ->..
 *  5.17 Инициализируем AccauntHelper в DialogHelper
 *  5.18 Добавим прослушивателей нажатия на кнопки в DialogHelper
 *
 *  Урок 6. Продолжаем работать с Authentication(часть 2). Необходимо сделать функцию для входа и выхода, и чтобы это состояние отображалалось на панели nav_header_main
 *  6.1 Создадим текстовый ресурс в strings, который будет показывать, войдите либо зарегистрируйтесь. Эта надпись будет появлятся, когда мы не вошли в аккаунт, либо мы нажали выйти из аккаунта
 *  6.2 Чтобы добавить доступ к тексту "Войдите или зарегистрируйтесь" в MainActivity создаем переменную tvAccaunt, которой присвоили значение в функции init()
 *  6.3 Создадим публичную функцию uiUpdate()
 *  6.4 При нажатии кнопки "Выйти" мы запускаем функцию uiUpdate(null) и сюда передаем null
 *  6.5 Добавляем функцию onStart в MainActivity чтобы до регистрации в окне header была надписи, а не пустое место
 *  6.6 Сделаем, чтобы диалог после нажатия кнопки "Регистрация" убегал. DialogHepler->..
 *  6.7 Функция для входа AccauntHelper->.. Функцию signUInWithEmail() запускаем из DialogHelper
 *
 *  Урок 7. Восстановление пароля
 *  7.1 в классе DialogHelper в проверке на Sign_in или Sign_up показываем скрытую кнопку "Забыли пароль?"
 *  7.2 Переносим условия проверки в отдельную функцию setDialogState в классе DialogHelper
 *  7.2.1 Вызываем функцию setDialogState
 *  7.3 Переносим условия нажатия на кнопку SignUpIn в отдельную функцию  setOnClickSignUpIn класса DialogHelper
 *  7.3.1 Вызываем функцию SignUpIn
 *  7.4 Вызываем обработчки кнопки "забыли пароль?"
 *  7.6 В sign_dialog.xml вводим textView для сообщению пользователю текта ввести почту при восстановлении пароля
 *
 *  Урок 8. Авторизация по Google аккаунту.Часть 1
 *  8.1 Включаем в Firebase опцию "регистрация через гугл-аккаунт" и там запросит нас асоциировать с этим какую-то почту(по-умолчанию та, что есть уже на аккаунте)
 *  8.2 в AccauntHelper создаем функции для регистрации по гугл-аккаунту getSignInClient()
 *  8.3 в gradle->dependencies: implementation 'com.google.android.gms:play-services-auth:20.2.0' - добавили библиотеку. Нам нужно получить гугл-аккаунт из нашего смартфона.
 *  С приложения нужно получить доступ к гугл-аккаунту. Но он хорошо защищен.
 *  8.4 Создали опции gso
 *  8.5 Возвращаем опции GoogleSignInClient
 *  8.6 Присвоим возвращенного клиента переменной
 *  8.7 Создаем функцию signInWithGoogle() в классе AccauntHelper для запуска с кнопки "войти под гугл-аккаунтом". Cоздали разметку в sign_dialog.xml
 *  8.8 Создали класс GoogleAccConst в который положили константу GOOGLE_SIGN_IN_REQUEST_CODE = 132
 *  8.9 -> AccauntHelper
 *  Мы получили в приложение гугл-аккаунт пользователя. Теперь по этому аккаунту нужно отправлять запрос на Firebase
 *  8.10 получаем из Firebase гугл-аккаунт пользователя
 *  8.11 В DialogHelper обрабатываем нажатие на кнопку "Зарегистироваться с помощью гугл"
 *  8.12 Создаем функцию signInFirebaseWithGoogle  в AccauntHelper, которая будет брать из intent, который мы получили из функции onActivityResult(). Берем аккаунт, у него токен, и с помощью этого токена регистрироваться на Firebase
 *  Пока что только мы запускали выбор аккаунта и его получение (до этого пункта)
 *  8.13 Приложение не запустится, так как Firebase требуется отпечаток электронный SHA-1 для debug версии Асtive Build Variant. Чтобы его достать, нужно в Gradle в правом верхнем углу экрана AS->выбрать иконку слона->Run Anything->
 *  выбрать или набрать "gradle app:signingReport" и скопировать SHA-1 отпечаток в Firebase->save с дальнейшей заменой google-services.json файла. и далее чтобы запускать приложение, нужно выбрать в Edit configuration-> app
 *  Для того, чтобы корректно работала регистрация через гугл-аккаунт на виртуальном устройстве, неообходимо скачать и запускать приложение на эмуляторе, подключенном к playmarket и сервисам гугл
 */

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

//    private var rootElement:ActivityMainBinding? = null //4.3.1переменная создана на уровне класса, чтобы к ней можно было добратся из любого места внутри класса
    private lateinit var tvAccaunt: TextView   //6.2
    private lateinit var rootElement:ActivityMainBinding
    private val dialogHelper = DialogHelper(this)   //5.3 Инициализируем DialogHelper. Передаем в конструкторе этот класс - MainActivity
    val myAuth = FirebaseAuth.getInstance() //5.13 Инициализируем объект myAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        rootElement = ActivityMainBinding.inflate(layoutInflater)   //4.3.2 Инициализируем переменную. Надуваем разметку с помощью layoutInflater. Теперь эта переменная может в себе хранить разметку экрана. Разметка рисуется графическим процессором
        //не будет нулевых значений, так как у нас есть реалные ссылки на объекты, которые уже нарисованы
        val view = rootElement.root //4.3.3 Передаем переменную на экран. Root элемент - это элемент, который содержит в себе все view
        setContentView(view)    //4.3.4 Рисуем экран
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {   //8.10
        if(requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE){
//            Log.d("MyLog", "Sign in result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)  //Как только мы выбрали один из аккаунтов, то запускается функция onActivityResult() и нам передает intent - это сообщение к системе. Т.о. наше activity приложения обменивается данными с системой андроид
            try {   //мы не просто берем аккаунт, а пытаемся его взять, поскольку может быть множество ошибок

                val account = task.getResult(ApiException::class.java)  //Я слежу за ошибками, которые могут произойти во время регистрации либо входа
                //У нас уже есть аккаунт, но прежде чем достать токен, нужно проверить что наш аккаунт не равен null:
                if(account != null) {
                    Log.d("MyLog", "Api 0")
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }

            } catch (e:ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun init(){
//        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)  //2.6.3  Создали кнопку toggle
        val toggle = ActionBarDrawerToggle(this, rootElement.drawerLayout, rootElement.mainContent.toolbar, R.string.open, R.string.close)  //4.4
//        drawerLayout.addDrawerListener(toggle)  //2.6.4
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        navView.setNavigationItemSelectedListener(this) //2.7.1
        rootElement.navView.setNavigationItemSelectedListener(this)
        tvAccaunt = rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)  //6.2.1
    }

    override fun onStart() {    //6.5
        super.onStart()
        uiUpdate(myAuth.currentUser)    //Если мы не зарегистрировались, currentUser будет null ("Войдите или зарегистрируйтесь"), если не null - текущий адресс email
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {    //2.7.2 Когда мы нажали на элемент из главного меню, мы можем проверить id этого элемента
        when(item.itemId){
            R.id.id_my_ads -> {

                Toast.makeText(this, "Pressed id_my_ads", Toast.LENGTH_SHORT).show()

            }
            R.id.id_car -> {

                Toast.makeText(this, "Pressed id_car", Toast.LENGTH_SHORT).show()

            }
            R.id.id_pc -> {

                Toast.makeText(this, "Pressed id_pc", Toast.LENGTH_SHORT).show()

            }
            R.id.id_smart -> {

                Toast.makeText(this, "Pressed id_smart", Toast.LENGTH_SHORT).show()

            }
            R.id.id_appliance -> {

                Toast.makeText(this, "Pressed id_appliance", Toast.LENGTH_SHORT).show()

            }
            R.id.id_sign_up -> {

//                Toast.makeText(this, "Pressed id_sign_up", Toast.LENGTH_LONG).show()
                dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)    //5.4 //5.9 Для регистрации передаем константу регистрации

            }
            R.id.id_sign_in -> {

//                Toast.makeText(this, "Pressed id_sign_in", Toast.LENGTH_LONG).show()
                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)   //5.4  //5.10 Вход и константа берется для входа

            }
            R.id.id_sign_out -> {

//                Toast.makeText(this, "Pressed id_sign_out", Toast.LENGTH_LONG).show()
                uiUpdate(null)  //6.4
                myAuth.signOut()    //для выхода своей функции писать не нужно, мы воспользуемся функцией класса Auth

            }
        }
//        drawerLayout.closeDrawer(GravityCompat.START)   //При нажатии на одну из кнопок выполнится одно из условий и после этого запустится closeDrawer, и меню закроется
        rootElement.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {  //6.3 в user содержится информация, под каким email мы зарегистрировались и оттуда мы и будем доставать email. Будем доставать email под которым зарегистировались, и передавть user-a
        tvAccaunt.text = if(user == null){
            resources.getString(R.string.not_reg)
        }   else {
            user.email
        }
    }

}