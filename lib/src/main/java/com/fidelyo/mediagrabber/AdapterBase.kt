package com.fidelyo.mediagrabber

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

abstract class AdapterBase<I : AdapterBase.Item, V : AdapterBase.ViewHolderBase<I>> : RecyclerView.Adapter<V>() {

    private var items: ArrayList<I> = arrayListOf()

    var onItemClickListener: OnItemClickListener<I>? = null

    fun showAll(p0: ArrayList<I>) {
        items.clear()
        items.addAll(p0)
        notifyDataSetChanged()
    }

    fun append(p0: ArrayList<I>) {
        items.addAll(p0)
        notifyItemRangeInserted(items.size - p0.size, p0.size)
    }

    fun append(p0: I) {
        val position = items.size
        if (items.add(p0))
            notifyItemInserted(position)
    }

    fun remove(p0: I) {
        val position = items.indexOf(p0)
        if (items.remove(p0))
            notifyItemRemoved(position)
    }

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }

    fun onItemClickListener(handler: (I, View) -> Unit) {
        onItemClickListener = object : OnItemClickListener<I> {
            override fun onClicked(i: I, view: View) {
                handler(i, view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return Item.TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
        return onCreateItemViewHolder(parent, viewType).apply {
            onItemClickListener = this@AdapterBase.onItemClickListener
        }
    }

    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): V

    override final fun onBindViewHolder(holder: V, position: Int) {
        holder.bind(items[position])
    }

    override final fun onViewRecycled(holder: V) {
        holder.recycle()
    }

    override final fun getItemCount() = items.size

    interface Item {

        companion object {
            val TYPE = 0
        }

    }

    interface OnItemClickListener<I : Item> {

        fun onClicked(i: I, view: View)
    }

    abstract class ViewHolderBase<I : AdapterBase.Item>(view: View) : RecyclerView.ViewHolder(view) {

        var onItemClickListener: AdapterBase.OnItemClickListener<I>? = null

        lateinit var i: I

        fun bind(i: I) {

            this.i = i
            this.itemView.setOnClickListener { this.onItemClickListener?.onClicked(i, itemView) }

            onAttached(i)
        }

        fun recycle() {
            onDetached(i)
        }

        abstract fun onAttached(i: I)

        abstract fun onDetached(i: I)

    }
}
