package com.MikeKrysan.myapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.MainActivity
import com.MikeKrysan.myapplication.R
import com.MikeKrysan.myapplication.act.DescriptionActivity
import com.MikeKrysan.myapplication.act.EditAdsAct
import com.MikeKrysan.myapplication.databinding.AdListItemBinding
import com.MikeKrysan.myapplication.model.Ad
import com.squareup.picasso.Picasso

class AdsRcAdapter(val act: MainActivity) : RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {

    val adArray = ArrayList<Ad>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)    //Recycler View мы вжывляем внуть другого RecyclerView
        return AdHolder(binding, act)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }

    override fun getItemCount(): Int {
        return adArray.size
    }

    fun updateAdapter(newList : List<Ad>) {
        val tempArray = ArrayList<Ad>()//Создаем временный список, чтобы при добавлении объявлнений пакетами(pagination) из базы данных,  старый список не стирало
        tempArray.addAll(adArray)    //добавляем весь старый список
        tempArray.addAll(newList)    // И добавляем еще список новый

        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adArray, tempArray))
        diffResult.dispatchUpdatesTo(this)
        adArray.clear() //стираем старый список
        adArray.addAll(tempArray) //записываем новый список, в котором есть старые и новые элементы
    }

    fun updateAdapterWithClear(newList : List<Ad>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(adArray, newList))
        diffResult.dispatchUpdatesTo(this)
        adArray.clear()
        adArray.addAll(newList)
    }

    class AdHolder(val binding : AdListItemBinding, val act: MainActivity) : RecyclerView.ViewHolder(binding.root){

        fun setData(ad: Ad) = with(binding) {
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvTitle.text = ad.title
            tvViewCounter.text = ad.viewsCounter
            tvFavCounter.text = ad.favCounter
            Picasso.get().load(ad.mainImage).into(mainImage)
            isFav(ad)
            showEditPanel(isOwner(ad))
            mainOnClick(ad)
        }

        private fun mainOnClick(ad: Ad) = with(binding){
            ibFav.setOnClickListener{
                if(act.myAuth.currentUser?.isAnonymous == false) act.onFavClicked(ad)
            }
            itemView.setOnClickListener {
                act.onAdViewed(ad)
            }
            ibEditAd.setOnClickListener(onClickEdit(ad))
            ibDeleteAd.setOnClickListener{
                act.onDeleteItem(ad)
            }
        }

        private fun isFav(ad: Ad) {
            if(ad.isFav) {
                binding.ibFav.setImageResource(R.drawable.ic_fav_pressed)
            } else {
                binding.ibFav.setImageResource(R.drawable.ic_fav_normal)
            }
        }

        private fun onClickEdit(ad : Ad) : View.OnClickListener {
            return View.OnClickListener {
                val editIntent = Intent(act, EditAdsAct::class.java).apply{
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.ADS_DATA, ad)
                }
                act.startActivity(editIntent)
            }
        }

        private fun isOwner(ad: Ad): Boolean {
            return ad.uid == act.myAuth.uid
        }

        private fun showEditPanel(isOwner: Boolean) {
            if(isOwner) {
                binding.editPanel.visibility = View.VISIBLE
            } else {
                binding.editPanel.visibility = View.GONE

            }
        }

    }

    interface Listener{
        fun onDeleteItem(ad: Ad)
        fun onAdViewed(ad: Ad)
        fun onFavClicked(ad: Ad)
    }

}