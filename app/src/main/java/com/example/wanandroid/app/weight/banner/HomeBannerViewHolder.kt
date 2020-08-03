package com.example.wanandroid.app.weight.banner

import android.media.Image
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.wanandroid.R
import com.example.wanandroid.data.model.bean.BannerResponse
import com.zhpan.bannerview.holder.ViewHolder

/**
 *@ author: lkw
 *created on:2020/7/7 17:04
 *description:
 *email:lkw@mantoo.com.cn
 */
class HomeBannerViewHolder : ViewHolder<BannerResponse> {
    override fun getLayoutId(): Int = R.layout.banner_itemhome

    override fun onBind(itemView: View, data: BannerResponse?, position: Int, size: Int) {
        val img = itemView.findViewById<ImageView>(R.id.banner_img)
        data?.let {
            Glide.with(img.context.applicationContext)
                .load(it.imagePath)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(img)
        }
    }
}