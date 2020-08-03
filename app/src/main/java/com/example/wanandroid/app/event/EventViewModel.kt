package com.example.wanandroid.app.event

import android.app.Application
import com.example.wanandroid.data.model.bean.CollectBus
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.UnPeekLiveData

/**
 *@ author: lkw
 *created on:2020/7/2 15:58
 *description: APP全局的Viewmodel，可以在这里发送全局通知替代Eventbus，LiveDataBus等
 *email:lkw@mantoo.com.cn
 */
class EventViewModel(app: Application) : BaseViewModel(app) {

    //全局收藏，在任意一个地方收藏或取消收藏，监听该值的界面都会收到消息
    var collect =
        UnPeekLiveData<CollectBus>()

    //分享文章通知
    var shareArticle =
        UnPeekLiveData<Boolean>()

    //添加toto通知
    var addTodo =
        UnPeekLiveData<Boolean>()
}