package com.example.wanandroid.viewmodel.state

import android.app.Application
import androidx.core.graphics.ColorUtils
import com.example.wanandroid.app.utils.ColorUtil
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.IntLiveData
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData

/**
 *@ author: lkw
 *created on:2020/7/9 10:48
 *description:我的页面viewModel
 *email:lkw@mantoo.com.cn
 */
class MeViewModel(application: Application) : BaseViewModel(application) {

    val name=StringLiveData("请先登录")

    val integral=IntLiveData(0)

    var info=StringLiveData("id：-- 排名：-")

    var imageUrl=StringLiveData(ColorUtil.randomImage())
}