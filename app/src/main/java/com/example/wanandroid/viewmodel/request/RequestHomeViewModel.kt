package com.example.wanandroid.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.wanandroid.app.App
import com.example.wanandroid.app.network.stateCallback.ListDataUiState
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.BannerResponse
import com.example.wanandroid.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

/**
 *@ author: lkw
 *created on:2020/7/6 17:15
 *description: 首页数据请求viewModel
 *描述　: 有两种回调方式：
 * 1.首页文章列表 将返回的数据放在Viewmodel中过滤包装给activity/fragment去使用
 * 2.首页轮播图 将返回的数据直接给activity/fragment去处理使用
 * 可以根据个人理解与喜好使用
 *email:lkw@mantoo.com.cn
 */
class RequestHomeViewModel(application: Application) : BaseViewModel(application) {

    //页码，首页数据从零开始
    var pageNo: Int = 0

    //首页文章列表数据
    var homeDataState: MutableLiveData<ListDataUiState<AriticleResponse>> = MutableLiveData()

    //首页轮播图数据
    var bannerData: MutableLiveData<ResultState<ArrayList<BannerResponse>>> = MutableLiveData()

    fun getHomeDate(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 0
            getApplication<App>()
        }
        request({ HttpRequestManger.instance.getHomeData(pageNo) }, {
            //请求成功
            pageNo++
            val listDataUiState = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.isEmpty(),
                hasMore = it.hasMore(),
                isFirstEmpty = isRefresh && it.isEmpty(),
                listDate = it.datas
            )
            homeDataState.postValue(listDataUiState)
        }, {
            //请求失败
            val listDataUiState = ListDataUiState(
                isSuccess = false,
                errorMsg = it.errorMsg, isRefresh = isRefresh,
                listDate = arrayListOf<AriticleResponse>()
            )
            homeDataState.postValue(listDataUiState)
        })
    }

    /**
     * 获取轮播图数据
     */
    fun getBannerData() {
        request({ HttpRequestManger.instance.getBannerData() }, bannerData)
    }
}