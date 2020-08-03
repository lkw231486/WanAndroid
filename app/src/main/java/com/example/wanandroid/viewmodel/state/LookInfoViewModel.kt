package com.example.wanandroid.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData

/**
 *@ author: lkw
 *created on:2020/7/8 17:13
 *description:
 *email:lkw@mantoo.com.cn
 */
class LookInfoViewModel(application: Application) : BaseViewModel(application) {

    var name: StringLiveData = StringLiveData("--")

    var imageUrl: StringLiveData =
        StringLiveData("https://upload.jianshu.io/users/upload_avatars/9305757/93322613-ff1a-445c-80f9-57f088f7c1b1.jpg?imageMogr2/auto-orient/strip|imageView2/1/w/300/h/300/format/webp")

    var info: StringLiveData = StringLiveData()
}