package com.MikeKrysan.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.accaunthelper.AccauntHelper
import com.MikeKrysan.myapplication.act.DescriptionActivity
import com.MikeKrysan.myapplication.act.EditAdsAct
import com.MikeKrysan.myapplication.act.FilterActivity
import com.MikeKrysan.myapplication.adapters.AdsRcAdapter
import com.MikeKrysan.myapplication.databinding.ActivityMainBinding
import com.MikeKrysan.myapplication.dialogHelper.DialogConst
import com.MikeKrysan.myapplication.dialogHelper.DialogHelper
import com.MikeKrysan.myapplication.model.Ad
import com.MikeKrysan.myapplication.utils.FilterManager
import com.MikeKrysan.myapplication.viewModel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

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
 *
 *  Урок18 - делаем Recycler View adapter для показа выбранных фото
 *  Если пользователь выбирает более одной картинки: нажимает на кнопку, открывается фрагмент (onClickGetImages в EditAddsAct). Теперь необходимо сделать, чтобы при нажатии на кнопку запускалась
 *  команда ImagePicker.getImages функции onRequestPermissionsResult(). Как только пользователь выбрал картинки, запускается onActivityResult() и здесь мы проверяем, есть ли у нас в массиве
 *  returnValues размер больше одного, значит что все эти картинки, этот список мы отправляем во фрагмент ImageListFrag где мы подключим recyclerView и будем показывать все эти картинки.
 *  Если у returnValues размер один или пользователь ничего не выбрал, тогда остаемся на editActivity
 *  18.1 В папке frag создаем адаптер: SelectImageRvAdapter
 *  18.2 Создаем select_image_frag_item.xml
 *  18.3 Во внутреннем классе ImageHolder класса SelectImageRvAdapter создадим функцию setData и сюда мы будем передавать ссылку и титл. Значит нужно создать объект, который будет содержать в себе два этих элемента
 *  18.4 Создадим дата класс frag->SelectImageItem
 *  18.5 Инициализируем переменные в setData, далее связываем переменные с Data классом SelectImageItem. Переменные пока не заполняются, потому что мы ими не пользуемся
 *  18.6 Когда будет заполнятся onBindViewHolder, он будет брать holder.setData() и сюда передавать наш item из списка. Но никакого списка пока что нету, так что нужно будет добавить список в класс SelectImageRvAdapter
 *  18.7 Создадим функцию для обновления fun updateAdapter().
 *  18.8 После создания функции updateAdapter() мы готовы принять все и показать в recyclerView. recyclerView нужно подключить адаптер, для этого идем в ImageListFrag и добавляем тип передаваемых данных
 *  (для того, чтобы узать тип передаваемых данных, в EditAddsAct -> onActivityResult выделяем returnValues и нажимаем Shift+Ctrl+P): newList:ArrayList
 *  18.9 Теперь мы получили список, заполним его, для этого находим recyclerView и присваиваем его переменной rcView.
 *  18.9.1 Адаптер можно создать на уровне класса или внутри функции. Лучше создадим на уровне класса
 *  18.9.2 Адаптер создан, теперь к recyclerView нужно присвоить layoutManager, где передадим контекст - activity
 *  18.9.3 Теперь в rcView добавляем адаптер
 *  18.9.4 Обновляем адаптер. Единственная проблема: сразу список со ссылками на фото мы не можем передать, потому как нам нужно передать другой тип данных (массив не со String а с SelectedListItem - два элемнта внутри). Поэтому с помощью цикла for
 *  заполняем два элемента String массива
 *  18.9.5 Обновляем адаптер
 *  18.10 Временный вариант для проверки при запуске приложения с помощью эмулятора (EditAddsAct-> onActivityResult() )
 *  18.11 Чтобы запустилась проверка, поставим код временно из функции onRequestPermissionsResult() в функцию onClickGetImages()
 *  18.12 В AndroidManifest прописываем строку android:requestLegacyExternalStorage="true" - позволяем получить доступ и считать информацию (в нашем случае картинки с телефона для библиотеки pix)
 *
 *  Урок19. добавляем Drag&Drop функцию, возможность перемешивать выбранные фото, для чего будем использовать ItemTouchHelper
 *  Сделаем возможность передвигать картинку при нажатии в любом месте imageview, но чтобы пользователь интуитивно понимал, что карткинки можно перетаскивать, добавим видимую кнопку (опционал при этом остается тем же)
 *  19.1 Создаем кнопку в select_image_frag_item.xml, предварительно создав ее: drawable->new->vector_asset и дадим ей название imDrag
 *  19.2 Идем в ImageListFrag, нужно подключить к recyclerView itemtouchhelper. Делается это следующим образом:
 *  19.2.1 создаем переменную touchHelper и присваиваем ей класс ItemTouchHelper() который будет следить за возможностью для пользователя перетаскивать элементы внутри recyclerView
 *  19.2.2 У ItemTouchHelper() есть функция, которая к rcView подключает функцию touchHelper
 *  19.3 Создам калбек в utils->ItemTouchMoveCallback. Наследовались от ItemTouchHelper, имплементировали все необходимые методы.
 *  19.3.1 В функции getMovementsFlags указали, какие действия пользователя мы хотим отслеживать
 *  19.3.2 Создаем инстанцию класса ItemTouchCallback в классе ImageListFrag
 *  19.4 Cейчас картинки двигаются, но они не могут занять новой позиции, так как в адаптере не прописано, куда деваться другим элементам при передвижении одного из них. В функции onMove() класса
 *      ItemTouchMoveCallback передается элемент target: RecyclerView.ViewHolder который определяет, над каким элементам сейчас находится перетаскиваемый элемент. Элементы же хранятся в адаптере,
 *      и адаптер для картинок называется SelectImageRvAdapter в котором есть mainArray - массив, который хранит в себе все item (титул и ссылку), поэтому здесь и нужно поменять все и
 *      сказть адаптеру, что мы изменили элементы местами. Но мы сейчас находимся в ItemTouchMoveCallback. Как классу SelectImageRvAdapter сообщить информацию о том, что  мы перетаскиваем элементы
 *      и еще передать информацию о target и viewHolder? Для этого мы можем создать интерфейс, добавить ему методы, и через интерфейс передавать.
 *      Создаем интерфейс ItemTouchAdapter в классе ItemTouchMoveCallback
 *  19.5 Добавляем созданный только что интерфейс в SelectImageRvAdapter.
 *  Прежде чем перетащить элемент, необходимо сохранить тот элемент, который сейчас находится на данной позиции, иначе он будет стерт, а на его место станет перетащеный мною элемент
 *  19.6 Интерфейс не запускается, так как нет синхронизации. Функция onMove класса ItemTouchMoveCallback запускается сама по себе, и никакая информация об изменениях в viewHolder и target не передается.
 *  Поэтому в данную функцию нужно передать интерфейс адаптера SelectImageRvAdapter и сказать что он будет работать в ItemTouchMoveCallback->onMove(_)
 *  19.7 Создаем видимость взаимодействия с элементом: выбранный элемент будет менять цвет либо прозрачность при перетаскивании.
 *  Для этого перезаписываем функцию onSelectedChanged()  ItemTouchMoveCallback.
 *  Проверяем. Теперь картинка при перемещении остается полупрозрачной, когда мы отпускаем элемент, нужно вернуть его полупрозрачность на 1
 *  19.8 Нам нужна еще одна функция, которая возвращает первоначальное состояние элемента override clearView()
 *  19.9 Осталось проверить, что в адаптере есть изменения. После из этого адаптера мы будем передавать ссылки на editActivity. Идем в ImageListFrag. Когда мы жмем на кнопку BACK (функция onDetach())
 *      мы возвращаемся на EditAddsAct, и здесь необходимо чтобы ссылки на картинки были уже перемешаны. Будем брать из адаптера mainArray (массив где находятся все элементы: титл, ссылка на картинку)
 *      и проверять перемешались они или нет. Проверили, все работает
 *  19.10 Остается проверить, передает ли адаптер ссылку на editActivity и там уже это все заргужать в наш viewPager и показывать
 *      Логика следующая, при выборе картинок должна быть для каждой картинки кнопка: редактировать эту картинку, удалить эту картинку. Также нужно кнопка "Выбрать все"
 *
 *  Урок20. Работаем со ViewPager, создаем адапер для ViewPager.
 *      На данынй момент мы можем делать картинки, возвращаемся на editActivity, но картинки пока во ViewPager не появляются. Это потому что для ViewPager мы не подготовили адаптер
 *      ViewPager работает по такому же принципу как и RecyclerView: появляется список и его заполняет адаптер. Будем использовать recyclerViewAdapter, только будем подключать его к viewPager.
 *      Список будет горизонтальный, передвигатся картинки будут слева направо либо справа налево
 * 20.1 Создаем адаптер внутри viewPager: imageAdapterItem.xml и в него добавляем картинку
 * 20.2 Создали один элемент для нашего списка, Теперь нужно создать адаптер, который будет брать этот элемент, брать ссылку, заполнять в эту картинку эту ссылку, в этой ссылке показывать картинку
 *      внутри и после брать следующий элемент и его заполнять и таким образом у нас будет список из картинок.
 *      Для этого создадим отдельный пакет adapters, ImageAdapter. Заполнили внутренний класс ImageHolder()
 * 20.3 Создаем массив mainArray в который мы будем передавать ссылки на картинки
 * 20.4 Создаем функцию для обновления update()
 * 20.5 После запуска функции update нужно сообщить адаптеру, что данные внутри изменились
 * 20.6 Подключаем viewPager к EditAct. В EditAddsAct инициализируем адаптер
 * 20.7 В функции init() нужно подключить адаптер ко viewPager
 * 20.8 Теперь нужно обновлять адаптер, когда возвращаешся обратно с фрагмента. Когда мы выбрали картинки и нажимаем кнопку BACK запускается метод onDetach() класса ImageListFrag в котором запускается интерфейс
 *      fragCloseInterface. Чтобы этот интерфейс сработал в активити EditAddsAct в функции onFragClose() мы сможем передать список и здесь же, уже на нашем активити сделать update адаптера во viewPager.
 *      Тогда список с ссылками на картинки в функции onFragClose обновят наш адаптер и тогда во viewPager появится обновление картинок.
 *      Но для этого нужно сказать интерфейсу, что мы передаем список ссылок. Но у меня сейчас список из элементов SelectImageItem и там есть два элемента: title и imageUri, а нам нужен только один.
 *      Поэтому нам нужно будет перегрузить этот список в другой массив, где у нас будет только объект String, потому что в ImageAdapter мы ждем массив из объектов String
 * 20.9 В интерфейсе FragmentCloseInterface указываем, что будем передавать list : ArrayList<String>
 * 20.10 Чтобы решить вопрос описанный в 20.8, 20.9 есть два варианта:
 *      1) В функции onDetach() взять все из массива mainArray и перегрузить только ссылки в отдельный массив который будет уже типа String
 *      2) В классе ImageAdapter переделать mainArray и ждать не String а SelectImageItem
 * 20.10.1 ImageAdapter->onBindViewHolder: holder.setData(mainArray[position].imageUri)
 * 20.10.2 FragmentCloseInterface: fun onFragClose(list : ArrayList<SelectImageItem>)
 * 20.10.3 ImageAdapter->update(): fun update(newList : ArrayList<SelectImageItem>)
 * 20.10.4 EditAddsAct-> onFragClose(): override fun onFragClose(list : ArrayList<SelectImageItem>)
 * 20.11 EditAddsAct->onFragClose(): imageAdapter.update(list)
 * Проверяем работу приложения. Когда мы возвращаемся на активити после выбора фото остаетя та позиция, на которой мы ранее остановились. И нет возможности свайпать далее справа налево с последней третьей картинки.
 * Если нужно отредактировать одну картинку во viewPager, сделаем так, чтобы не вызывалось окно выбора новых картинок а переходили сразу на фрагмент, где находятся все картинки, где будет
 * кнопка "выбрать все" и пользователь заново сможет выбрать три картинки, либо по-отдельности, тогда пользователь сможет изменить только одно фото, удалить его или добавить. Т.о. когда
 * пользователь вернется на активити со viewPager там будет измененные фото,  их не прийдется выбирать. Это логика для следующего урока
 * 20.12 Сделаем так, что первое фото будет подписано "Главное фото" и при перемешивании картинок новой картинке присваивалось это название
 * Меняем местами элементы мы в SelectImageRvAdapter-> onMove()
 * 20.13 Запускаем проверяем. Ничего не произошло, мы не указали notifiDataSetChanged(). Если его добавить в onMove(), то можно будет передвигать картинку только на одну позицию, а со второй на нулевую
 *       не сдвинуть, так как эта функция видит дейсвие и сразу меняет позиции. Нас это не устраивает. Нам нужно менять картинки тогда, когда мы отпускаем элемент. Это происходит в ItemTouchMoveCallback->clearView()
 *       Обновлять notifyDataSetChanged будем здесь, как только отпустили картинку. Для этого нужно будет создать интерфейс, изменение нужно делать в адаптере.
 *       Создадим функцию onClear() интерфейса ItemTouchAdapter в классе ItemTouchMoveCallback
 * 20.13.1 В SelectImageRvAdapter->onMove убираем строчку notyfyDataSetChanged, она здесь уже не нужна
 * 20.13.2 Классу SelectImageRvAdapter добавляем еще один интерфейс onClear()
 * 20.13.3 В ItemTouchMoveCallback->fun clearView()->adapter.onClear
 *
 * Урок 21. Добавляем Toolbar на наш фрагмент с выбором картинок. Добавляем кнопку выбора фото и удаления всех выбранных фото
 * 21.1 Добавляем Toolbar. Идем в list_image_frag.xml и в него добавляем toolbar. Удаляем кнопку back
 *  21.1.1 В toolbar добавляем кнопку "назад". В list_image_frag.xml во вкладке code в теге Toolbar вводим "app:navigationIcon="?attr/homeAsUpIndicator"
 *  21.1.2 Создаем меню в правой части toolbar. Одна кнопка для удаления: при нажатии на эту кнопку все что есть в списке очистится (удалятся три картинки). На каждом элементе будет кнопка удалить и редактировать
 *         Идем в папку menu пакета res, создаем menu_choose_image.xml
 *         drawable->new->vector asset - выбираем иконки для двух кнопок меню
 *         Присваиваем кнопка атрибут OrderInCategory. Тот элемент, у кого число будет больше, будет правее
 * 21.2 Созданное меню нужно поместить в toolbar. Идем в ImageListFrag.kt и делаем там небольшие изменения
 *      Закоментировали кнопуку "BACK"
 *      Переводим фрагмент на viewBinding. Когда у нас один элемент, хорошо использовать findViewById, но когда несколько элементов, лучше использовать viewBinding
 *      21.2.1 Удаляем findViewById
 *      21.2.2 В классе ImageListFrag инициализируем viewBinding, который создал класс ListImageFragBinding
 *      21.2.3 Берем созданный rootElement и инициализируем его в функции onViewCreated. Сейчас этот экран view надувается с помощью inflater, но с viewBinding этого делать не нужно, мы инициализируем
 *          наш элемент и передаем inflater.
 *      21.2.4 Так что теперь, когда я хочу получить rootElement, я пишу rootElement.rcViewSelectImage или любой другой элемент
 *
 *  21.3 Снова идем в ImageListFrag и создаем функцию setUpToolbar, которая будет инициализировать и настраивать toolbar
 *      21.3.1 Запускаем функцию setUpToolbar в функции onViewCreated
 *  Запускаем приложение, проверяем. Все работает корректно, но эти кнопки на данном этапе на активны, потому что мы еще не добавили слушателей нажатий
 *  21.4 Добавляем слушателей нажатий в меню toolbar для кнопок. Проверяем
 *      21.4.1 Мы нашли два элемента, теперь можем им присвоить слушателя нажатий. У них слушатели нажатий такие: setOnMenuClickListener
 *      21.4.2 Слушателя кнопке назад мы присваиваем по-другому, так как ее нет в menu_choose_image.xml
 *  Проверяем работу кнопок, все работает
 *  21.5 При нажатии кнопки назад, выходим на активити. То, что раньше делала кнопка Back, сейчас делает эта кнопка
 *  21.6 При удалении элементов из меню перезагружаем массив: переадем вместо массива с тремя элементами пустой массив, и перезагружаем наш адаптер
 *  21.7 Для корректной работы кнопки добавить картинки, нужно высчитывать максимальное количество минус то, что есть на данный момент, потому как для каждой картинки будет кнопка удалить только эту картинку
 *      и будет в массиве не всегда 3 картинки либо 0. Максимальное количество картинок, которое я позволяю загрузить пользователю - 3.
 *      Если пользователь выбрал три картинки, то кнопку добавить картинки в меню нужно прятать
 *      21.7.1 В utils->ImagePicker создаем константу MAX_IMAGE_COUNT число будет равно 3
 *      21.7.2 Возвращаемся в ImageListFrag. Создаем переменную imageCount в функции setUpToolbar
 *  21.8 Фрагмент работает на EditAddsAct. В функции onActivityResult условие if(returnValues?.size!!>1) не должно работать, если мы запустили выбор картинок из фрагмента, потому что когда мы выберем
 *      картинки на фрагменте, в активити EditAddsAct опять запустится onActivityResult() и здесь будет проблема, снова будет произведена проверка, сколько картинок мы выбрали и нам не дасть выбрать
 *      одну картинку и если даже выбрали две картинки, создаст новый фрагмент и его снова запустит. Нам не нужен новый фрагмент, мы еще этот не закрыли.
 *      Если мы запустили выбор картинок из активити, это условие нормально работает.
 *      В функции onActivityResult() мы создаем анонимный класс ImageListFrag. Проблема в том, что когда созастся анонимный класс, он загрузится в память и исчезнет, ссылки на него не будет и мы
 *      не сможем к нему обратиться, мы не сможем проверить он null либо нет, закрыт или нет. Как можно сделать так, чтобы мы постоянно хранили на него ссылку и знали что с ним происходит?
 *      Для этого мы можем создать вместо анонимного класса переменную и записать его туда
 *      21.8.1 Дорабатываем условие функции onActivityResult
 *      21.8.2 Создаем функцию updateAdapter() в ImageListFrag для обновления адаптера, когда анонимный класс не null и мы берем картинки из фрагмента.
 *          Нам нужно не только обновлять картинки, но и добавлять, ведь если запускается эта функция это значит, что пользователь хочет добавить картинки. Но список постоянно очищается:
 *          в классе SelectImageRvAdapter есть функция updateAdapter и самое первое что мы делаем, мы очищаем список. В нашем случае я не хочу очищать адаптер, а добавить элемент, добавить boolean
 *          и постоянно указывать, хотим очищать либо не хотим
 *      21.8.3 В ImageListFrag в функции setUpToolbar добавляем условия true или false
 *  21.9 У нас один элемент из списка состоит из двух элементов: титл и ссылка
 *  21.10 Функцию updateAdapter из класса ImageListFrag запускаем в условии onActivityResult класса EditAddsAct
 *  21.11 Запуситили проверили. Когда выбираешь повторно картинки и возвращаешся на EditAddsAct, в окне не отображаются выбранные картинки(окно для свайпа). Это произошло потому, что мы вышли из
 *      фрагмента, он закрылся и разрушен, его не существует. Но переменная которую мы создали в EditAddsAct chooseImageFrag, чтобы остлеживать анонимный класс так и продолжает хранить в себе
 *      старую ссылку. Следовательно, когда происходит проверка if(returnValues?.size!! > 1 && chooseImageFrag == null) больше никогда не происходит, поэтому если мы по-новому хотим открыть новый фрагмент,
 *      то он не открывается, потому что запускается следующая проверка: else if(chooseImageFrag  != null), а здесь нечего запускать, потому что фрагмента нет.
 *      Поэтому, когда запускается функция onFragClose когда мы закрыли наш фрагмент, то нужно в этот момент переменной chooseImageFrag присвоить опять null, тогда сработает условие if функции
 *      onActivityResult и снова создастся фрагмент
 *      Проверили, все работает
 *
 * Урок 22. Оптимизируем код и делаем проверку если уже есть выбранные фото то передаем их на наш фрагмент для добавления или редактирования фото
 *  Добавим возможность если есть во viewPager уже картинки, то вместо того чтобы открывать экран выбора картинок, нас сразу перебрасывало в  экран списка картинок во фрагменте
 *  22.1 Когда мы обновляем адаптер updateAdapter в ImageListFrag, мы передаем cсылки наших картинок: ArrayList<String>. Но у нас адаптер принимает тип данных ArrayList<SelectImageItem>() там уже идет и титл
 *      ссылка на картинку. Мы можем передавать ссылку на картинки без титлов, а титлы пусть добавляются сразу напрямую в адаптере. Мы сделаем отдельный массив, где запишем три слова: Главное фото, фото номер 2,
 *      фото номер 3.
 *      22.1.1 Избавляемся от цикла for в onViewCreated() класса ImageListFrag, оптимизируем код
 *      22.1.2 Проблема в том, что сейчас адаптер ждет массив с типом данных selectImageItem. В вложенном классе ImageHolder класса SelectImageRvAdapter в функции setData передаем просто String
 *      22.1.3 Cоздадим массив в папке res->values:new-> valueResourceFile->title_image_array.xml
 *  22.2 Массив, который мы создали в title_image_array.xml берем в SelectImageRvAdapter->ImageHolder->setData(): tvTitle.text = .
 *      Когда рисуется viewHolder, он тоже берется по позициям. На какой позиции viewHolder создался, ту позицию мы можем взять и оттуда взять слово из массива.
 *  22.3 Добавляем логику: после того, как выбрали фото, передали их во veiwPager, при нажатии на карандаш проверять, если нет картинок во viewPager, то тогда только мы вызываем экран для выбора фото.
 *      Если уже есть картинки, то при нажатии на карандаш нас перебрасывает на фрагмент и в списке эти картинки появляются, где мы сможем добавить либо удалить все(в верхнем меню)
 *      22.3.1 Идем в EditAddsAct и оптимизируем код: будет открытие фрагмента, но уже не при получении картинок, как было прописано в onActivityResult(). То-есть мы нажали на кнопку onClickGetImages() и
 *          запускаем функцию getImages и указываем, что изначально можно взять три картинки. У нас появляется экран для выбора картинок, мы выбрали картинки, и когда жмем на галочку, то запускается
 *          функция onActivityResult() и проверяет: если там больше одной фотографии, то создает фрагмент и в него передает фрагмент returnValues, - это и есть нашы ссылки. Фрагмент запускает адаптер,
 *          этот адаптер заполняет recyclerView.
 *          Теперь я хочу сделать то-же самое, но уже без выбора картинок. При нажатии на карандаш я хочу открыть фрагмент, только не брать картинки, и не открыть фрагмент при запуске onActivityResult(который запустится только тогда, когда я выбрал картинки),
 *          а запустить фрагмент, если есть выбраты картинки.
 *          Выносим код из функции onActivityResult в отдельную функцию openChooseImageFragment, так как этот отрезок кода будет использоваться минимум два раза.
 *     22.3.2 Откуда мы будем запускать теперь фрагмент? Функция onClickGetImages в EditAddsAct пока что всегда запускает экран выбора картинок, но так не должно быть.
 *          Мы должны выполнить проверку.
 *    Запускаем, проверяем. Нажал на карандаш, и без выбранных фото перебрасывает на фрагмент. Меняем условия проверки из < 0 на ==0. 
 *    22.4 Когда во ViewPager свайпунть элемнты, перейдя к последнему, зайти во фрагмент
 *        и вернутся во ViewPager, последняя позиция остается активной. Сделаем так, чтобы при возврате с фрагмента на viewPager не зависимо от того, на какой позиции осталась выбранной картинка, чтобы показывало
 *        нам всегда первую картинку.
 *
 *   Урок 23. Делаем редактирование картинок. Пользователь сможет изменить любую выбранную картинку и ему не придеться  выбирать снова все картинки если ему нужно изменить толко одну
 *      23.1 Добавим кнопку imEditImage в select_image_frag_item.xml
 *      23.2 В utils->ImagePicker создаю константу для возможности выбора одной картинки
 *          23.2.1 Добавляем в функцию getImages() rCode: fun getImages(context: AppCompatActivity, imageCounter: Int, rCode: Int) который будет отвечать за передачу константы чтобы изменить все картинки или одну
 *          23.2.2 Редактируем код в EditAddsAct и в ImageListFrag->setUpToolbar()
 *     23.3 В SelectImageRvAdapter находим созданную кнопку в xml и добавляем ей слушателя нажатий. Чтобы при нажатии этой кнопки мы запускали выбор картинок и запускали другой requestCode
 *     23.4 Когда я выбрал картинку при помощи кнопки imEditImage нужно указать, куда ее нужно поместить, ведь мы выбираем из адаптера, а это массив ссылок на картинки. Поместить нужно на позицию, откуда берем, и
 *          нам нужно знать позицию в EditAddsAct. Поэтому нужно создать переменную и когда мы нажмем на кнопку, нужно узнавать на какую позицию нажали и передать ее в EditAddsAct. После, как мы получим
 *          картинку, мы отправим картинку в наш адаптер, и уже будем знать позицию, потому что мы знаем позицию, на которую мы нажали.
 *          Создаем переменную editImagePos в EditAddsAct
 *     23.5 После создания переменной возвращаемся в SelectImageRvAdapter и делаем две вещи:
 *          23.5.1 Запускаем ImagePicker чтобы мы могли выбрать картинку (ImageHolder->setData())
 *          23.5.2 Чтобы знать позицию картинки, на которую нажал, возьмем контекст
 *     23.6 В EditAddsAct->onActivityResult прописываем условия получения одной картинки
 *     23.7 Чтобы обновить не весь адаптер для вызова одной картники создаем функцию, которая будет обновлять только одну картинку.
 *          23.7.1 В ImageListFrag создаем функцию setSingleImage()
 *          23.7.2 Запускаем функцию setSingleImage() в EditAddsAct->onActivityResult
 *     У нас пока не просиходит проверки изменения одной картинки. Далее мы пропишем логику, когда изначально пользователь выбрал только одну картинку, потом поместил ее во ViewPager и далее захотел
 *     ее отредактировать. В таком случае он вернется на ListImageFrag, где у нас есть список. Оттуда он сможет и добавить, и перемешать, и по отдельности изменить, там и происходит редактирование картинок
 *
 *     Урок 24. Делаем удаление одиночных картинок и добавляем возможность выбрать одну картинку
 *     Мы сделаем удаление item-ов: добавим кнопку, чтобы удалились выбранные картинки через карандашик. Делаем возможность выбрать одну картинку. На данный момент
 *     когда выбираешь картинку, она не отображается во viewPager. Также делаем возможным добавлять картинки с большим весом и качеством (более 10мб) - для избежания
 *     ошибки outOfMemoryException, так как imageView не подготовлен для этого, создав imageManager, который будет проверять картинку, смотреть какого она размера;
 *     если это большая картинка, значит мы ее уменьшаем до нужного размера, до 1920х1080p. И для базы данных это будет хорошо, так как в базе данных не желательно
 *     хранить большие картинки, потому что за место в бз будем платить
 *     24.1 Добвавляем кнопку для удаления imDelete для каждого элемента и карандаш выберем картинку векторную, чтобы все кнопки были одинакового размера
 *     24.2 В классе SelectImageRvAdapter->сlass ImageHolder()-> setDatd() сперва находим кнопку "imDelete", сперва инициализировав ее в классе
 *     24.3 Присвоим кнопке imDelete слушателя нажатий. Каким образом мы удаляем картинку? Сначала удаляем из массива, где хранятся картинки, а после сообщаем нашему
 *          адаптеру, что мы удалили картинку. Чтобы передать в класс ImageHolder переменную из класса SelectImageHolder, нужно передать в класс ImageHolder наш адаптер,
 *          а уже у адаптера есть доступ к массиву, добавляем в скобки класса ImageHolder параметр:
 *          Запускаем, проверяем, работает. Но когда удалить второе фото, третье стает на его место, и надпись не изменяется, пишет, что это фото3 попрежнему
 *     24.4 Сделаем логику добавления одной картинки, и чтобы она отображалась в пейджере. EditAddsAct->onActivityResult куда приходит ссылка на картинку, которую мы выбрали
 *          мы делаем проверку. Запускаем проверяем, работает. Теперь,когда одна картинка выбрана, мы нажимаем карандаш, и нас перекидывает во фрагмент, где мы можем
 *          добавить картинки, удалить все, удалить эту либо изменить эту
 *     24.5 Чиним проблему неизмененных надписей картинок при удалении других. SelectImageRvAdapter->ImageHolder->setData(). Чтобы сохранить анимацию при удалении фото,
 *
 *   Урок25. Создаем ImageManager
 *     25.1 Создаем класс(Object - все методы без инициализации) для проверки размеров картинок. utils->ImageManager
 *     25.2 Создаем функцию getImageSize , которая будет выдавать размер
 *     25.3 Заблокируем проверку выбора одной картинки в onActivityResult() класса EditAddsAct чтобы когда передаем картинку большего размера, нам AS не выдало ошибку
 *     25.4 Создадим функцию для замера высоты и ширины картинки в зависимости от ориентации смартфона в object ImageManager
 *     25.5 Прежде чем в функции getImageSize() делать проверку фото на ширину и высоту, делаем проверку ориентации, вызвав функцию imageRotation()
 *
 *   Урок26. Создаем функцию для уменьшения картинок, если фото превышает предел, который мы установили, то мы уменьшаем до заданного значения
 *      26.1 Создаем специальную константу в object ImageManager которая будет ограничивать размер загружаемой пользователем картинки
 *      26.2 Cоздадим функцию imageResize, которая позволит сжимать картинку сообразно ее изначальных размеров
 *      26.3 С помощью логов проверяем как сжимаются наши картинки, которые превышают максималное значение, которое мы задали с помощью const val MAX_IMAGE_SIZE = 1000
 *          Мы добились того, что даже если пользователь будет загружать большие картинки, мы решили проблему с ошибкой загрузки больших картинок, и у нас не будет проблемы с базой данных,
 *          У нас будут идти bitmap, которые мы будем уже загружать на Firebase
 *
 *   Урок27. Добавляем библеотеку Picasso, Coroutine и учимся использовать Coroutine чтобы не блокировать основной поток трудоемкими операциями
 *      27.1 Подключаем библиотеку Picasso (build.gradle(Module))
 *      Если не хватает памяти для методов, в build.gradle(Module) прописываем "multiDexEnabled true
 *      27.2 Добавляем корутины implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
 *      27.3 Говорим, что функция imageResize в ImageManager suspend - чтобы закончилась одна операция и перешла к другой, но только не на основном потоке. строка "= withContext(Dispatchers.IO)"
 *          гаворит нам о том, что мы подключили корутины и эта функция будет выполнятся в фоновом процессе, не на основном потоке, и поэтому нам не обязательно использовать thread(). Но возникает
 *          пробема: мы не можем уже теперь использовать эту функцию в другом месте. Ведь imageResize мы вызываем в методе onViewCreated класса ImageListFrag. Чтобы запустить здесь этот метод,
 *          нужно чтобы функция onViewCreated также была coroutine или suspend, и эту проблему легко решить, вызвав корутин в этом методе.
 *      27.4 Вызываем корутин в методе onViewCreated(). Эта корутина будет выполнять какие-то задачи на основном потоке, но сама функция - одна из задача - будет выполнятся на второстепенном потоке
 *      27.5 В классе ImageListFrag создали переменную job класса Job, которая необходима для завершения корутина, запущенного в функции onViewCreated()
 *      27.6 job.cancel() вызываем завершение корутины в методе onDetach().
 *      Корутины - это, грубо говоря, диспетчер задач, который указывает как и где он будет работать; внутри него указываем разные задачи; мы можем эти задачи запускать синхронно, асинхронно
 *      и после получать результат
 *      27.7 После завершения метода imageResize класса ImageManager выдадим сообщение "Done", вызвав в методе onViewCreated() класса ImageListFrag
 *
 *      Урок28. Уменьшаем размер фото до выбранного размера, создаем Bitmap массив и показываем все выбранные фото в RecyclerView и в ViewPager. Теперь у нас не будет ошибки при выборе фото огромного размера.
 *          Когда мы выбрали картинки и они добавились сса во ViewPager, при нажатии на карандаш запустится функция onClickGetImages() класса DditAddsAct. В этот раз уже запустится openChooseImageFrag(null) - мы передадим null, это означает, что
 *          ссылок у нас нет и сами обновим chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray) и передадим bitmap которые находятся в нашем ViewPager. Раньше у нас были ссылки, и не нужно было всего этого делать, а теперь
 *          идут и ссылки и bitmap вперемешку
 *
 *     Урок29. Добавляем ProgressBar для индикации ожидания сжатия картинок. Как только картинки будут сжаты и готовы к показу в RecyclerView прячем ProgressBar.
 *              При выборе пользователем тяжелых картинок проходит некоторое время пока они сожмутся до нужного размера. В это время нам нужно показать пользователю что что-то происходит.
 *          Поэтому мы добавим ProgressBar, который будет показывать статус сжатия и добавления картинки. Еще нужно заблокировать возможность пользователем выхода назад, пока не загрузится
 *          фото, так как если он выйдет нечего будет передавать назад, потому что фото мы не получили. И еще нужно будет добавить ProgressBar для выбора каждого фото в отдельности.
 *          То-есть будет общий ProgressBar, который будет крутится, когда мы загружаем все фотографии сразу, и будет ProgressBar который будет показан, когда мы выбираем отдельное фото.
 *          Дополнение: когда мы выбрали три картинки, мы не прячем кнопку "добавить еще", если нажать на нее, когда уже выбрано максимальное количество картинок, выйдет ошибка. Мы не можем
 *          добавить еще, когда выбрано максимальное количество картинок, поэтому ее нужно спрятать. В данный момент, если нажать кнопку "добавить еще" и выбрать еще картинку, то произойдет ошибка.
 *          Если ее выбрать, она добавится, но если попытаться ее показать, то нас выкинет на пустое активити без ранее выбранных фото
 *              Настроим progress_dialoglayout.xml           
 *              Пропишем логику, чтобы диалог загрузки убиралсся, когда фото будут заргужены
 *              Убираем окно загрузки после того, как картинки были загружены
 *
 *     Урок30. Доделываем выбор одного фото. Делаем внешний вид экрана выбора фото, и переносим код выбора фото из EdtiAddsAct в ImagePicker
 *
 *     Урок31. Прячем кнопку "добавить картинки" если уже выбраны 3 картинки и показываем снова если удалили одну из картинок. Подключаем наш RecyclerView Adapter через ViewBinding и
 *     показываем по разному горизонтальные и вертикальные картинки.  Если картинка вертикальная то показываем полностью картинку выбирая CENTRE_INSIDE scaleType у ImageView если
 *     картинка горизонтальная то выбираем CENTRE_CROPE заполняя картинкой весь ImageView а что не уместилось обрезается.
 *
 *     Урок32. Добавляем Banner рекламу  AdMob
 *
 *     Урок33. Добавляем Interstitial рекламу от AdMob
 *
 *     Урок34. Начинаем делать экран для создания объявления
 *
 *     Урок35. Заканчиваем экран для создания объявлений и создаем data class для хранения текста объявления
 *
 *     Урок36. Настраиваем запись в базу данных
 *
 *     Урок37. Создаем полный путь для сохранения объявления, и оптимизируем версии библиотек Firebase
 *
 *     Урок38. Учимся считывать даныые с базы данных Real Time Database
 *
 *     Урок39. Создаем разметку для шаблона объявления в списке, добавляем RecyclerView
 *
 *     Урок40. Создаем RecyclerView Adapter
 *
 *     Урок41. Учимся добавлять библиотеки вручную как модуль. Удаляем хранилище - Урок 16.2. JCenter отключен!
 *
 *     Урок42. Подключаем адаптер к Recyclerview, создаем интерфейс ReadDataCallBack, настраиваем адаптер и item объявления. Добавляем Title (заголовок)
 *
 *     Урок43. Делаем дизайн шаблона объявления для списка, делаем дизайн Toolbar
 *
 *     Урок44. заменяем метод startActivityForResult на новый способ получения результата из запущенного активити.
 *          Создаем callback для получения нескольких фото. Создаем функцию для запуска активити для выбора фото. Добавляем запуск активити для выбора изображений в EditAdsAct.
 *          Добавляем callback для выбора только одного фото. Тестируем приложение
 *
 *     Урок46. Показываем или прячем панель для редактирования в зависимости от аккаунта. Если это владелец объявления, то показываем панель для редактирования (кнопки "редактировать",
 *          "удалить"). Если это не владелец объявления, то прячем панель
            - Добавляем в класс Ad новую переменную uid.
            - Создаем функцию isOwner() в AdsRcAdapter.
            - Присваиваем id контейнеру в котором находятся кнопки редактирования и удаления.
            - Создаем функцию showEditPanel() в AdsRcAdapter.
            - Создаем новые объявления и тестируем.
 *
 *      Урок46_. Смотрим, как подписывать приложение и как выбрать Build Variant. Мы укажем настройки подписки и создадим release вариант нашего приложения, что позволит нам создавать подписанный
 *          apk-файл и загрузить его на PlayMarket или поделиться с друзьями
 *
 *      Урок46_1. Используем архитектуру MVVM.
 *          - Убираем прямую связь между DbManager и MainActivity
 *          - Создаем Model
 *          - Создаем ViewModel и подключаем к Model (DbManager)
 *          - Подключаем ViewModel к View (MainActivity)
 *
 *      Урок47. Добавляем нижнее меню (BottomNavigationView)
 *          - добавляем Bottom Navigation View и создаем xml файл для меню
 *          - убираем текст из меню
 *          - создаем новые иконки для кнопок меню
 *          - меняем цвет состояния иконок. Создаем drawable selector в xml
 *          - добавляем слушатель нажатий для созданного меню
 *          - добавляем выбор элемента по возврату на MainActivity
 *
 *      Урок48. Узнаем, как сортировать и фильтровать данные с помощью класса Querry в Firebase Real Time Database
 *
 *      Урок49. Редактирование объявления. На этом уроке пишем логику для передачи объявления при нажатии на кнопку "редактировать" на EditAdsActivity для редактирования
 *
 *      Урок50. Редактирование объявления, часть 2. Пишем логику для редактирования и публикации отредактированного объявления
 *
 *      Урок51. Удаление объявлений, DiffUtils.Callback. На этом уроке пишем логику для удаления обновлений и обновление адаптера через DiffUtil класс
 *
 *      Урок52. Создаем счетчик просмотров объявлений
 *
 *      Урок53. Создаем категорию Избраное Часть 1. Пишем код для добавления и удаления объявления в Избранное
 *
 *      Урок54. Создаем категорию Избранное  Часть 2. На этом уроки делаем счетчик избранных.
 *
 *      Урок55. Фильтруем избранные объявления и добавляем TextView "Пусто" для индикации отсутствия объявлений
 *
 *      Урок56. Добавляем анонимного пользователя. Часть 1
 *          - функция signInAnonymously()
 *          - активируем анонимный вход на Firebase Authentication
 *          - проверка выхода анонимного пользователя
 *          - удаляем анонимный аккаунт если зарегистрировались
 *          - блокируем добав в избранное у анонимного пользователя
 *          - обновляем UI в зависимости под каким аккаунтом вошли
 *
 *      Урок57. делаем удаление анонимного аккаунта если пользователь регестрируется или входит по электронной почте.
 *      устанавливаем "Правила" безопасности на Firebase для того чтобы ограничить доступ не авторизированному пользователю.
 *
 *      Урок58. Вышло новое обновление для библиотеки Pix, по этой причине мы переделываем код под новую версию данной библиотеки которая
 *      теперь поддерживает версию андроид 11  а также можем уже удалить jcenter из проекта так как библиотеку уже перенесли на mavenCentral
 *      Урок59. Новая версия Pix Image Picker, часть 2
 *      Урок60. Новая версия Pix Image Picker, часть 3
 *      Урок61. Новая версия Pix Image Picker, часть 4
 *      
 *      Урок62. Загрузка фото на Firebase Storage
 *          - создание функции prepareImageByteArray()
 *          - cоздание функции uploadImage()
 *          - создание функции uploadImages()
 *          
 *      Урок63. Добавляем возможность загрузки нескольких фото на Firebase Storage
 */

/*
    Урок64. Добавляем код для показа объявлений в списке
    Урок65. Показ фото в списке объявлений

    Урок66. Создаем экран с просмотром объявления
    Урок67. Заполняем экран с объявлением. Начнем с заполнения ViewPager картинками (фото)
            Добавляем слушателя нажатий AdsRcAdapter для возможности просмотра объявления (DescriptionActivity). В функции mainOnClick()

    Урок68. Заполняем экран с объявлениями. Заполняем все TextView экрана из класса Ad

    Урок69. Делаем возможность отправки сообщения владельцу объявления и звонок по телефону. А также добавляем Email-адресс в класс Ad
            При нажатии на кнопку "позвонить", открывается стандартное приложение из смартфона и туда передаем номер телефона, на который мы хотим позвонить

    Урок70. Краткое описание объявления в 4 строчки. Счетчик фото в DescriptionActivity. Счетчик количества просмотров. Заполняем ViewPager на EditActivity

    Урок71. Делаем счетчик фото в EditAdsActivity

    Урок72. Заменяем устаревшую функцию onActivityResult для входа по Google аккаунту - КАК И РАНЕЕ, ВХОД ПО ГУГЛ-АККАУНТУ ОТВАЛИЛСЯ И НЕ РАБОТАЕТ (работает, нужно ключ обновлять если пользуешся VPN)

    Урок73. Заканчиваем NavigationView, добавляем показ аватарки в NavigationView при регистрации. Фото аккаунта в меню, меняем цвет категорий

    Урок74. Начинаем делать Pagination (получение объявлений из БД порциями). Делаем слушателя скролла для RecyclerView
    Урок75. Pagination, часть 2. Делаем загрузку объявлений по порциям
            -Добавляем время публикации в объявление
            -Загрузка объявлений по порциям
            -Подгрузка объявлений через scrollListener
    Урок76. Pagination, часть 3. Правильное обновление адаптера

    Урок77.Фильтрация по категориям, часть 1
        -функция publishAds() с записью фильтра
        -функция getAllAdsFromCat()
        -улучшаем scrollListener()

    Урок78. Заканчиваем код для фильтрации объявлений по категориям и делаем, чтобы самые свежие объявления были вверху
        -Функция getAllAdsFirstPage()
        -Функция getAllAdsNextPage()
        -изменяем функцию scrollListener()
        -создаем функцию getAdsByCategory()
        -функция getAllAdsFromCatFirstPage()

    Урок79. Начинаем делать FilterActivity для фильтрации объявлений по городу, стране и т.д.
        -создаем Activity и подключаем ViewBinding
        -создание кнопки меню для открытия FiltrActivity
        -добавляем ActionBar на FilterActivity
        -активируем кнопку для выхода из активити

   Урок80. Продолжаем делать FilterActivity для фильтрации объявлений по городу, стране и т.д
        -разметка экрана
        -функции для выбора страны и города
        -слушатель нажатий onClickDone
        -функция createFilter()
        -проверка фильтра

    Урок81. FilterActivity. Сегодня делаем передачу фильтра между активити

    Урок82. Делаем запись фильтров в БД для дальнейшей фильтрации объявлений по этим фильтрам

    Урок83. Делаем получение пути фильтра для фильтрации. Функция getFilter

    Урок84. Делаем получение пути фильтра для фильтрации и получаем сам фильтр для передачи на DbManager
        -устраняем ошибку
        -получаем название узла и фильтра
        -проверка получения фильтра
 */


 

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AdsRcAdapter.Listener{

//    private var rootElement:ActivityMainBinding? = null //4.3.1переменная создана на уровне класса, чтобы к ней можно было добратся из любого места внутри класса
    private lateinit var tvAccaunt: TextView   //6.2
    private lateinit var imAccount: ImageView   //
    private lateinit var binding:ActivityMainBinding
    private val dialogHelper = DialogHelper(this)   //5.3 Инициализируем DialogHelper. Передаем в конструкторе этот класс - MainActivity
    val myAuth = Firebase.auth //5.13 Инициализируем объект myAuth
    val adapter = AdsRcAdapter(this)
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private var clearUpdate: Boolean = true //Переменная когда мы хотим очистить список, и вернутся на главный экран
    private var currentCategory: String? = null //Переменная с помощью которой мы будем определять, на какой категории мы сейчас находимся
    private var filter:String = "empty"   //создаем глобальную переменную записанного массива из фильтра FilterActivity
    lateinit var filterLauncher: ActivityResultLauncher<Intent>   //создаем ActivityResultLauncher для того чтобы запускать активити и ждать результата

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)   //4.3.2 Инициализируем переменную. Надуваем разметку с помощью layoutInflater. Теперь эта переменная может в себе хранить разметку экрана. Разметка рисуется графическим процессором
        //не будет нулевых значений, так как у нас есть реалные ссылки на объекты, которые уже нарисованы
        val view = binding.root //4.3.3 Передаем переменную на экран. Root элемент - это элемент, который содержит в себе все view
        setContentView(view)    //4.3.4 Рисуем экран
        init()
        initRecyclerView()
        initViewModel()
        bottomMenuOnClick()
        scrollListener()
        onActivityResultFilter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Слушатель нажатий на меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //если фильтр - запускаем наше меню:
//        if(item.itemId == R.id.id_filter) startActivity(Intent(this@MainActivity, FilterActivity::class.java)) //пока что запускаем так, но потом будем запускать через ланчер. Фильтр выбран, мы хотим получить результат и с помощью этого результата фильтровать
        //запускаем с помощью лаунчера
        if(item.itemId == R.id.id_filter) {
            //создаем Intent
            val i = Intent(this@MainActivity, FilterActivity::class.java).apply{
                putExtra(FilterActivity.FILTER_KEY, filter) //помещаем в Intent фильтр если он там был. Если его не было, то туда помещается слово empty
            }
            filterLauncher.launch(i)    //запускаем и ждем уже результата
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bNavView.selectedItemId = R.id.id_home
    }

    private fun onActivityResult() {
        //Создаем launcher
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            //Создается колбек, который ожидает результата
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null) {
                    Log.d("MyLog", "Api 0")
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            } catch (e:ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
    }

    private fun onActivityResultFilter() {
        filterLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                filter = it.data?.getStringExtra(FilterActivity.FILTER_KEY)!!
                Log.d("MyLog", "Filter : $filter")
                Log.d("MyLog", "getFilter : ${FilterManager.getFilterNode(filter)}")

            }
        }
    }

    override fun onStart() {    //6.5
        super.onStart()
        uiUpdate(myAuth.currentUser)    //Если мы не зарегистрировались, currentUser будет null ("Войдите или зарегистрируйтесь"), если не null - текущий адресс email
    }

    //11.13:
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if(item.itemId == R.id.id_new_adds) {   //Здесь нам приходит item на который нажали, и проверяем его идентификатор. Если у нас есть совпадение, значит я нажал на эту кнопку "New" и запустится новое активити
//            val i = Intent(this, EditAddsAct::class.java)  //запускаем Активити. Передаем контекст, на котором находимся, и передаем активити, на которым мы хотим перейти
//            startActivity(i)    //теперь запускаем intent(намерение) и нужное активити
//        }
//            return super.onOptionsItemSelected(item)
//    }

    //11.8:
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {   //8.10
//        if(requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE){
////            Log.d("MyLog", "Sign in result")
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)  //Как только мы выбрали один из аккаунтов, то запускается функция onActivityResult() и нам передает intent - это сообщение к системе. Т.о. наше activity приложения обменивается данными с системой андроид
//          try {   //мы не просто берем аккаунт, а пытаемся его взять, поскольку может быть множество ошибок
//
//                val account = task.getResult(ApiException::class.java)  //Я слежу за ошибками, которые могут произойти во время регистрации либо входа
//                //У нас уже есть аккаунт, но прежде чем достать токен, нужно проверить что наш аккаунт не равен null:
//                if(account != null) {
//                    Log.d("MyLog", "Api 0")
//                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
//                }
//
//            } catch (e:ApiException) {
//                Log.d("MyLog", "Api error: ${e.message}")
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this, {
            val list = getAdsByCategory(it)
            if(!clearUpdate) {
                adapter.updateAdapter(list) //передаем список обработанный (перевернутый) c помощью функции getAdsByCategory
            } else {
                adapter.updateAdapterWithClear(list)
            }
            binding.mainContent.tvEmpty.visibility = if(adapter.itemCount == 0) View.VISIBLE else View.GONE
        })
    }

    //функция, которая будет делать реверс объявлений по времени в определенной категории (от более новых к более старым)
    private fun getAdsByCategory(list: ArrayList<Ad>): ArrayList<Ad> {
        val tempList = ArrayList<Ad>()  //создаем временный список
        tempList.addAll(list)    //в этот временный список перегружаем список со всеми объявлениями
        //Если текущая категория не равна категории "разные", то тогда нужно отфильтровать объявления с той категории на которой мы находимся
        if(currentCategory != getString(R.string.dif)) {
            tempList.clear()
            list.forEach {
                if(currentCategory == it.category) tempList.add(it)
            }
        }
        tempList.reverse()  //переворачиваем объявления, чтобы самое свежее показывало первым
        return tempList
    }

    private fun init() {
        currentCategory = getString(R.string.dif)   //по-умолчанию, мы находимся в категории "разное"
        setSupportActionBar(binding.mainContent.toolbar)   //11.9  Сначала указываем, какой тулбар используется в активити, а после уже запускаем нажатие на кнопку меню
        onActivityResult()    //Инициализируем наш launcher
        navViewSetings()
        // в левом верхнем углу. Если эту строку поставить внизу, то мы сперва добавим нажатие на кнопку, но мы еще не будем знать, какой тулбар используется и тогда
        // считываться не будет нажатие на кнопку
//        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)  //2.6.3  Создали кнопку toggle
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.mainContent.toolbar, R.string.open, R.string.close)  //4.4
//        drawerLayout.addDrawerListener(toggle)  //2.6.4
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        navView.setNavigationItemSelectedListener(this) //2.7.1
        binding.navView.setNavigationItemSelectedListener(this)
        tvAccaunt = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)  //6.2.1
        imAccount = binding.navView.getHeaderView(0).findViewById(R.id.imAccountImage)
    }

    private fun bottomMenuOnClick() = with(binding) {
        mainContent.bNavView.setOnNavigationItemSelectedListener {  item ->
            clearUpdate = true  //каждый раз при нажатии на кнопки, обновляется список
            when(item.itemId) {
                R.id.id_new_ad -> {
                    val i = Intent(this@MainActivity, EditAdsAct::class.java)
                    startActivity(i)
                }
                R.id.id_my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.ads_my_ads)
                }
                R.id.id_favs -> {
                    firebaseViewModel.loadMyFavs()
                }
                R.id.id_home -> {
                    currentCategory = getString(R.string.dif)   //кнопка home подразумевает категорию "разное"
                    firebaseViewModel.loadAllAdsFirstPage()
                    mainContent.toolbar.title = getString(R.string.dif)
                }
            }
            true
        }
    }

    private fun initRecyclerView() {    // Подключаем адаптер к RecyclerView объявлений пользователя
        binding.apply{
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {    //2.7.2 Когда мы нажали на элемент из главного меню, мы можем проверить id этого элемента
        clearUpdate = true  //каждый раз когда жмем на новую категорию, старый список очищается, и загружается первая страница данной категории
        when(item.itemId){
            R.id.id_my_ads -> {
                Toast.makeText(this, "Pressed id_my_ads", Toast.LENGTH_SHORT).show()
            }
            R.id.id_car -> {
                getAdsFromCat(getString(R.string.ads_car))
            }
            R.id.id_pc -> {
                getAdsFromCat(getString(R.string.ads_pc))
            }
            R.id.id_smart -> {
                getAdsFromCat(getString(R.string.ads_smartphone))
            }
            R.id.id_appliance -> {
                getAdsFromCat(getString(R.string.ads_appliances))
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
                if(myAuth.currentUser?.isAnonymous == true) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
//                Toast.makeText(this, "Pressed id_sign_out", Toast.LENGTH_LONG).show()
                uiUpdate(null)  //6.4
                myAuth.signOut()    //для выхода своей функции писать не нужно, мы воспользуемся функцией класса Auth
                dialogHelper.accHelper.signOutGoogle()  //11.2
            }
        }
//        drawerLayout.closeDrawer(GravityCompat.START)   //При нажатии на одну из кнопок выполнится одно из условий и после этого запустится closeDrawer, и меню закроется
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getAdsFromCat(cat: String) {
        currentCategory = cat   //мы знаем, что за категорию мы нажали. Мы точно знаем, на какой категории мы находимся
        firebaseViewModel.loadAllAdsFromCat(cat)
    }

    fun uiUpdate(user: FirebaseUser?) {  //6.3 в user содержится информация, под каким email мы зарегистрировались и оттуда мы и будем доставать email. Будем доставать email под которым зарегистировались, и передавть user-a
        if(user == null){
            dialogHelper.accHelper.signInAnonymously(object: AccauntHelper.Listener {
                override fun onComplete() {
//                    tvAccaunt.setText(R.string.Guest) //либо:
                    tvAccaunt.text = getString(R.string.Guest)
                    imAccount.setImageResource(R.drawable.ic_account_def)
                }
            })
        }   else if(user.isAnonymous){
            tvAccaunt.text = getString(R.string.Guest)
            imAccount.setImageResource(R.drawable.ic_account_def)
        } else if(!user.isAnonymous) {
            tvAccaunt.text = user.email
            Picasso.get().load(user.photoUrl).into(imAccount)    //Ссылку на картинку гугл-пользователя скачаем с помощью библиотеки Picasso
        }
    }

    override fun onDeleteItem(ad: Ad) {
        firebaseViewModel.deleteItem(ad)
    }

    override fun onAdViewed(ad: Ad) {
        firebaseViewModel.adViewed(ad)
        //При нажатии на экран объявления открываем его на новом активити - DescriptionActivity
        val i = Intent(this, DescriptionActivity::class.java)   //Создаем сначала intent, с помощью которого будем открывать активити и передавать объявление, на которое нажали. Указываем класс, который мы хотимм открыть - DescriptionActivity
        i.putExtra("AD", ad)    //положили в intent информацию, которую хотим передать на этот активити
        startActivity(i)   //запускаем активити, которое мы указали.
    }

    override fun onFavClicked(ad: Ad) {
        firebaseViewModel.onFavClick(ad)
    }

    //Меняем цвет категорий NavigationView прогрaмно:
    private fun navViewSetings() = with(binding) {
        val menu = navView.menu
        val adsCat = menu.findItem(R.id.adsCat)
        val spanAdsCat = SpannableString(adsCat.title)    //Создаем spanable string - специальный класс, который позволяет менять цвет по буквам внутри String
        spanAdsCat.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.color_red)), 0, adsCat.title!!.length, 0)
        adsCat.title = spanAdsCat

        val accountCat = menu.findItem(R.id.accCat)
        val spanAccCat = SpannableString(accountCat.title)
        spanAccCat.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.color_red)), 0, accountCat.title!!.length, 0)
        accountCat.title = spanAccCat
    }

    private fun scrollListener() = with(binding.mainContent) {
        rcView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            //функция, которая следит за состоянием изменения при скроле, берем ее и дополнительно дописываем:
            override fun onScrollStateChanged(recView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recView, newState)
                if(!recView.canScrollVertically(SCROLL_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    clearUpdate = false     //При скролле мы хотим добавить новые объявления, не стирая старые
                    //Когда мы находимся на главной странице и на категории "Разное", тогда ее нужно запускать вот так:
                    val adsList = firebaseViewModel.liveAdsData.value!!
                    if(adsList.isNotEmpty()) {
                        getAdsFromCat(adsList)
                    }
                }
            }
        })
    }

    //Функция для выбора объявлений по категории. Запускается при скролле
    private fun getAdsFromCat(adsList: ArrayList<Ad>) {
        //Если у нас есть элементы(объявления)
        adsList[0].let {
            if (currentCategory == getString(R.string.dif)) {   //и они не находятся в категории "разные"
                firebaseViewModel.loadAllAdsNextPage(it.time)
            } else {
                val catTime = "${it.category}_${it.time}"   //создаем шаблон строки
                firebaseViewModel.loadAllAdsFromCatNextPage(catTime)    //берем объявления из выбранной категории
            }
        }
    }

    companion object {
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        const val SCROLL_DOWN = 1
    }

}