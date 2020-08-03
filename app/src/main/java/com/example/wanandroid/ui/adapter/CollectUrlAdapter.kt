package com.example.wanandroid.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.app.weight.customview.CollectView
import com.example.wanandroid.data.model.bean.CollectUrlResponse

/**
 *@ author: lkw
 *created on:2020/7/10 9:08
 *description: 收藏网址列表适配器
 *email:lkw@mantoo.com.cn
 */
class CollectUrlAdapter(data: ArrayList<CollectUrlResponse>) :
    BaseQuickAdapter<CollectUrlResponse, BaseViewHolder>(
        R.layout.item_collecturl, data
    ) {

    private var mOnCollectViewClickListener: OnCollectViewClickListener? = null

    init {
        setAdapterAnimion(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: CollectUrlResponse) {
        //赋值
        item.run {
            holder.setText(R.id.item_collectUrl_title, item.name)
            holder.setText(R.id.item_collectUrl_link, item.link)
            holder.getView<CollectView>(R.id.item_collectUrl_collect).isChecked = true
        }
        //小红心点击事件
        holder.getView<CollectView>(R.id.item_collectUrl_collect)
            .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
                override fun onClick(v: CollectView) {
                    mOnCollectViewClickListener?.onClick(item, v, holder.adapterPosition)
                }
            })
    }

    fun setOnCollectViewClickListener(onCollectViewClickListener: OnCollectViewClickListener) {
        mOnCollectViewClickListener = onCollectViewClickListener
    }

    interface OnCollectViewClickListener {
        fun onClick(item: CollectUrlResponse, v: CollectView, position: Int)
    }
}