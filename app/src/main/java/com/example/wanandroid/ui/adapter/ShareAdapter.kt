package com.example.wanandroid.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.data.model.bean.AriticleResponse

/**
 *@ author: lkw
 *created on:2020/7/24 13:21
 *description: 分享文章适配器
 *email:lkw@mantoo.com.cn
 */
class ShareAdapter(data: ArrayList<AriticleResponse>) :
    BaseQuickAdapter<AriticleResponse, BaseViewHolder>(
        R.layout.item_share_ariticle, data
    ) {

    init {
        //设置列表进入动画
        setAdapterAnimion(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: AriticleResponse) {
        //赋值
        item.run {
           holder.setText(R.id.item_share_title,item.title)
           holder.setText(R.id.item_share_data,item.niceDate)
        }
    }
}