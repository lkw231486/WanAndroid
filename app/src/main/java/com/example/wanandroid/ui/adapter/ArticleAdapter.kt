package com.example.wanandroid.ui.adapter

import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.ext.setAdapterAnimion
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.app.weight.customview.CollectView
import com.example.wanandroid.data.model.bean.AriticleResponse
import me.hgj.jetpackmvvm.ext.util.toHtml

/**
 *@ author: lkw
 *created on:2020/7/6 9:34
 *description: 首页recyclerView适配器
 *email:lkw@mantoo.com.cn
 */
class ArticleAdapter(date: MutableList<AriticleResponse>?) :
    BaseDelegateMultiAdapter<AriticleResponse, BaseViewHolder>(date) {

    private var mOnCollectViewClickListener: OnCollectViewClickListener? = null
    private val Article = 1 //文章类型
    private val Project = 2//项目类型
    private var showTag = false//是否展示标签，tag一般主页才用的到

    constructor(date: MutableList<AriticleResponse>?, showTag: Boolean) : this(date) {
        this.showTag = showTag
    }

    init {
        setAdapterAnimion(SettingUtil.getListMode())
        //第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<AriticleResponse>() {
            override fun getItemType(data: List<AriticleResponse>, position: Int): Int {
                //根据是否有图片 判断为文章还是项目，好像有点low的感觉。。。我看实体类好像没有相关的字段，就用了这个，也有可能是我没发现
                return if (TextUtils.isEmpty(data[position].envelopePic)) Article else Project
            }
        })
        //第二步，绑定布局文件
        getMultiTypeDelegate()?.let {
            it.addItemType(Article, R.layout.item_ariticle)
            it.addItemType(Project, R.layout.item_project)
        }
    }

    override fun convert(holder: BaseViewHolder, item: AriticleResponse) {
        when (holder.itemViewType) {
            Article -> {
                //文章布局赋值
                item.run {
                    holder.setText(
                        R.id.item_home_author,
                        if (author.isNotEmpty()) author else shareUser
                    )
                    holder.setText(R.id.item_home_content, title.toHtml())
                    holder.setText(R.id.item_home_type2, "$superChapterName·$chapterName".toHtml())
                    holder.setText(R.id.item_home_date, niceDate)
                    holder.getView<CollectView>(R.id.item_home_collect).isChecked = collect
                    if (showTag) {
                        //展示标签
                        holder.setGone(R.id.item_home_new, !fresh)
                        holder.setGone(R.id.item_home_top, type != 1)
                        if (tags.isNotEmpty()) {
                            holder.setGone(R.id.item_home_type1, false)
                            holder.setText(R.id.item_home_type1, tags[0].name)
                        } else {
                            holder.setGone(R.id.item_home_type1, true)
                        }
                    } else {
                        holder.setGone(R.id.item_home_top, true)
                        holder.setGone(R.id.item_home_type1, true)
                        holder.setGone(R.id.item_home_new, true)
                    }
                }
                holder.getView<CollectView>(R.id.item_home_collect)
                    .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
                        override fun onClick(v: CollectView) {
                            mOnCollectViewClickListener?.onClick(item, v, holder.adapterPosition)
                        }
                    })
            }

            Project -> {
                //项目布局赋值
                item.run {
                    holder.setText(
                        R.id.item_project_author,
                        if (author.isNotEmpty()) author else shareUser
                    )
                    holder.setText(R.id.item_project_title, title.toHtml())
                    holder.setText(R.id.item_project_content, desc.toHtml())
                    holder.setText(
                        R.id.item_project_type,
                        "$superChapterName·$chapterName".toHtml()
                    )
                    holder.setText(R.id.item_project_date, niceDate)
                    if (showTag) {
                        //展示标签
                        holder.setGone(R.id.item_project_top, type != 1)
                        holder.setGone(R.id.item_project_new, !fresh)
                        if (tags.isNotEmpty()) {
                            holder.setGone(R.id.item_project_type1, false)
                            holder.setText(R.id.item_project_type1, tags[0].name)
                        } else {
                            holder.setGone(R.id.item_project_type1, true)
                        }
                    } else {
                        holder.setGone(R.id.item_project_top, true)
                        holder.setGone(R.id.item_project_new, true)
                        holder.setGone(R.id.item_project_type1, true)
                    }
                    holder.getView<CollectView>(R.id.item_project_collect).isChecked = collect
                    Glide.with(context).load(envelopePic)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(holder.getView(R.id.item_project_imageview))
                }
                holder.getView<CollectView>(R.id.item_project_collect)
                    .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
                        override fun onClick(v: CollectView) {
                            mOnCollectViewClickListener?.onClick(item, v, holder.adapterPosition)
                        }
                    })
            }
        }
    }


    fun setOnCollectViewClickListener(onCollectViewClickListener: OnCollectViewClickListener) {
        this.mOnCollectViewClickListener = onCollectViewClickListener
    }

    interface OnCollectViewClickListener {
        fun onClick(item: AriticleResponse, v: CollectView, position: Int)
    }
}