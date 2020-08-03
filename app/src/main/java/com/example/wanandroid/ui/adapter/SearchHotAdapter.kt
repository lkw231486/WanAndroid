package com.example.wanandroid.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.ColorUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.data.model.bean.SearchResponse

/**
 *@ author: lkw
 *created on:2020/7/21 11:12
 *description: 搜索热点列表适配器
 *email:lkw@mantoo.com.cn
 */
class SearchHotAdapter(data: ArrayList<SearchResponse>) :
    BaseQuickAdapter<SearchResponse, BaseViewHolder>(
        R.layout.flow_layout, data
    ) {

    init {
        setAdapterAnimion(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: SearchResponse) {
        holder.setText(R.id.flow_tag, item.name)
        holder.setTextColor(R.id.flow_tag, ColorUtil.randomColor())
    }
}