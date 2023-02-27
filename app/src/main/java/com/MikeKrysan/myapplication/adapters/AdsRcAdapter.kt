package com.MikeKrysan.myapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.MikeKrysan.myapplication.MainActivity
import com.MikeKrysan.myapplication.act.EditAdsAct
import com.MikeKrysan.myapplication.databinding.AdListItemBinding
import com.MikeKrysan.myapplication.model.Ad

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
            showEditPanel(isOwner(ad))
            itemView.setOnClickListener {
                act.onAdViewed(ad)
            }
            ibEditAd.setOnClickListener(onClickEdit(ad))
            ibDeleteAd.setOnClickListener{
                act.onDeleteItem(ad)
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
    }

}