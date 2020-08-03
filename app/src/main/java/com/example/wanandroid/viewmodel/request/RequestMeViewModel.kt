package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.wanandroid.data.model.bean.IntegralResponse
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

/**
 *@ author: lkw
 *created on:2020/7/9 13:33
 *description: 我的页面viewModel
 *email:lkw@mantoo.com.cn
 */
class RequestMeViewModel(application: Application):BaseViewModel(application){

    var meData=MutableLiveData<ResultState<IntegralResponse>>()

    fun getUserInfo(){
      request({HttpRequestManger.instance.getUserInfo()},meData)
    }
}