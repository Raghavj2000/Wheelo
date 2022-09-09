package com.example.wheelchairguidance.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wheelchairguidance.R
import org.osmdroid.bonuspack.location.POI

class RecyclerViewAdapter(private val onClickListener: OnClickListener) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    var poiList = mutableListOf<POI>()

    fun setList(pois: List<POI>) {
        this.poiList = pois.toMutableList()
        notifyDataSetChanged()
    }

    class OnClickListener(val clickListener: (poi: POI) -> Unit) {
        fun onClick(poi: POI) = clickListener(poi)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView
        var itemDescription: TextView

        init {
            itemTitle = itemView.findViewById(R.id.poiTitle)
            itemDescription = itemView.findViewById(R.id.poiDescription)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.d("Raghav123", "onCreate: " + poiList)
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        val list = poiList!![position].mDescription.split(",", limit = 2)
        holder.itemTitle.text = list[0]
        holder.itemDescription.text = list[1].trim()
        holder.itemView.setOnClickListener {
            onClickListener.onClick(poiList!![position])
        }
    }

    override fun getItemCount(): Int {
        return poiList?.size ?: 0
    }

}