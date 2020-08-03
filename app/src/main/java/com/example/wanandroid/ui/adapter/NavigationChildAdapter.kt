package com.example.wanandroid.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.ColorUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.data.model.bean.AriticleResponse
import me.hgj.jetpackmvvm.ext.util.toHtml

/**
 *@ author: lkw
 *created on: 2020/8/2 9:31
 *description: 体系itemView适配器
 *email:lkw@mantoo.com.cn
 */
class NavigationChildAdapter(data: ArrayList<AriticleResponse>) :
    BaseQuickAdapter<AriticleResponse, BaseViewHolder>(
        R.layout.flow_layout, data
    ) {
    //设置列表加载动画
    init {
        setAdapterAnimion(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: AriticleResponse) {
        holder.setText(R.id.flow_tag, item.title.toHtml())
        holder.setTextColor(R.id.flow_tag, ColorUtil.randomColor())
    }
}