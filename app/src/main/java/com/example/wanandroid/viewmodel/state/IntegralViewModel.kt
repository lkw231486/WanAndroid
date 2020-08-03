package com.example.wanandroid.viewmodel.state

import android.app.Application
import androidx.databinding.ObservableField
import com.example.wanandroid.data.model.bean.IntegralResponse
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 *@ author: lkw
 *created on:2020/7/20 11:14
 *description: 积分viewModel
 *email:lkw@mantoo.com.cn
 */
class IntegralViewModel(application: Application) : BaseViewModel(application) {

    val rank = ObservableField<IntegralResponse>()
}