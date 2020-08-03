package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.wanandroid.data.model.bean.UserInfo
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

/**
 *@ author: lkw
 *created on:2020/7/7 11:02
 *description: 登录注册的请求ViewModel
 *email:lkw@mantoo.com.cn
 */
class RequestLoginRegisterViewModel(application: Application) : BaseViewModel(application) {
    //方式1  自动脱壳过滤处理请求结果，判断结果是否成功
    var loginResult = MutableLiveData<ResultState<UserInfo>>()

    //方式2  不用框架帮脱壳，判断结果是否成功
    //    var loginResult2 = MutableLiveData<ResultState<ApiResponse<UserInfo>>>()

    fun loginReq(username: String, passowrd: String) {
        //1.这种是在 Activity/Fragment的监听回调中拿到已脱壳的数据（项目有基类的可以用）
        request(
            {
                HttpRequestManger.instance.login(username, passowrd)
            }//请求体
            , loginResult, //请求的返回结果，请求成功与否都会改变该值，在Activity或fragment中监听回调结果，具体可看loginActivity中的回调
            true, //是否显示等待框，，默认false不显示 可以默认不传
            "正在登录中..."
        )//等待框内容，可以默认不填请求网络中...
    }

    fun registerAndLogin(username: String, passowrd: String, repassword: String) {
        request(
            {
                HttpRequestManger.instance.register(username, passowrd, repassword)
            }, loginResult,
            true,
            "正在注册..."
        )
    }


}