package com.MikeKrysan.myapplication.model

//Класс, с помощью которого можно будет фильтровать объявления в базе данных по времени и категории(машины, бытовая техника, смартфоны ...)
data class AdFilter(
    val time: String? = null,
    val catTime: String? = null
)

