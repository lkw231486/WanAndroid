package com.example.wanandroid.data.model.bean

import me.hgj.jetpackmvvm.network.BaseResponse

/**
 *@ author: lkw
 *created on:2020/7/6 17:27
 *description: 服务器返回数据的基类
 * 如果你的项目中有基类，那美滋滋，可以继承BaseResponse，请求时框架可以帮你自动脱壳，自动判断是否请求成功，怎么做：
 * 1.继承 BaseResponse
 * 2.重写isSucces 方法，编写你的业务需求，根据自己的条件判断数据是否请求成功
 * 3.重写 getResponseCode、getResponseData、getResponseMsg方法，传入你的 code data msg
 *email:lkw@mantoo.com.cn
 */
data class ApiResponse<T>(var errorCode: Int, var errorMsg: String, var data: T) :
    BaseResponse<T>() {
    override fun isSucces() = errorCode == 0

    override fun getResponseData() = data

    override fun getResponseCode() = errorCode

    override fun getResponseMsg() = errorMsg

}