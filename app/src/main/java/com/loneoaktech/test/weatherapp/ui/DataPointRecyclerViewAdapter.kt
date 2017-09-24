package com.loneoaktech.test.weatherapp.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loneoaktech.test.weatherapp.model.DataPoint

/**
 * Recycler view for display weather DataPoints.  Mapping is via a lamda using the Kotlin android plugin mapping
 * Created by BillH on 9/24/2017.
 */
class DataPointRecyclerViewAdapter(
        private val context: Context,
        private val layoutResource: Int,
        private val bind: (v: View) -> Unit)
    : RecyclerView.Adapter<DataPointRecyclerViewAdapter.ViewHolder> (){

    private val _data : MutableList<DataPoint> = mutableListOf()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var datapoint: DataPoint?=null
    }

    public fun setData(newData: List<DataPoint>){
        _data.clear()
        _data.addAll(newData)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = _data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(context).inflate(layoutResource, parent, false))

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.apply{
            datapoint = _data[position]
            bind(this.itemView)
        }
    }

}