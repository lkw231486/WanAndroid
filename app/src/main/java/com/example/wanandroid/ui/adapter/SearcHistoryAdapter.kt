package com.example.wanandroid.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.ColorUtil
import com.example.wanandroid.app.utils.SettingUtil

/**
 *@ author: lkw
 *created on:2020/7/21 11:22
 *description: 搜索历史列表适配器
 *email:lkw@mantoo.com.cn
 */
class SearcHistoryAdapter(data:MutableList<String>):BaseQuickAdapter<String,BaseViewHolder>(R.layout.item_history,data){

    init {
        setAdapterAnimion(SettingUtil.getListMode())
    }
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.item_history_text,item)
        holder.setTextColor(R.id.item_history_text,ColorUtil.randomColor())
    }

}