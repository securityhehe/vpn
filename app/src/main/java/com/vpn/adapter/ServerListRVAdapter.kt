package com.vpn.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.vpn.adapter.ServerListRVAdapter.MyViewHolder
import com.vpn.ui.home.NavItemClickListener
import com.bumptech.glide.Glide
import com.vpn.R
import com.vpn.bean.Server
import com.vpn.databinding.ServerSelectActivityBinding
import com.vpn.databinding.ServerSelectItemBinding
import com.vpn.ui.home.IChangeServer
import java.util.ArrayList

class ServerListRVAdapter( private val mContext: Context ) :RecyclerView.Adapter<MyViewHolder>() {
    var serverLists: ArrayList<Server> = arrayListOf()
    private val listener: IChangeServer = mContext as IChangeServer
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val V = ServerSelectItemBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return MyViewHolder(V);
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val server = serverLists[position]
        holder.V.countryTv.text = server.country
        Glide.with(mContext).load(server.flagUrl).into( holder.V.iconImg)
        holder.V.radio.isChecked = server.isSelect
        holder.V.serverItemLayout.setOnClickListener {
            listener.clickedItem(position,server)
        }
    }

    override fun getItemCount(): Int {
        return serverLists.size
    }

    inner class MyViewHolder(var V:ServerSelectItemBinding ) : RecyclerView.ViewHolder(V.root) ;

    @SuppressLint("NotifyDataSetChanged")
    fun changeState(state:Boolean, position: Int){
        serverLists.forEach {
           it.isSelect = false
        }
        if(position >=0 && position < serverLists.size-1){
           serverLists[position].isSelect = state
        }
        notifyDataSetChanged()
    }

}