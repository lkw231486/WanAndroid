package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.wanandroid.app.network.stateCallback.ListDataUiState
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.NavigationResponse
import com.example.wanandroid.data.model.bean.SystemResponse
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request

/**
 *@ author: lkw
 *created on:2020/7/27 16:24
 *description: 广场页面请求
 *email:lkw@mantoo.com.cn
 */
class RequestTreeViewModel(application: Application) : BaseViewModel(application) {

    //页码，体系 广场的页码是从0开始的
    private var pageNo = 0

    //广场数据
    var plazaDataState: MutableLiveData<ListDataUiState<AriticleResponse>> = MutableLiveData()

    //每日一问数据
    var askDataState: MutableLiveData<ListDataUiState<AriticleResponse>> = MutableLiveData()

    //体系子栏目列表数据
    var systemChileDataState: MutableLiveData<ListDataUiState<AriticleResponse>> = MutableLiveData()

    //体系数据
    var systemDataState: MutableLiveData<ListDataUiState<SystemResponse>> = MutableLiveData()

    //导航数据
    var nagivationDataState: MutableLiveData<ListDataUiState<NavigationResponse>> =
        MutableLiveData()

    /**
     * 获得广场数据
     */
    fun getPlazaData(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 0
        }
        request({ HttpRequestManger.instance.getPlazaData(pageNo) }, {
            //成功
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.isEmpty(),
                hasMore = it.hasMore(),
                isFirstEmpty = it.isRefresh() && it.isEmpty(),
                listDate = it.datas
            )
            plazaDataState.postValue(listDataUiState)
        }, {
            //失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                errorMsg = it.errorMsg,
                isRefresh = isRefresh,
                listDate = arrayListOf<AriticleResponse>()
            )
            plazaDataState.postValue(listDataUiState)
        })
    }

    /**
     * 获取每日一问数据
     */
    fun getAskData(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo == 0
        }
        request({ HttpRequestManger.instance.getAskData(pageNo) }, {
            //成功回调
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.isEmpty(),
                isFirstEmpty = it.isRefresh() && it.isEmpty(),
                hasMore = it.hasMore(),
                listDate = it.datas
            )
            askDataState.postValue(listDataUiState)
        }, {
            //失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                isRefresh = isRefresh,
                errorMsg = it.errorMsg,
                listDate = arrayListOf<AriticleResponse>()

            )
            askDataState.postValue(listDataUiState)
        })
    }

    /**
     * 获得体系数据
     */
    fun getSystemData() {
        request({ HttpRequestManger.instance.getSystemData() }, {
            //成功
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                listDate = it
            )
            systemDataState.postValue(listDataUiState)
        }, {
            //失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                errorMsg = it.errorMsg,
                listDate = arrayListOf<SystemResponse>()
            )
            systemDataState.postValue(listDataUiState)
        })
    }

    /**
     * 获取导航数据
     */
    fun getNavigationData() {
        request({ HttpRequestManger.instance.getNavigationData() }, {
            //成功
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                listDate = it
            )
            nagivationDataState.postValue(listDataUiState)
        }, {
            //失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                errorMsg = it.errorMsg,
                listDate = arrayListOf<NavigationResponse>()
            )
            nagivationDataState.postValue(listDataUiState)
        })
    }

    /**
     * 获取体系子栏目数据
     */
    fun getSystemChildData(isRefresh: Boolean, cid: Int) {
        if (isRefresh) {
            pageNo == 0
        }
        request({ HttpRequestManger.instance.getSystemChildData(pageNo, cid) }, {
            //成功
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.isEmpty(),
                hasMore = it.hasMore(),
                isFirstEmpty = it.isEmpty() && it.isRefresh(),
                listDate = it.datas
            )
            systemChileDataState.postValue(listDataUiState)
        }, {
            //失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                isRefresh = isRefresh,
                errorMsg = it.errorMsg,
                listDate = arrayListOf<AriticleResponse>()
            )
            systemChileDataState.postValue(listDataUiState)
        })
    }
}