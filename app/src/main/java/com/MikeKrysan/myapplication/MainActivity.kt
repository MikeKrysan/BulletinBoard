package com.MikeKrysan.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.MikeKrysan.myapplication.act.EditAddsAct
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
 *  8.14 добавим информацию, чтобы при регистрации с помощью гугл-аккаунта на хедере в боковом меню отображалась почта аккаунта
 *
 *  Урок9. Авторизация по Google аккаунту, часть 2 (проверка на неверыный email; неверный пароль; вход по гугл-аккаунту, когда у пользователя уже есть email адресс; неверный формат email-адресатиь)
 *  Ошибки с email и гугл-аккаунтом можно ловить в трех метстах в AccauntHelper: вход в аккаунт по почте, регистрация по почте и вход по гугл-аккаунту
 *  9.1 Отлавливаем ошибки при регистрации при помощи емейла signUpWithEmail() AccauntHelper класс. (9.1.1-9.1.5)
 *  9.2 Создаем пакет constants и в нем класс Object FirebaseAuthConstants
 *  9.3 Если сперва зарегистрироваться через почту, а далее войти через гугл аккаунт, то в Firebase данные перезапишутся, и позже я не смогу войти через email
 *  9.4 Ошибка ввода короткого пароля
 *
 *  Урок10. Соединение двух аккаунтов (Google Account & Password)
 *  10.1 Создадим функцию  linkEmailWithGoogle() в классе AccauntHelper
 *  10.2 Запускаем функцию linkEmailWithGoogle() в том месте, где была обнаружена ошибка
 *
 *  Урок11. Добавляем кнопку для создания нового объявления, добавляем выход из Google аккаунта, и начинаем создавать EditActivity
 *  А также создаем возможность для пользователя выйти из своего гугл-аккаунта, потом войти в приложение из другого своего гугл-аккаунта (даем возможность пользователю выбирать из какого гугл-аккаунта ему зайти)
 *  11.1 Через AccauntHelper делаем так, чтобы всегда при входе через гугл-аккаунт приложение запрашивало выбрать из какого из имеющихся совершить вход
 *  11.2 Запускаем signOutGoogle() на слушателе при нажатии на кнопке из меню "id_sign_out" (выход)
 *  11.3 При нажатии и при входе через гугл-аккаунт не закрывается диалоговое окно. Для того, чтобы оно закрывалось, нужго вызвать dismiss() в DialogHelper->
 *  11.4 Добавим активити для редактирования нашего объявления. Открывать его будем через кнопку в правом верхнем углу тулбара. Menu-> New -> Menu Resource File -> main_menu-> ok (создали main_menu в папке menu)
 *  11.5 Drawable->New->Vector asset->создали кнопку и назвали ее new_adds
 *  11.6 Добавили кнопку "New" в тег item mein_menu.xml
 *  11.7 Добавляем библиотеку xmlns:app="http://schemas.android.com/apk/res-auto" в тег menu main_menu.xml, чтобы можно было воспользоваться тегом showAsAction
 *  11.8 Когда мы исплользуем toolbar по-умолчанию(встроенный), в таком случае кнопку "New" на этот тулбар добавляем следующим образом: но в нашем случае когда создан свой тулбар, этого не достаточно, нужно еще выполнить п.11.9
 *  11.9 Но поскольку наш тулбар мы создали сами, нам нужно сказать, что встроенный action_bar в activity это будет мой toolbar. MainActivity->init()
 *  11.10 В com.MikeKrysan.myapplication->New->Package->act->enter
 *  11.11 В act->New->Activity->EmptyActivity->переименовываем на EditAddsAct->Finish
 *  11.12.Чтобы получить доступ к элементам экрана, необходимо создать root-элемент в EditAddsAct
 *  11.13 В MainActivity нам нужно прослушать нажатие на элемент из меню (кнопка "New"), для этого нам нужно добавить onOptionsItemSelected()
 *  11.14 Обработаем еще одну ошибку: когда пользователь не зарегистрирован по email, но пытается войти, зарегистрировавшись при этом по гугл-аккаунту или нет. AccauntHelper->signInWithEmail()
 *
 *  Урок12. Начинаем создавать EditActivity для редактирования объявления. Добавляем список стран
 *  12.1 Cоздаем разметку в  activity_edit_ads.xml
 *  12.2 Для выбора из списка стран воспользуемся спиннером. Для того, чтобы он заработал, необходим файл со списком стран. Создадим папку assets и поместим в нее файл со списком стран: app->New->Folder->Assets folder->finish
 *  Cоздалась папка assets (ресурсы)
 *  12.3 Создадим в сom.MikeKrysan.myapplication папку utils, в которой будем создавать классы помощники. В данном случае нам нужно создать класс, который будет получать список сначала стран, потом
 *  городов из файла countriesToCities.json и выводить его в спиннере в activity_edit_adds.xml
 *  utils->New->Kotlin class file->Object->CityHelper(назвали)
 *  12.4 В EditAddsAct создаем адаптер и подключаем к нашему спиннеру
 *  12.5 Прикрепляем адаптер к спиннеру
 *
 *  Урок13. Создание диалога с SearchView для выбора и поиска страны. в activity_edit_ads.xml  сделаем вместо поиска страны и спиннера кнопку с поиском и выпадающим спиннером. Поиск будет универсальным. Не только для поиска стран
 *  и городов, но и для любого списка, который мы передадим
 *  13.1 В EditAddsAct убираем спинер, адаптер  - в новой концепции поиска нам это будет не нужно. Берем данные как заполняем города и присваиваем их переменной listCountry
 *  13.2 Создаем новый пакет dialogs, в нем класс  DialogSpinnerHelper
 *  13.3 Создаем spinner_layout.xml с rootElement->LinearLayout
 *  13.4 Создаем объект класса DialogSpinnerHelper в классе EditAddsAct
 *  13.5 Покажем весь список с помощью RecyclerView, для этого создадим еще один класс в пакете dialogs->RcViewDialogSpinner
 *  13.6 Создаем textView для отображения страны или города: layout Recource File->sp_list_item
 *  13.7 Передаем textView с id tvSpItem из файла sp_list_item.xml в функцию onCreateViewHolder класса RcViewDialogSpinner как отдельный item в нашем списке
 *  13.8 В DialogSpinnerHelper прежде чем нарисовать rootView, нужно найти все его элементы: SearchView и RecyclerView
 *  13.9 Создаем фильтр по которому мы будем фильтровать страны или города. В CityHelper
 *  13.10 В DialogSpinnerHelper создадим фунцию setSearchView(), чтобы не захламлять класс. В методе onQueryTextChange() при создании временного массива не может принимать null,
 *   поэтому делаем проверку на null в CityHelper:
 *  13.11 Проверка SearchText на null функции filterListData класса CityHelper
 *
 *  Урок14. Делаем возможность выбора страны из списка и отображение данного выбора в TextView
 *  14.1 Создаем функцию init() в EditAdsAct -  для инициализации и чтобы очистить код. Из функции onCreate переносим данные. Запускаем эту функцию в onCreate()
 *  14.2 Создаем диалог на уровне класса в EditAdsAct
 *  14.3 В activity_edit_ads.xml создаем нужную разметку, удалили два элемента, добавили textView
 *  14.4 В файле string добавили строку "Выберите город"
 *  14.5 При нажатии на textView "Выберите страну" нужно чтобы появился диалог выбора стран и когда в диалоге что-то выбрали, показать его в textView. Для этого нам нужно создать слушателя нажатий
 *  Идем в EditAddsAct и создаем функцию onClickSelectCountry. В activity_edit_adds.xml прикрепляем слушателя кнопки. Далее запускаем диалог. Проверяем. В диалоге будет записано "выберите страну" и
 *  список стран
 *  14.6 Для того, чтобы выбранная страна появилась в textView нам нужно добавить слушателя нажатий для RcViewSpinner и в этом слушателе нажатий брать тот элемент на который нажали и передавать в textView из
 *  activity_edit_adds.xml . Поэтому нужен доступ к textView в recyclerView класса RcViewDialogSpinner. Можно сделать интерфейс и при нажатии на кнопку, интерфейс запускается в EditAddsAct и там уже
 *  что-то делаем с textView, либо передать в onCreateViewHolder класса RcViewDialogSpinner наш editActivity и через editActivity получить доступ к textView чтобы его менять
 *  14.7 Нужно передать на RcViewDialogSpinner наш context, а именно наш активити - EditAddsAct. Context у нас есть не в конструкторе, а в функции showSpinnerDialog() класса
 *  DialogSpinnerHelper. Поэтому когда мы запускаем showSpinnerDialog мы запускаем context, а это и есть наше EditAddsAct
 *  и поэтому отсюда мы можем получить доступ к textView из activity_edit_adds.xml. Этот textView мы можем передать в
 *  RcViewDialogSpinnerAdapter(). Т.о. сontext в RcViewDialogSpinnerAdapter теперь и есть EditAddsAct
 *  14.8 Добавляем интерфейс OnClickListener во внутренний класс SpViewHolder класса RcViewDialogSpinnerAdapter. Чтобы context стал доступен в функциях класса RcViewDialogSpinnerAdapter можно двумя способами:
 *  а) создать переменную в которую передать context и обращаться к этой переменной,
 *  б) в конструкторе класса RcViewDialogSpinnerAdapter указать переменную RcViewDialogSpinnerAdapter(var context: Context)
 *  14.9 У нас все еще нет доступа к textView в RcViewDialogSpinnerAdapter, так как rootElement класса EditAddsAct, через который я могу получить доступ к textView и к любому другому элементу на экране- он private
 *      Можно его сделать public, и тогда будет доступ к rootElement у RcViewDialogSpinner
 *  14.10 в функции onClick вложенного класса SpViewHolder класса RcViewDialogSpinnerAdapter делаем даункаст: в классе context мы перевели из класса EditAddsAct в Context, так как нам это было нужно использовать, но в данной функции нужно взять EditAddsAct, чтобы использовать rootElement.
 *      Если не сделать даункаст, то через context не получится обратится к rootElement класса EditAddsAct
 *  14.11 в activity_edit_adds.xml textView меняем id с textView на tvCountry и обращаемся к нему в функции onClick()
 *  14.12 Берем текст из itemView функции setData класса SpViewHolder для того, чтобы присвоить его (context as EditAddsAct).rootElementForEditAddsAct.tvCountry.text = .
 *  Но мы его не сможем взять напрямую, так как itemView лежит в функции; для того, чтобы можно было к нему обратится, есть множество вариантов, создадим переменную которая принадлежит классу SpViewHolder private var itemText = ""
 *  14.13 в itemText (String) когда запускается setData(), мы записываем text: itemText= text, который будет теперь доступен во всем классе SpViewHolder
 *  14.14 У каждого viewHolder (ячейка со страной) будет свой текст, записанный в переменную itemText. Поэтому, когда я жму на кнопку (у каждого viewHolder своя кнопка) я его беру и передаю в наш tvCountry.
 *  14.15 Выбрали слушателя нажатий для элемента itemView (setData()->SpViewHolder->RcViewDialogSpinner) : itemView.setOnClickListener(this). Если этого не указать, не будет работать, так как слушатель нажатий принадлежит всему классу SpViewHolder, но при нажатии на какой элемент он будет запускатся - не укзали
 *  А теперь указали - item элемент, это один из элемнтов выпадающего спиннера, т.е любая страна из списка
 *  Запускаем приложение и смотрим что получилось
 *  14.16 Чтобы закрывался диалог при нажатии на кнопку и выборе страны необходимо: в DialogSpinnerHelper помимо context из RcViewDialogSpinnerAdapter передать и диалог.
 *  Создадим диалог в DialogSpinnerHelper: val dialog = builder.create(). Так как у builder нет функции dismiss() чтобы закрыть диалог, то мы сразу с помощью builder создаем диалог и уже в этот диалог и передаем setView(rootView)
 *  14.17 Передаем dialog в recyclerView(RcViewDialogSpinnerHelper), предварительно приняв его в классе RcViewDialogSpinnerHelper : var dialog:AlertDialog (class RcViewDialogSpinnerAdapter(var context: Context, var dialog:AlertDialog) )
 *
 *  Урок15. Делаем возможность выбора города из списка и отображение данного выбора в textView
 *  Будем показывать список городов в зависимости от того, какая страна выбрана. Если страна не выбрана, будем показывать сообщение: "Сначала нужно выбрать страну", либо убрать возможность выбора города
 *  15.1 В классе CityHepler создаем функцию getAllCities, которая подобная getAllCountries, но дополнительно принимает название страны (country: String)
 *  15.2 из jsonObject мы будем брать теперь не названия, а сами массивы
 *  15.3 В EditAddsAct создадим кнопку, при нажатии на которую мы будем вызывать список с городами и назовем ее onClickSelectCity
 *  15.4 Проверяем, показывается ли тост: "Выберите страну", когда выбран город, но не выбрана страна. (в даннымй момент не показывает!)
 *  15.5 На данном этапе, когда выбираешь город, то он записывается в строку страны, и когда повторно нажать на строку город, вылитает ошибка. Это происходит потому, что в DialogSpinnerHelper мы передаем контекст, а в
 *  RcViewDialogSpinnerAdapter в функции onClick() мы выбираем rootElement.tvCountry.text = itemText. В этом месте следовало сделать по другому. Но этот вариант не подходит, так как выбирает всегда в одном и том же textView - tvCountry,/
 *  а нам нужно выбирать как в tvCountry, так и в tvCity.
 *  Есть несколько вариантов решения данной проблемы:
 *  а) передаем сразу textView и выбираем в нем;
 *  б) передаем какой-нибудь булеан (например: boolean == true - это страны, false - это города) и тогда сделать проверку, если это страны, показывать в одном textView, если города - в другом.
 *  Воспользуемся первым вариантом
 *  15.5.1 В EditAddsAct в функции onClickSelectCountry дополнительно передаем textView посредством rootElement.
 *  15.5.2 Когда жмем на "Выберите город" передаем rootElement tvCity
 *  15.5.3 теперь в DialogSpinnerHelper будет также ожидать и TextView, который я назову tvSelection
 *  15.5.4 и это нужно будет передать в RcViewDialogSpinnerAdapter
 *  15.5.5 Теперь идем в RcViewDialogSpinnerAdapter и контекст  заменяем на tvSelection
 *  15.5.6 В DialogSpinnerHelper-> showSpinnerDialog() убираем  context и подставляем в нужном порядке входящие значения
 *  15.6 Когда выбрали город, а потом поменяли страну, то прошлый город из другой страны остался. Такого нельзя допускать. Если заново выбрать страну, нужно сделать,чтобы поле с городами очищалось и возвращалось значение по-умолчанию
 *  Делаем проверку: если у нас значение по-умолчанию (город не выбран), то страну выбираем любую и все, ничего не делаем. Если уже выбран какой-нибудь город, и6снова выбираем страну, то нужно все очистить в строке городов и вернуть значение по-умолчанию
 *
 * Урок16.`  Добавляем библиотеку Pix для выбора фото для наших объявлений из памяти
 * Есть два способа добавления старой библиотеки Pix
 * 1) implementation("com.fxn769:pix:1.5.6") и добавить jcenter() в Gradle Scripts->settings.gradle-> в dependencyResolutionManagement { } (для новых версий)
 * 2) файл для скачивания https://www.youtube.com/redirect?event=video_description&redir_token=QUFFLUhqbjBFVG9SUGhUM3AzMjVjdjFhTFFFd2RPOVVIUXxBQ3Jtc0tuemtFeE1qc2I5RXo1bDdBUGQwV1hRbUxjUFNxdkdEYW51LU5QaU03RXNrZDJoZFNUQi12R3RGS1ZsT1N6YWpXZlhYQnlSLURtX0JTNWc4LS1XV1hlS1ZSZE9BdVNZMFFCSnNna0JtbFI0YnpVZ1JTaw&q=https%3A%2F%2Fneco-desarrollo.es%2Fwp-content%2Fuploads%2F2022%2F02%2Fpix-1.5.6.rar&v=EyUxVGUwO3E
 * Показ картинок во viewPager можно сделать с помощью отдельного активити или с помощью фрагмента и во фрагменте показывать список, гдя появятся выбранные картинки
 * 16.1 Cоздадим класс-объект ImagePicker. В этот класс мы будем получать картинки, чтобы далее их показать в списке и произвоидить над ними манипуляции. Также этот класс для разгрузки EditAddsAct
 * 16.2 Создали функцию getImages() в ImagePicker
 * 16.3 Чтобы функция getImages() могла работать, сначала нужно запросить доступ к камере и к памяти смартфона. Для этого в EditAddsAct добавляем функцию onRequestPermissionResult()
 * 16.3.1 Создали переменную булеан isImagesPermissionGranted, по-умолчанию false
 * 16.4 В activity_edit_adds.xml добавим временную кнопку imageButton для добавления картинок во viewPager. Кнопка должна быть впереди viewPager, но по-умолчанию она сзади и не видна. Чтоба кнопка была впереди, ее нужно вынести наверх в структуре дерева компонентов (Component Tree)
 * 16.5 Создаем функцию слушателя нажатия кнопки onClickGetImages в EditAddsAct
 * 16.6 Присвоим кнопке слушателя onClick->onClickGetImages в activity_edit_adds.xml
 * 16.7 Производим проверку. Студия не запускает приложение, проблемы с доступом к камере.
 * 16.8 Теперь мы можем получить результат. Пока что получим его в EditAddsAct. Далее сделаем отдельное активити для выбора картинок, или фрагмент. Для того, чтобы получать результат, используем
 * onActivityResult
 * 16.9 Проверяем, что за ссылки (результат) мы получаем
 *
 * Урок 17 - добавляем Fragment для показа списка с выбранными картинками. При выборе нескольких картинок даем пользователю возможность их перемешать
 * Логика следующая: два варианта:
 * 1) Пользователь выбрал одну картинку, новый экран открывать не нужно, чтобы перемешать картинки. Выбранную картинку сразу показываем во viewPager
 * 2) Пользователь выбрал больше одной. Новый фрагмент на котором будет recyclerview, который будет заполнятся с помощью адаптера. И здесь будет кнопка, чтобы можно было перемешать картинки
 * Для редактирования логика измениться. Чтобы изменить картинки, я буду заходить на этот экран. Если у меня одна картинка, то я сразу могу ее выбрать. Если у меня несколько картинок, то сначала
 *  показываю картинки что уже есть, возле каждой картинки кнопка "удалить" и одна большая кнопка "выбрать все"
 *17.1 В activity_edit_adds.xml заменяем ConstraintLayout на FrameLayout и присваиваем ему идентификатор place_holder. Для ScrollView id:scrollViewMain
 * 17.2 Создаем пакет, где будем хранить все наши фрагменты (frag). В данном пакете создаем класс ImageListFrag. Класс должен наследоваться от фрагмента
 * 17.3 Создаем новый layout: list_image_frag.xml
 * 17.4 Запускаем приложение. Если мы нажимаем на кнопку, то запустится наш фрагмент и заменит экран на наш. Но так как есть элементы view, то ничего не произойдет. Необходимо спрятать
 *  view, чтобы появился фрагмент, так как они используют один и тот же контейнер - placeholder.
 *  Поэтому, когда мы запускаем onClickGetImages() в EditAddsAct, мы прячем фрагмент.
 *  Запускаем, проверяем, теперь ушли view вместе с фрагментом
 * 17.5 Добавляем во фрагмент кнопку, чтобы можно было из него выйти, когда он запущен. list_image_frag.xml и даем ей id: bBack
 * 17.6 В ImageListFrag достаем нашу кнопку в функции onViewCreated
 * 17.7 Когда мы выйдем из фрагмента, то мы не увидем view. Нужно их показать. Но как нам отслеживать, что фрагмент закрылся? Мы можем это сделать при помощи интерфейса.
 *  Для этого создаем интерфейс в frag->FragmentCloseInterface; EditAddsAct наследуется также от FragmentCloseInterface
 *  17.8 Когда фрагмент закрыт, запускается функция onDetach() - то-есть фрагмент отсоединяется от activity. Это значит, что фрагмент не работает больше с activity
 *  Это делалось для проверки
 *  17.9 Для того, чтобы ограничить число выбора картинок в ImagePicker в функции getImages передаем переменную ImageCounter:Int. Теперь в EditAddsAct->onRequestPermissionResult() мы можем регулировать число картинок
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

    //11.13:
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.id_new_adds) {   //Здесь нам приходит item на который нажали, и проверяем его идентификатор. Если у нас есть совпадение, значит я нажал на эту кнопку "New" и запустится новое активити
            val i = Intent(this, EditAddsAct::class.java)  //запускаем Активити. Передаем контекст, на котором находимся, и передаем активити, на которым мы хотим перейти
            startActivity(i)    //теперь запускаем intent(намерение) и нужное активити
        }
            return super.onOptionsItemSelected(item)
    }

    //11.8:
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
         setSupportActionBar(rootElement.mainContent.toolbar)   //11.9  Сначала указываем, какой тулбар используется в активити, а после уже запускаем нажатие на кнопку меню
        // в левом верхнем углу. Если эту строку поставить внизу, то мы сперва добавим нажатие на кнопку, но мы еще не будем знать, какой тулбар используется и тогда
        // считываться не будет нажатие на кнопку
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
                dialogHelper.accHelper.signOutGoogle()  //11.2

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