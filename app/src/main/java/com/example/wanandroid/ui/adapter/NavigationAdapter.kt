package com.example.wanandroid.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.ext.setNbOnItemClickListener
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.NavigationResponse
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import me.hgj.jetpackmvvm.ext.util.toHtml

/**
 *@ author: lkw
 *created on: 2020/8/2 8:59
 *description: 体系页面适配器
 *email:lkw@mantoo.com.cn
 */
class NavigationAdapter(data: ArrayList<NavigationResponse>) :
    BaseQuickAdapter<NavigationResponse, BaseViewHolder>(
        R.layout.item_system, data
    ) {

    //创建item点击事件接口对象
    private lateinit var navigationClickInterFace: NavigationClickInterFace

    //初始化列表动画
    init {
        setAdapterAnimion(SettingUtil.getListMode())
    }

    override fun convert(holder: BaseViewHolder, item: NavigationResponse) {
        holder.setText(R.id.item_system_title, item.name.toHtml())
        holder.getView<RecyclerView>(R.id.item_system_rv).run {
            val flexboxLayoutManager: FlexboxLayoutManager by lazy {
                FlexboxLayoutManager(context).apply {
                    // 方面 主轴为水平方向，起点左端
                    flexDirection = FlexDirection.ROW
                    //左对齐
                    justifyContent = JustifyContent.FLEX_START
                }
            }
            layoutManager = flexboxLayoutManager
            setHasFixedSize(true)
            adapter = NavigationChildAdapter(item.articles).apply {
                setNbOnItemClickListener { adapter, view, position ->
                    //传递item点击事件
                    navigationClickInterFace.onNavigationClickListener(
                        item.articles[position],
                        view
                    )
                }
            }
        }
    }

    //添加item点击事件接口
    interface NavigationClickInterFace {
        fun onNavigationClickListener(item: AriticleResponse, view: View)
    }


    //初始化对象
    fun setNavigationClickInterFace(face: NavigationClickInterFace) {
        navigationClickInterFace = face
    }

}