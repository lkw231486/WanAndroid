package com.example.wanandroid.app.event

import android.app.Application
import com.example.wanandroid.app.utils.CacheUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.data.model.bean.UserInfo
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.UnPeekLiveData
import me.hgj.jetpackmvvm.callback.livedata.UnPeekNotNullLiveData

/**
 *@ author: lkw
 *created on:2020/7/2 14:52
 *description: APP全局的Viewmodel，可以存放公共数据，当他数据改变时，所有监听他的地方都会收到回调,也可以做发送消息 ,比如 全局可使用的 地理位置信息，账户信息,App的基本配置等等，
 *email:lkw@mantoo.com.cn
 */
class AppViewModel(app: Application) : BaseViewModel(app) {

    //是否第一次启动
    var isFirst =
        UnPeekNotNullLiveData<Boolean>()

    //是否已经登录过
    var isLogin =
        UnPeekNotNullLiveData<Boolean>()

    //App的账户信息 使用UnPeekLiveData 因为账户信息有可能为空
    var userInfo =
        UnPeekLiveData<UserInfo>()

    //App主题颜色 中大型项目不推荐以这种方式改变主题颜色，比较繁琐耦合，且容易有遗漏某些控件没有设置主题色
    var appColor =
        UnPeekNotNullLiveData<Int>()

    //App 列表动画
    var appAnimation =
        UnPeekNotNullLiveData<Int>()

    //kotlin初始化静态资源
    init {
        //初始化
        isFirst.value=CacheUtil.isFirst()
        isLogin.value=CacheUtil.isLogin()
        //默认值保存的账户信息，没有登陆过则为null
        userInfo.value=CacheUtil.getUser()
        //默认值颜色
         appColor.value=SettingUtil.getColor(getApplication())
        //初始化列表动画
        appAnimation.value=SettingUtil.getListMode()
    }
}