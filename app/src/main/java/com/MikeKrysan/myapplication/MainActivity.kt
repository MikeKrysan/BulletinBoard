package com.MikeKrysan.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*

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
 ** 2.2 Добавляем NavigationView в разметку activity_main.xml
 * 2.2.1 добавляем идентификатор: navView
 * 2.2.2 app:menu="@menu/drawer_main_menu" - Указываем, какое меню мы хотим, чтобы указывалось внутри нашего контейнера NavigationView
 * 2.2.3 android:layout_gravity="start" - чтобы меню не растягивалось на весь экран (контейнер NavigationView) и выдвигалось слева направо
 * 2.2.4 android:fitsSystemWindows="true" - этот элемент будет задвинут по умолчанию слева
 * 2.2.5 в drawerLayout прописываем tools:openDrawer="start" - элемент выдвижного меню id=navView будет отображен
 *
 * 3. Добавим header для выдвижного меню. В нем будет картинка и почта, по которой я зарегистрировался. Картинка будет показыватся после того, как будет сделана регистрация через гугл аккаунт
 * Если регистрация произведена по почте, то картинка отображатся не будет
 * 3.1 Создаем layout_resources_file в папке layot и називаем его nav_header_main.xml
 * 3.2 Header включаем в NavigationView, который находится на activity_main : app:headerLayout="@layout/nav_header_main". Таким образом вживился nav_header_main.xml в navigationView
 *
 * 4. Если на данном этапе запустить приложение, то action bar будет впереди выдвигающегося меню, это будет неудобно для пользователя. Для этого спрячем action bar и status tool bar за выдвигающееся меню.
 * 4.1 В values->themes->themes создаем свою тему, назвав name="AppTheme.NoActionBar"
 * 4.2 <item name="windowActionBar">false</item> - скрываем ActionBar. Но AS попытается напечатать текст названия приложения, который прописан в манифесте, поэтому добавляем еще одно условие: <item name="windowNoTitle">true</item>
 * 4.3 Чтобы исчез ItemBar, нужно прописать: <item name="android:windowFullscreen">true</item>
 * 4.4 Делаем то же для темной темы, скопировав со светлой
 * 4.5 Чтобы применить созданную тему, идем в манифест и чтобы применить ко всему приложению свою тему, я пишу android:theme="@style/AppTheme.NoActionBar"
 *
 * 5 Добавим наш собственный ActionBar, и чтобы он был за нашим выдвижным меню:
 * 5.1 Создаем новое активити и даем ему название main_content.xml - это активити, на котором будет в ActionBar кнопка вызова главного меню и сам список наших объявлений
 * 5.2 Чтобы подключить main_content.xml к основной разметке activity_main.xml, необходимо добавить в разметку activity_main <include layout="@layout/main_content"...
 *
 * 6. Создаем кнопку в ActionBar для открытия меню
 * 6.1 Чтобы обратится напрямую к view по их идентификаторам в новых версиях котлин необходимо в build.gradle  прописать: apply plugin: 'kotlin-android-extensions'. Иначе нужно создавать переменные и в них помещать findViewById
 * 6.2 Создаем функцию для инициализации view fun init()
 * 6.3 Создадим переменную toggle. Класс ActionBarDrawerToggle не может принимать строковое значение(ожидает ресурс Int), поэтому создадим два ресурса в файле strings.xml open и close
 * 6.4 Указываем, что кнопка toggle будет открывать наше меню (driverLayout
 *
 * 7. Прослушаем нажатия кнопок нашего меню NavigationView, можно добавить для всего класса интерфейс OnNavigationItemSelectedListener
 * 7.1 Как нам сказать, что при нажатии на кнопки в меню NavigationView мы передаем события в интерфейс OnNavigationItemSelectedListener? navView.setNavigationItemSelectedListener(this) - данные о событии нажатия передаются в наш класс, так как интерфейс встроен в класс
 *7.2 Когда мы нажали на элемент из главного меню, мы можем проверить id этого элемента
 *
 *

 */

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)  //6.3  Создали кнопку toggle
        drawerLayout.addDrawerListener(toggle)  //6.4
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this) //7.1
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {    //7.2 Когда мы нажали на элемент из главного меню, мы можем проверить id этого элемента
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

                Toast.makeText(this, "Pressed id_sign_up", Toast.LENGTH_LONG).show()

            }
            R.id.id_sign_in -> {

                Toast.makeText(this, "Pressed id_sign_in", Toast.LENGTH_LONG).show()

            }
            R.id.id_sign_out -> {

                Toast.makeText(this, "Pressed id_sign_out", Toast.LENGTH_LONG).show()

            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)   //При нажатии на одну из кнопок выполнится одно из условий и после этого запустится closeDrawer, и меню закроется
        return true
    }

}