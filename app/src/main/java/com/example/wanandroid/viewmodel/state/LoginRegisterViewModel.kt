package com.example.wanandroid.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData

/**
 *@ author: lkw
 *created on:2020/7/6 17:56
 *description: 登录注册的ViewModel,登录注册共用一个ViewModel
 *email:lkw@mantoo.com.cn
 */
class LoginRegisterViewModel(application: Application):BaseViewModel(application) {

    //用户名
    var username=StringLiveData()

    //密码（登录注册界面）
    var password=StringLiveData()

    //确认密码
    var password2=StringLiveData()

    //是否显示铭文密码
    var isShowPwd=BooleanLiveData()

    //是否显示确定铭文密码
    var isShowPwd2=BooleanLiveData()
}