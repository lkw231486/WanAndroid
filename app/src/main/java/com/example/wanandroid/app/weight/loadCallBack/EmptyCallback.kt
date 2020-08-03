package com.example.wanandroid.app.weight.loadCallBack

import com.example.wanandroid.R
import com.kingja.loadsir.callback.Callback


/**
 *@ author: lkw
 *created on:2020/7/1 15:58
 *description: 空布局显示
 *email:lkw@mantoo.com.cn
 */
class EmptyCallback : Callback(){
    override fun onCreateView(): Int {
        return R.layout.layout_empty
    }

}