package com.MikeKrysan.myapplication.model

//Класс, с помощью которого можно будет фильтровать объявления в базе данных по времени и категории(машины, бытовая техника, смартфоны ...)
data class AdFilter(
    val time: String? = null,
    val cat_time: String? = null,
    //добавляем все возможные варианты комбинаций отправки фильтров на базу данных:
    //три параметра всегда будут: cat, time, withSent.
    //Filter with category
    val cat_country_withSent_time:String? = null,
    val cat_country_city_withSent_time:String? = null,
    val cat_country_city_index_withSent_time:String? = null,
    val cat_index_withSent_time:String? = null,
    val cat_withSent_time:String? = null,
    //Возможно, пользователь будет фильтровать зайдя из категории по-умолчанию "разные", тогда категория cat не будет передаваться
    //Filter without category (category "Dif")
    val country_withSent_time:String? = null,
    val country_city_withSent_time:String? = null,
    val country_city_index_withSent_time:String? = null,
    val index_withSent_time:String? = null,
    val withSent_time:String? = null
    )

