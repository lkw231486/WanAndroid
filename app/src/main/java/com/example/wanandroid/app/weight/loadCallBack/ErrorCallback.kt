package com.example.wanandroid.app.weight.loadCallBack

import com.example.wanandroid.R
import com.kingja.loadsir.callback.Callback


/**
 *@ author: lkw
 *created on:2020/7/1 16:05
 *description:
 *email:lkw@mantoo.com.cn
 */
class ErrorCallback : Callback(){
    override fun onCreateView(): Int {
        return R.layout.layout_error
    }

}