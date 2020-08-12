package com.example.wanandroid.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.app.weight.banner.HomeBannerViewHolder
import com.example.wanandroid.app.weight.customview.CollectView
import com.example.wanandroid.app.weight.loadCallBack.EmptyCallback
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.app.weight.recyclerview.DefineLoadMoreView
import com.example.wanandroid.app.weight.recyclerview.SpaceItemDecoration
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.BannerResponse
import com.example.wanandroid.data.model.bean.CollectBus
import com.example.wanandroid.databinding.FragmentHomeBinding
import com.example.wanandroid.ui.adapter.ArticleAdapter
import com.example.wanandroid.viewmodel.request.RequestCollectViewModel
import com.example.wanandroid.viewmodel.request.RequestHomeViewModel
import com.example.wanandroid.viewmodel.state.HomeViewModel
import com.kingja.loadsir.core.LoadService
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState
import me.hgj.jetpackmvvm.ext.util.toHtml

/**
 *@ author: lkw
 *created on:2020/7/3 17:25
 *description: 首页
 *email:lkw@mantoo.com.cn
 */
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {


    //页面适配器
    private val articleAdapter: ArticleAdapter by lazy { ArticleAdapter(arrayListOf(), true) }

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>

    //recyclerview的底部加载view 因为在首页要动态改变它的颜色，所以加了这个字段
    private lateinit var footView: DefineLoadMoreView

    //收藏viewmodel  注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的
    private val requestCollectViewModel: RequestCollectViewModel by lazy { getViewModel<RequestCollectViewModel>() }

    //请求数据ViewModel 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的
    private val requestHomeViewModel: RequestHomeViewModel by lazy { getViewModel<RequestHomeViewModel>() }
    override fun layoutId(): Int = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        //状态页配置
        loadsir = LoadServiceInit(swipeRefresh) {
            //点击从试时触发操作
            loadsir.showCallback(LoadingCallback::class.java)
            requestHomeViewModel.getBannerData()
            requestHomeViewModel.getHomeDate(true)
        }
        toolbar?.run {
            init("首页")
            inflateMenu(R.menu.home_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.home_search -> {
                        nav().navigate(R.id.action_mainFragment_to_searchFragment)
                    }
                }
                true
            }
        }
        //初始化recycleview
        recyclerView.init(LinearLayoutManager(context), articleAdapter).let {
            //因为首页要添加轮播图，所以我设置了firstNeedTop字段为false,即第一条数据不需要设置间距
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f), false))
            footView = it.initFooter(SwipeRecyclerView.LoadMoreListener {
                requestHomeViewModel.getHomeDate(false)
            })
            //初始化 FloatingActionButton
            it.initFloatBtn(floatbtn)
        }
        //初始化swipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求数据
            requestHomeViewModel.getBannerData()
            requestHomeViewModel.getHomeDate(true)
        }
        articleAdapter.run {
            setOnCollectViewClickListener(object : ArticleAdapter.OnCollectViewClickListener {
                override fun onClick(item: AriticleResponse, v: CollectView, position: Int) {
                    if (shareViewModel.isLogin.value) {
                        if (v.isChecked) {
                            requestCollectViewModel.unCollect(item.id)
                        } else {
                            requestCollectViewModel.collect(item.id)
                        }
                    } else {
                        v.isChecked = true
                        nav().navigate(R.id.action_mainFragment_to_loginFragment)
                    }
                }
            })
            setNbOnItemClickListener { adapter, view, position ->
                nav().navigate(R.id.action_mainFragment_to_webFragment, Bundle().apply {
                    putParcelable(
                        "articleData",
                        articleAdapter.data[position - recyclerView.headerCount]
                    )
                })
            }
            addChildClickViewIds(R.id.item_home_author, R.id.item_project_author)
            setNbOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.item_home_author, R.id.item_project_author -> {
                        nav().navigate(
                            R.id.action_mainFragment_to_lookInfoFragment,
                            Bundle().apply {
                                putInt(
                                    "id",
                                    articleAdapter.data[position - recyclerView.headerCount].userId
                                )
                            }
                        )
                    }
                }
            }

        }
    }

    /**
     * 懒加载
     */
    override fun lazyLoadData() {
        //设置界面加载中
        loadsir.showCallback(LoadingCallback::class.java)
        //请求轮播图数据
        requestHomeViewModel.getBannerData()

        //请求文章列表数据
        requestHomeViewModel.getHomeDate(false)
    }

    override fun createObserver() {
        requestHomeViewModel.run {
            //监听首页文章列表请求的数据变化
            homeDataState.observe(viewLifecycleOwner, Observer {
                swipeRefresh.isRefreshing = false
                recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
                if (it.isSuccess) {
                    //成功
                    when {
                        //第一页没有数据 显示空布局界面
                        it.isFirstEmpty -> {
                            loadsir.showCallback(EmptyCallback::class.java)
                        }
                        //是第一页
                        it.isRefresh -> {
                            loadsir.showSuccess()
                            articleAdapter.setNewInstance(it.listDate)
                        }
                        //不是第一页
                        else -> {
                            loadsir.showSuccess()
                            articleAdapter.addData(it.listDate)
                        }
                    }
                } else {
                    //失败
                    if (it.isRefresh) {
                        //如果是第一页，则显示错误界面，并提示错误信息
                        loadsir.setErrorMsg(it.errorMsg)
                        loadsir.showCallback(ErrorCallback::class.java)
                    } else {
                        recyclerView.loadMoreError(0, it.errorMsg)
                    }
                }
            })
            //监听轮播图请求的数据变化
            bannerData.observe(viewLifecycleOwner, Observer { resultState ->
                parseState(resultState, { data ->
                    //请求轮播图数据成功，添加轮播图到headview ，如果等于0说明没有添加过头部，添加一个
                    if (recyclerView.headerCount == 0) {
                        val headView =
                            LayoutInflater.from(context).inflate(R.layout.include_banner, null)
                                .apply {
                                    val bannerView =
                                        findViewById<BannerViewPager<BannerResponse, HomeBannerViewHolder>>(
                                            R.id.banner_view
                                        )
                                    bannerView.setHolderCreator {
                                        HomeBannerViewHolder()
                                    }.setOnPageClickListener {
                                        nav().navigate(R.id.action_mainFragment_to_webFragment,
                                            Bundle().apply {
                                                putParcelable("bannerData", data[it])
                                            }
                                        )
                                    }.create(data.toList())
                                }
                        recyclerView.addHeaderView(headView)
                    }
                }, {
                    //这里是请求banner数据失败 失败了就不管他了。随他去吧
                })
            })
        }

        requestCollectViewModel.collectUiState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                //收藏或取消收藏操作成功，发送全局收藏消息
                eventViewModel.collect.postValue(CollectBus(it.id, it.collect))
            } else {
                showMessage(it.errorMsg)
                for (index in articleAdapter.data.indices) {
                    if (articleAdapter.data[index].id == it.id) {
                        articleAdapter.data[index].collect = it.collect
                        articleAdapter.notifyItemChanged(index)
                        break
                    }
                }
            }
        })

        shareViewModel.run {
            //监听账户信息是否改变 有值时(登录)将相关的数据设置为已收藏，为空时(退出登录)，将已收藏的数据变为未收藏
            userInfo.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    it.collectIds.forEach { id ->
                        for (item in articleAdapter.data)
                            if (id.toInt() == item.id) {
                                item.collect = true
                                break
                            }
                    }
                } else {
                    for (item in articleAdapter.data) {
                        item.collect = false
                    }
                }
                articleAdapter.notifyDataSetChanged()
            })
            //监听全局的主题颜色变化
            appColor.observe(viewLifecycleOwner, Observer {
                setUiTheme(it, toolbar, floatbtn, swipeRefresh, loadsir, footView)
            })
            //监听全局的列表动画改变
            appAnimation.observe(viewLifecycleOwner, Observer {
                articleAdapter.setAdapterAnimion(it)
            })
            //监听全局的收藏信息，收藏的id与数据源列表id匹配则需要更新
            eventViewModel.collect.observe(viewLifecycleOwner, Observer {
                for (index in articleAdapter.data.indices) {
                    if (articleAdapter.data[index].id == it.id) {
                        articleAdapter.data[index].collect = it.collect
                        articleAdapter.notifyItemChanged(index)
                        break
                    }
                }
            })
        }

    }
}