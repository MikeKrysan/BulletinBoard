package com.MikeKrysan.myapplication.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchMoveCallback(val adapter:ItemTouchAdapter) : ItemTouchHelper.Callback(){   //19.3    //19.6  (val adapter:ItemTouchAdapter)
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {   //указатель, какие именно движения я хочу замечать
        val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN   //19.3.1
        return makeMovementFlags(dragFlag, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.onMove(viewHolder.adapterPosition, target.adapterPosition)    //19.6 Дублируем движения с адаптера SelectImageRvAdapter
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {    //19.7 Функция запускается, когда мы нажали на наш элемент
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE)viewHolder?.itemView?.alpha = 0.5f  //Делаем проверку на наличие нуля. То-есть если элемент не выбран то AS пропускает данную строку
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {   //19.8. Безопасный запуск функции. Ноль не прийдет
        viewHolder.itemView.alpha = 1.0f    //Раз ноль не прийдет, значит можно убрать вопросительные знаки
        super.clearView(recyclerView, viewHolder)
    }

    interface ItemTouchAdapter {    //19.4
        fun onMove(startPos:Int, targetPos:Int)
    }
}