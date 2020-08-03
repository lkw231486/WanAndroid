package com.example.wanandroid.app.weight.banner

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wanandroid.R
import com.zhpan.bannerview.holder.ViewHolder

/**
 *@ author: lkw
 *created on:2020/7/2 17:44
 *description:
 *email:lkw@mantoo.com.cn
 */
class WelcomeBannerViewHolder: ViewHolder<String>{
    override fun getLayoutId(): Int {
        return R.layout.banner_itemwelcome
    }

    override fun onBind(itemView: View, data: String?, position: Int, size: Int) {
      val textView=itemView.findViewById<TextView>(R.id.banner_text)
        textView.text=data
    }

}