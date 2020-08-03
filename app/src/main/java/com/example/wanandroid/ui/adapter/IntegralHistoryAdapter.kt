package com.example.wanandroid.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.DatetimeUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.data.model.bean.IntegralHistoryResponse

/**
 *@ author: lkw
 *created on:2020/7/20 16:34
 *description: 积分获取历史记录列表适配器
 *email:lkw@mantoo.com.cn
 */
class IntegralHistoryAdapter(data: ArrayList<IntegralHistoryResponse>) :
    BaseQuickAdapter<IntegralHistoryResponse, BaseViewHolder>(
        R.layout.item_integral_history, data
    ) {

    init {
        //设置列表加载动画
        setAdapterAnimion(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: IntegralHistoryResponse) {
        //赋值
        item.run {
            val descStr =
                if (desc.contains("积分")) desc.subSequence(desc.indexOf("积分"), desc.length) else ""
            holder.setText(R.id.item_integralhistory_title, reason + descStr)
            holder.setText(R.id.item_integralhistory_date, DatetimeUtil.formatDate(date,DatetimeUtil.DATE_PATTERN_SS))
            holder.setText(R.id.item_integralhistory_count,"+$coinCount")
            holder.setTextColor(R.id.item_integralhistory_count, SettingUtil.getColor(context))
        }
    }
}