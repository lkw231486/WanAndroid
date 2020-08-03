package com.example.wanandroid.app.network

import com.example.wanandroid.data.model.bean.*
import retrofit2.http.*

/**
 *@ author: lkw
 *created on:2020/7/6 16:28
 *description: 网络的api
 *email:lkw@mantoo.com.cn
 */
interface ApiService {

    //玩转安卓网络请求地址
    companion object {
        const val SERVER_URL = "https://wanandroid.com/"
    }


    /**
     * 登录接口
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<UserInfo>

    /**
     * 注册账号接口
     */
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): ApiResponse<Any>


    /**
     * 获取首页文章列表接口
     */
    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>>

    /**
     * 获取头部文章接口
     */
    @GET("article/top/json")
    suspend fun getTopArticleList(): ApiResponse<ArrayList<AriticleResponse>>

    /**
     *获取banner数据
     */
    @GET("banner/json")
    suspend fun getBanner(): ApiResponse<ArrayList<BannerResponse>>

    /**
     * 收藏的文章
     */
    @POST("lg/collect/{id}/json")
    suspend fun collect(@Path("id") id: Int): ApiResponse<Any?>

    /**
     * 取消收藏
     */
    @POST("lg/uncollect_originId/{id}/json")
    suspend fun unCollect(@Path("id") id: Int): ApiResponse<Any?>

    /**
     * 收藏网址
     */
    @POST("lg/collect/addtool/json")
    suspend fun collectUrl(
        @Query("name") name: String,
        @Query("link") link: String
    ): ApiResponse<CollectUrlResponse>

    /**
     * 取消收藏
     */
    @POST("lg/collect/deletetool/json")
    suspend fun deleteCollect(@Query("id") id: Int): ApiResponse<Any?>

    /**
     * 获取作者信息以及作者文章
     */
    @GET("user/{id}/share_articles/{page}/json")
    suspend fun getShareDataById(
        @Path("page") page: Int,
        @Path("id") id: Int
    ): ApiResponse<ShareResponse>

    /**
     * 获取当前账号信息接口
     */
    @GET("lg/coin/userinfo/json")
    suspend fun getUserInfo(): ApiResponse<IntegralResponse>

    /**
     * 获取收藏的文章
     */
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectData(@Path("page") pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<CollectResponse>>>

    /**
     *获取收藏网址接口
     */
    @GET("lg/collect/usertools/json")
    suspend fun getCollectUrlData(): ApiResponse<ArrayList<CollectUrlResponse>>

    /**
     * 获取积分排行榜
     */
    @GET("coin/rank/{page}/json")
    suspend fun getIntegalRank(@Path("page") page: Int): ApiResponse<ApiPagerResponse<ArrayList<IntegralResponse>>>

    /**
     * 历史积分查询
     */
    @GET("lg/coin/list/{page}/json")
    suspend fun getIntegralHistory(@Path("page") page: Int): ApiResponse<ApiPagerResponse<ArrayList<IntegralHistoryResponse>>>

    /**
     * 获取热门搜索数据
     */
    @GET("hotkey/json")
    suspend fun getHotSearchData(): ApiResponse<ArrayList<SearchResponse>>

    /**
     * 根据字符串搜索文章
     */
    @POST("article/query/{page}/json")
    suspend fun getSearchDataByKey(
        @Path("page") pageNo: Int,
        @Query("k") searchKey: String
    ): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>>

    /**
     *项目分类标题
     */
    @GET("project/tree/json")
    suspend fun getProjectTitle(): ApiResponse<ArrayList<ClassifyResponse>>

    /**
     * 获取最新项目接口
     */
    @GET("article/listproject/{page}/json")
    suspend fun getProjectNewDate(@Path("page") pageNo: Int): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>>

    /**
     * 根据分类id获取项目
     */
    @GET("project/list/{page}/json")
    suspend fun getProjectDataByType(
        @Path("page") pageNo: Int,
        @Query("cid") cid: Int
    ): ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>>

    /**
     * 获取自己分享文章列表
     */
    @GET("user/lg/private_articles/{page}/json")
    suspend fun getShareData(@Path("page") pageNo: Int): ApiResponse<ShareResponse>

    /**
     *删除自己分享的文章
     */
    @POST("lg/user_article/delete/{id}/json")
    suspend fun deleteShareDate(@Path("id") id: Int): ApiResponse<Any>

    /**
     * 添加分享文章
     */
    @POST("lg/user_article/add/json")
    @FormUrlEncoded
    suspend fun addArticle(
        @Field("title") title: String,
        @Field("link") content: String
    ): ApiResponse<Any?>

    /**
     * 获取广场列表数据
     */
    @GET("user_article/list/{page}/json")
    suspend fun getSqureData(@Path("page") page: Int):ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>>

    /**
     * 每日一问
     */
    @GET("wenda/list/{page}/json")
    suspend fun getAskData(@Path("page") page: Int):ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>>

    /**
     * 获取体系数据
     */
    @GET("tree/json")
    suspend fun getSystemData():ApiResponse<ArrayList<SystemResponse>>

    /**
     * 获取体系列表
     */
    @GET("navi/json")
    suspend fun getNavigationData():ApiResponse<ArrayList<NavigationResponse>>

    /**
     * 获取体系子列表数据
     */
    @GET("article/list/{page}/json")
    suspend fun getSystemChildData(@Path("page")pageNo: Int,@Query("cid") cid: Int):ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>>

    /**
     *公众号分类
     */
    @GET("wxarticle/chapters/json")
    suspend fun getPublicTitle():ApiResponse<ArrayList<ClassifyResponse>>

    /**
     * 获取公众号数据
     */
    @GET("wxarticle/list/{id}/{page}/json")
    suspend fun getPublicData(@Path("page") pageNo: Int,@Path("id") id: Int):ApiResponse<ApiPagerResponse<ArrayList<AriticleResponse>>>
}