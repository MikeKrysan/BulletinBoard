package com.MikeKrysan.myapplication.utils

import com.MikeKrysan.myapplication.model.Ad
import com.MikeKrysan.myapplication.model.AdFilter

object FilterManager {
    fun createFilter(ad: Ad): AdFilter {
        return AdFilter(
            ad.time,
            "${ad.category}_${ad.time}",

            "${ad.category}_${ad.country}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.withSend}_${ad.time}",

            "${ad.country}_${ad.withSend}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.withSend}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.withSend}_${ad.time}"

        )
    }

    //Создаем еще одну функцию которая будет определять, что есть в сформированной строке и создать String, как в базе данных(China_empty_empty_time -> country_withSend_time)
    fun getFilterNode(filter: String):String {
        val sBuilderNode = StringBuilder()
        val sBuilderFilter = StringBuilder()    //84.1 Создаем StringBuilder для самих реальных названий
        val tempArray = filter.split("_")
        if(tempArray[0] != "empty") {
            sBuilderNode.append("country_")
            sBuilderFilter.append("${tempArray[0]}_")
        }
        if(tempArray[1] != "empty") {
            sBuilderNode.append("city_")
            sBuilderFilter.append("${tempArray[1]}_")
        }
        if(tempArray[2] != "empty") {
            sBuilderNode.append("index_")
            sBuilderFilter.append("${tempArray[2]}_")
        }
        sBuilderFilter.append(tempArray[3])
        sBuilderNode.append("withSend_time")     //время добавляем без всяких условий, потому что оно везде добавляется
        return "$sBuilderNode|$sBuilderFilter"
    }
}