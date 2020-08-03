package com.example.wanandroid.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.ColorUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.ClassifyResponse
import me.hgj.jetpackmvvm.ext.util.toHtml

/**
 *@ author: lkw
 *created on: 2020/8/2 10:48
 *description:
 *email:lkw@mantoo.com.cn
 */
class SystemChildAdapter(data: ArrayList<ClassifyResponse>) :
    BaseQuickAdapter<ClassifyResponse, BaseViewHolder>(
        R.layout.flow_layout, data
    ) {
    //设置列表加载动画
    init {
        setAdapterAnimion(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: ClassifyResponse) {
        holder.setText(R.id.flow_tag, item.name.toHtml())
        holder.setTextColor(R.id.flow_tag, ColorUtil.randomColor())
    }
}