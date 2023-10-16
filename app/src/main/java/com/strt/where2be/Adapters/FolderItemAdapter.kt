package com.strt.where2be.Adapters

import android.content.Context
import android.icu.text.CaseMap.Fold
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.strt.where2be.Activities.BaseActivity
import com.strt.where2be.Models.Folders
import com.strt.where2be.R

open class FolderItemAdapter(private val context:Context,private val list:ArrayList<Folders>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener:OnClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return folderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_folder,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model =list[position]
        if (holder is folderViewHolder){
            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.add_folder_button)
                .into(holder.itemView.findViewById(R.id.iv_list_item_folder_image))

            holder.itemView.findViewById<TextView>(R.id.tv_list_item_folder_name).text=model.name
            holder.itemView.setOnClickListener{
                if (onClickListener!=null){
                    onClickListener!!.onClick(position,model)
                }

            }
        }

    }

    interface OnClickListener{
        fun onClick(position: Int,model:Folders)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

    private class folderViewHolder(view: View):RecyclerView.ViewHolder(view)

}