package com.example.wanandroid.app.network.stateCallback

/**
 *@ author: lkw
 *created on:2020/7/24 13:51
 *description: 操作数据的状态类
 *email:lkw@mantoo.com.cn
 */
class UpdateUiState<T>(
    //请求是否成功
    var isSuccess: Boolean = true,
    //操作对象
    var data: T,
    //请求失败信息
    var errorMsg: String = ""
)

