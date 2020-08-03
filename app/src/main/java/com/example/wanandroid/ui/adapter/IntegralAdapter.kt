package com.example.wanandroid.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.data.model.bean.IntegralResponse

/**
 *@ author: lkw
 *created on:2020/7/20 13:12
 *description: 积分排行列表适配器
 *email:lkw@mantoo.com.cn
 */
class IntegralAdapter(date: ArrayList<IntegralResponse>) :
    BaseQuickAdapter<IntegralResponse, BaseViewHolder>(
        R.layout.item_integral, date
    ) {

    private var rankNum: Int = -1

    constructor(date: ArrayList<IntegralResponse>, rank: Int) : this(date) {
        this.rankNum = rank
    }

    /**
     * 设置列表动画
     */
    init {
        setAdapterAnimion(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: IntegralResponse) {
        //赋值
        item.run {
            if (rankNum == holder.adapterPosition + 1) {
                holder.setTextColor(R.id.item_integral_rank, SettingUtil.getColor(context))
                holder.setTextColor(R.id.item_integral_name, SettingUtil.getColor(context))
                holder.setTextColor(R.id.item_integral_count, SettingUtil.getColor(context))
            } else {
                holder.setTextColor(R.id.item_integral_rank, R.color.colorBlack333)
                holder.setTextColor(R.id.item_integral_name, R.color.colorBlack666)
                holder.setTextColor(R.id.item_integral_count, R.color.textHint)
            }
            holder.setText(R.id.item_integral_rank, "${holder.adapterPosition + 1}")
            holder.setText(R.id.item_integral_name, username)
            holder.setText(R.id.item_integral_count, coinCount.toString())
        }
    }
}