package com.example.wanandroid.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 *@ author: lkw
 *created on:2020/7/8 13:34
 *description: webView的model
 *email:lkw@mantoo.com.cn
 */
class WebViewModel(application: Application) : BaseViewModel(application) {
    //是否收藏
    var collect = false

    //收藏的id
    var articleId = 0

    //标题
    var showTitle: String = ""

    //文章的网络访问路径
    var url: String = ""

    //需要收藏的类型 具体参数说明请看collectType枚举类
    var collectType = 0
}