package com.example.wanandroid.app.weight.loadCallBack

import com.example.wanandroid.R
import com.kingja.loadsir.callback.Callback


/**
 *@ author: lkw
 *created on:2020/7/1 15:52
 *description: 显示加载布局
 *email:lkw@mantoo.com.cn
 */
class LoadingCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_loading
    }
}