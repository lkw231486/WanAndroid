package com.example.wanandroid.app.weight.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.example.wanandroid.R
import com.example.wanandroid.app.utils.SettingUtil

/**
 *@ author: lkw
 *created on:2020/7/16 17:31
 *description:
 *email:lkw@mantoo.com.cn
 */
class IconPreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {
    var circleImageView: MyColorCircleView? = null

    init {
        widgetLayoutResource = R.layout.item_icon_preference_preview
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        var color = SettingUtil.getColor(context)
        circleImageView = holder?.itemView?.findViewById(R.id.iv_preview)
        circleImageView?.color = color
        circleImageView?.border = color
    }

    fun setView() {
        val color = SettingUtil.getColor(context)
        circleImageView?.color = color
        circleImageView?.border = color
    }
}