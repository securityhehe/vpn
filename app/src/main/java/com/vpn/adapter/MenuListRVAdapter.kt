package com.vpn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.vpn.ui.home.NavItemClickListener
import com.vpn.bean.MenuItem
import com.vpn.databinding.MainMenuListItemBinding
import java.util.ArrayList

class MenuListRVAdapter( private val serverLists: ArrayList<MenuItem>, private val mContext: Context ) : RecyclerView.Adapter<MenuListRVAdapter.MenuViewHolder>() {

    var listener :NavItemClickListener = mContext as NavItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val bind = MainMenuListItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MenuViewHolder(bind)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = serverLists[position]
        holder.vh.iconImg.setImageResource(menuItem.icon)
        holder.vh.text.setText(menuItem.text)
        holder.vh.go.setImageResource(menuItem.go)
        holder.vh.root.setOnClickListener { listener.clickedItem(menuItem.opt) }
    }

    override fun getItemCount(): Int {
        return serverLists.size
    }
    inner class MenuViewHolder(var vh: MainMenuListItemBinding) : RecyclerView.ViewHolder(vh.root) {}

}