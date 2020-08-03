package com.example.wanandroid.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.ext.setNbOnItemClickListener
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.data.model.bean.SystemResponse
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import me.hgj.jetpackmvvm.ext.util.toHtml

/**
 *@ author: lkw
 *created on: 2020/8/2 10:37
 *description: 体系适配器
 *email:lkw@mantoo.com.cn
 */
class SystemAdapter(data: ArrayList<SystemResponse>) :
    BaseQuickAdapter<SystemResponse, BaseViewHolder>(
        R.layout.item_system, data
    ) {

    private var method: (data: SystemResponse, view: View, position: Int) -> Unit =
        { _: SystemResponse, _: View, _: Int -> }

    init {
        setAdapterAnimion(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: SystemResponse) {
        holder.setText(R.id.item_system_title, item.name.toHtml())
        holder.getView<RecyclerView>(R.id.item_system_rv).run {
            val flexboxLayoutManager: FlexboxLayoutManager by lazy {
                FlexboxLayoutManager(context).apply {
                    //水平方向
                    flexDirection = FlexDirection.ROW
                    //从左到右
                    justifyContent = JustifyContent.FLEX_START
                }
            }
            layoutManager=flexboxLayoutManager
            setHasFixedSize(true)
            setItemViewCacheSize(200)
            isNestedScrollingEnabled=false
            adapter=SystemChildAdapter(item.children).apply {
                setNbOnItemClickListener { _, view, position ->
                    method(item, view, position)
                }
            }
        }
    }

    interface SystemClickInterFace {
        fun onSystemClickListener(item: SystemResponse, position: Int, view: View)
    }

    fun setChildClick(method: (data: SystemResponse, view: View, position: Int) -> Unit) {
        this.method = method
    }
}