package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.wanandroid.app.network.stateCallback.ListDataUiState
import com.example.wanandroid.data.model.bean.IntegralHistoryResponse
import com.example.wanandroid.data.model.bean.IntegralResponse
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request

/**
 *@ author: lkw
 *created on:2020/7/20 13:39
 *description:积分排行请求接口
 *email:lkw@mantoo.com.cn
 */
class RequestIntegralViewModel(application: Application) : BaseViewModel(application) {
    private var pageNo = 1

    //积分排行数据
    var integralDataState = MutableLiveData<ListDataUiState<IntegralResponse>>()

    //获取积分历史数据
    var integralHistoryDataUiState = MutableLiveData<ListDataUiState<IntegralHistoryResponse>>()

    /**
     * 分页查询积分数据列表
     */
    fun getIntegralData(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 0
        }
        request({ HttpRequestManger.instance.getIntegralData(pageNo) }, {
            //请求成功
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.isEmpty(),
                hasMore = it.hasMore(),
                isFirstEmpty = it.isRefresh() && it.isEmpty(),
                listDate = it.datas
            )
            integralDataState.postValue(listDataUiState)
        }, {
            //请求失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                errorMsg = it.errorMsg,
                isRefresh = isRefresh,
                listDate = arrayListOf<IntegralResponse>()
            )
            integralDataState.postValue(listDataUiState)
        })
    }

    /**
     * 分页查询历史积分
     */
    fun getIntegralHistoryData(isRefresh: Boolean) {

        if (isRefresh) {
            pageNo = 0
        }

        request({ HttpRequestManger.instance.getIntegralHistroyData(pageNo) }, {
            //请求成功
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.isEmpty(),
                isFirstEmpty = isRefresh && it.isEmpty(),
                hasMore = it.hasMore(),
                listDate = it.datas
            )
            integralHistoryDataUiState.postValue(listDataUiState)
        }, {
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                isRefresh = isRefresh,
                errorMsg = it.errorMsg,
                listDate = arrayListOf<IntegralHistoryResponse>()
            )
            integralHistoryDataUiState.postValue(listDataUiState)
        })
    }

}