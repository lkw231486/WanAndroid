package com.example.wanandroid.ui.adapter

import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.app.weight.customview.CollectView
import com.example.wanandroid.data.model.bean.CollectResponse
import me.hgj.jetpackmvvm.ext.util.toHtml

/**
 *@ author: lkw
 *created on:2020/7/9 14:58
 *description: 收藏页面适配器
 *email:lkw@mantoo.com.cn
 */
class CollectAdapter(data: ArrayList<CollectResponse>) :
    BaseDelegateMultiAdapter<CollectResponse, BaseViewHolder>(data) {
    private var onCollectViewClickListener: OnCollectViewClickListener? = null
    private val Article = 1//文章类型
    private val Project = 0//项目类型

    init {
        setAdapterAnimion(SettingUtil.getListMode())
        //第一步 设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<CollectResponse>() {
            override fun getItemType(data: List<CollectResponse>, position: Int): Int {
                //根据是否有图片 判断为文章还是项目，好像有点low的感觉。。。我看实体类好像没有相关的字段，就用了这个，也有可能是我没发现
                return if (TextUtils.isEmpty(data[position].envelopePic)) Article else Project
            }
        })
        //绑定布局，绑定item的类型
        getMultiTypeDelegate()?.let {
            it.addItemType(Article, R.layout.item_ariticle)
            it.addItemType(Project, R.layout.item_project)
        }
    }

    override fun convert(holder: BaseViewHolder, item: CollectResponse) {
        when (holder.itemViewType) {
            Article -> {
                //文章布局赋值
                item.run {
                    holder.setText(R.id.item_home_author, if (author.isEmpty()) "匿名用户" else author)
                    holder.setText(R.id.item_home_content, title.toHtml())
                    holder.setText(R.id.item_home_type2, chapterName.toHtml())
                    holder.setText(R.id.item_home_date, niceDate)
                    holder.getView<CollectView>(R.id.item_home_collect).isChecked = true
                    //隐藏所有标签
                    holder.setGone(R.id.item_home_top, true)
                    holder.setGone(R.id.item_home_new, true)
                    holder.setGone(R.id.item_home_type1, true)
                }
                holder.getView<CollectView>(R.id.item_home_collect)
                    .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
                        override fun onClick(v: CollectView) {
                            onCollectViewClickListener?.onClick(item, v, holder.adapterPosition)
                        }
                    })
            }

            Project -> {
                //项目布局赋值
                item.run {
                    holder.setText(
                        R.id.item_project_author,
                        if (author.isEmpty()) "匿名用户" else author
                    )
                    holder.setText(R.id.item_project_title, title.toHtml())
                    holder.setText(R.id.item_project_content, desc.toHtml())
                    holder.setText(R.id.item_project_type, chapterName.toHtml())
                    holder.setText(R.id.item_project_date, niceDate)
                    //隐藏所有的标签
                    holder.setGone(R.id.item_project_top,true)
                    holder.setGone(R.id.item_project_new,true)
                    holder.setGone(R.id.item_project_type1,true)
                    holder.getView<CollectView>(R.id.item_project_collect).isChecked=true
                    Glide.with(context.applicationContext).load(envelopePic)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(holder.getView(R.id.item_project_imageview))
                }
                holder.getView<CollectView>(R.id.item_project_collect)
                    .setOnCollectViewClickListener(object :CollectView.OnCollectViewClickListener{
                        override fun onClick(v: CollectView) {
                         onCollectViewClickListener?.onClick(item,v,holder.adapterPosition)
                        }
                    })
            }
        }
    }


    fun setOnCollectViewClickListener(onCollectViewClickListener: OnCollectViewClickListener) {
        this.onCollectViewClickListener = onCollectViewClickListener
    }

    interface OnCollectViewClickListener {
        fun onClick(item: CollectResponse, v: CollectView, position: Int)
    }
}