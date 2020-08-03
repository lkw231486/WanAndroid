package com.example.wanandroid.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

/**
 *@ author: lkw
 *created on:2020/7/24 13:16
 *description:我的文章viewModel
 *email:lkw@mantoo.com.cn
 */
class AriticleViewModel(application: Application) : BaseViewModel(application) {

    //分享文章标题
    var shareTitle = StringObservableField()

    //分享文章链接
    var shareUrl = StringObservableField()

    //分享文章的人
    var shareName = StringObservableField()
}