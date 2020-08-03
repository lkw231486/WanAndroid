package com.example.wanandroid.data.repository.request

import android.net.Network
import com.example.wanandroid.app.network.NetWorkApi
import com.example.wanandroid.app.utils.CacheUtil
import com.example.wanandroid.data.model.bean.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.network.AppException

/**
 *@ author: lkw
 *created on:2020/7/6 17:24
 *description: 从网络中获取数据
 *email:lkw@mantoo.com.cn
 */
class HttpRequestManger {
    companion object {
        val instance: HttpRequestManger by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpRequestManger()
        }
    }

    /**
     * 登录
     */
    suspend fun login(username: String, password: String): ApiResponse<UserInfo> {
        return NetWorkApi.service.login(username, password)
    }

    /**
     * 账号注册
     */
    suspend fun register(
        username: String,
        password: String,
        repassword: String
    ): ApiResponse<UserInfo> {
        val registerDate = NetWorkApi.service.register(username, password, repassword)
        if (registerDate.isSucces()) {
            return login(username, password)
        } else {
            //抛出错误异常
            throw AppException(registerDate.errorCode, registerDate.errorMsg)
        }
    }

    /**
     *收藏文章
     */
    suspend fun collect(id: Int): ApiResponse<Any?> {
        return NetWorkApi.service.collect(id)
    }

    /**
     * 取消收藏
     */
    suspend fun unCollect(id: Int): ApiResponse<Any?> {
        return NetWorkApi.service.unCollect(id)
    }

    /**
     * 收藏网址
     */
    suspend fun collectUrl(name: String, link: String): ApiResponse<CollectUrlResponse> {
        return NetWorkApi.service.collectUrl(name, link)
    }

    /**
     * 取消网址收藏
     */
    suspend fun unCollectUrl(id: Int): ApiResponse<Any?> {
        return NetWorkApi.service.deleteCollect(id)
    }

    /**
     * 获取首页文章
     */
    suspend fun getHomeData(pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>> {
        //同时异步请求2个接口，请求完成后合并数据
        return withContext(Dispatchers.IO) {
            val data = async { NetWorkApi.service.getArticleList(pageNo) }
            //如果App配置打开了首页请求置顶文章，且是第一页
            if (CacheUtil.isNeedTop() && pageNo == 0) {
                val topDate = async { getTopData() }
                data.await().data.datas.addAll(0, topDate.await().data)
                data.await()
            } else {
                data.await()
            }
        }
    }

    /**
     * 获取顶部文章
     */
    private suspend fun getTopData(): ApiResponse<ArrayList<AriticleResponse>> {
        return NetWorkApi.service.getTopArticleList()
    }


    /**
     * 获取轮播图
     */
    suspend fun getBannerData(): ApiResponse<ArrayList<BannerResponse>> {
        return NetWorkApi.service.getBanner()
    }

    /**
     *获取作者信息
     */
    suspend fun getLookInfoById(id: Int, pageNo: Int): ApiResponse<ShareResponse> {
        return NetWorkApi.service.getShareDataById(pageNo, id)
    }

    /**
     * 获取当前用户积分
     */
    suspend fun getUserInfo(): ApiResponse<IntegralResponse> {
        return NetWorkApi.service.getUserInfo()
    }

    /**
     * 获取收藏文章数据
     */
    suspend fun collectArticleData(pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<CollectResponse>>> {
        return NetWorkApi.service.getCollectData(pageNo)
    }

    /**
     * 获取收藏网址接口
     */
    suspend fun collectUrlData(): ApiResponse<ArrayList<CollectUrlResponse>> {
        return NetWorkApi.service.getCollectUrlData()
    }

    /**
     * 获取积分列表
     */
    suspend fun getIntegralData(pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<IntegralResponse>>> {
        return NetWorkApi.service.getIntegalRank(pageNo)
    }

    /**
     * 获取历史积分
     */
    suspend fun getIntegralHistroyData(pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<IntegralHistoryResponse>>> {
        return NetWorkApi.service.getIntegralHistory(pageNo)
    }

    /**
     * 获取热门搜索数据
     */
    suspend fun getHotData(): ApiResponse<ArrayList<SearchResponse>> {
        return NetWorkApi.service.getHotSearchData()
    }

    /**
     *根据关键词搜索数据
     */
    suspend fun getSearchResultData(
        pageNo: Int,
        searchKey: String
    ): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>> {
        return NetWorkApi.service.getSearchDataByKey(pageNo, searchKey)
    }

    /**
     * 获取项目分类
     */
    suspend fun getProjectTitle(): ApiResponse<ArrayList<ClassifyResponse>> {
        return NetWorkApi.service.getProjectTitle()
    }

    /**
     * 根据项目id获取项目列表
     */
    suspend fun getProjectData(
        pageNo: Int,
        cid: Int,
        isNew: Boolean
    ): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>> {
        if (isNew) {
            return NetWorkApi.service.getProjectNewDate(pageNo)
        } else {
            return NetWorkApi.service.getProjectDataByType(pageNo, cid)
        }
    }


    /**
     * 添加文章
     */
    suspend fun addArticle(title: String, content: String): ApiResponse<Any?> {
        return NetWorkApi.service.addArticle(title, content)
    }

    /**
     * 获取自己分享文章
     */
    suspend fun getShareArticle(pageNo: Int): ApiResponse<ShareResponse> {
        return NetWorkApi.service.getShareData(pageNo)
    }

    /**
     * 删除分享文章
     */
    suspend fun delShareArticle(id: Int): ApiResponse<Any> {
        return NetWorkApi.service.deleteShareDate(id)
    }

    /**
     * 获取广场数据列表
     */
    suspend fun getPlazaData(pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>> {
        return NetWorkApi.service.getSqureData(pageNo)
    }

    /**
     * 获取每日一问数据列表
     */
    suspend fun getAskData(pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>> {
        return NetWorkApi.service.getAskData(pageNo)
    }

    /**
     * 获取体系栏目列表
     */
    suspend fun getNavigationData(): ApiResponse<ArrayList<NavigationResponse>> {
        return NetWorkApi.service.getNavigationData()
    }

    /**
     * 获取体系数据
     */
    suspend fun getSystemData(): ApiResponse<ArrayList<SystemResponse>> {
        return NetWorkApi.service.getSystemData()
    }

    /**
     * 获取体系子数据
     */
    suspend fun getSystemChildData(
        pageNo: Int,
        cid: Int
    ): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>> {
        return NetWorkApi.service.getSystemChildData(pageNo, cid)
    }

    /**
     * 获取公众号标题
     */
    suspend fun getPublicTitle():ApiResponse<ArrayList<ClassifyResponse>>{
        return NetWorkApi.service.getPublicTitle()
    }

    /**
     * 获取公众号数据
     */
    suspend fun getPublicData(pageNo: Int,id: Int):ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>>{
     return NetWorkApi.service.getPublicData(pageNo,id)
    }
}