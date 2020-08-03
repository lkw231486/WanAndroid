package com.example.wanandroid.ui.fragment.tree

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.app.weight.customview.CollectView
import com.example.wanandroid.app.weight.loadCallBack.EmptyCallback
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.app.weight.recyclerview.DefineLoadMoreView
import com.example.wanandroid.app.weight.recyclerview.SpaceItemDecoration
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.data.model.bean.CollectBus
import com.example.wanandroid.databinding.IncludeListBinding
import com.example.wanandroid.ui.adapter.ArticleAdapter
import com.example.wanandroid.viewmodel.request.RequestCollectViewModel
import com.example.wanandroid.viewmodel.request.RequestTreeViewModel
import com.example.wanandroid.viewmodel.state.TreeViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/27 16:17
 *description: 广场fragment
 *email:lkw@mantoo.com.cn
 */
class PlazaFragment : BaseFragment<TreeViewModel, IncludeListBinding>() {

    //页面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //收藏viewModel  注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的
    private val requestCollectViewModel: RequestCollectViewModel by lazy { getViewModel<RequestCollectViewModel>() }

    //请求Viewmodel 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的
    private val requestTreeViewModel: RequestTreeViewModel by lazy { getViewModel<RequestTreeViewModel>() }

    //recyclerview的底部加载view 因为要在首页动态改变他的颜色，所以加了他这个字段
    private lateinit var footView: DefineLoadMoreView

    private val articleAdapter: ArticleAdapter by lazy {
        ArticleAdapter(
            arrayListOf(),
            showTag = true
        )
    }

    override fun layoutId(): Int = R.layout.include_list

    override fun initView(savedInstanceState: Bundle?) {
        //状态页配置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击从试时触发操作
            loadSir.showCallback(LoadingCallback::class.java)
            requestTreeViewModel.getPlazaData(true)
        }
        //初始化recycleView
        recyclerView.init(LinearLayoutManager(context), articleAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            footView = it.initFooter(SwipeRecyclerView.LoadMoreListener {
                requestTreeViewModel.getPlazaData(false)
            })
            //初始化FloatingActionButton
            it.initFloatBtn(floatbtn)
        }
        //初始化SwipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求数据
            requestTreeViewModel.getPlazaData(true)
        }
        //设置收藏按钮点击事件
        articleAdapter.run {
            setOnCollectViewClickListener(object : ArticleAdapter.OnCollectViewClickListener {
                override fun onClick(item: AriticleResponse, v: CollectView, position: Int) {
                    if (shareViewModel.isLogin.value) {//如果登录
                        if (v.isChecked) {//如果勾选，取消关注
                            requestCollectViewModel.unCollect(item.id)
                        } else {//添加关注
                            requestCollectViewModel.collect(item.id)
                        }
                    } else {
                        //跳转到登陆页面
                        nav().navigate(R.id.action_mainFragment_to_loginFragment)
                    }
                }
            })
            //设置列表item点击事件
            setNbOnItemClickListener { adapter, view, position ->
                //打开webView浏览页面
                nav().navigate(R.id.action_mainFragment_to_webFragment, Bundle().apply {
                    //通过bundle传入当前item文章数据源
                    putParcelable("articleData", articleAdapter.data[position])
                })
            }
            //添加子点击按钮
            addChildClickViewIds(R.id.item_home_author, R.id.item_project_author)
            setNbOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.item_home_author, R.id.item_project_author -> {
                        // 打开作者个人主页面
                        nav().navigate(
                            R.id.action_mainFragment_to_lookInfoFragment,
                            Bundle().apply {
                                putInt("id", articleAdapter.data[position].userId)
                            })
                    }
                }
            }
        }
    }

    override fun lazyLoadData() {
        requestTreeViewModel.getPlazaData(true)
    }

    override fun createObserver() {
        requestTreeViewModel.plazaDataState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
            if (it.isSuccess) {
                //请求成功
                when {
                    it.isFirstEmpty -> {
                        loadSir.showCallback(EmptyCallback::class.java)
                    }
                    //是第一页
                    it.isRefresh -> {
                        loadSir.showSuccess()
                        articleAdapter.setNewInstance(it.listDate)
                    }
                    //不是第一页
                    else -> {
                        loadSir.showSuccess()
                        articleAdapter.addData(it.listDate)
                    }
                }
            } else {
                //失败
                if (it.isRefresh) {
                    //如果是第一页，则显示错误界面，并提示错误信息
                    loadSir.showCallback(ErrorCallback::class.java)
                    loadSir.setErrorMsg(it.errorMsg)
                } else {
                    recyclerView.loadMoreError(0, it.errorMsg)
                }
            }
        })
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
                        for (item in articleAdapter.data) {
                            if (id.toInt() == item.id) {
                                item.collect = true
                                break
                            }
                        }
                    }
                } else {
                    for (item in articleAdapter.data) {
                        item.collect = false
                    }
                }
                articleAdapter.notifyDataSetChanged()
            })
            // 监听全局的主题颜色改变
            appColor.observe(viewLifecycleOwner, Observer {
                setUiTheme(it, floatbtn, swipeRefresh, loadSir)
            })
            //监听全局动画改编
            appAnimation.observe(viewLifecycleOwner, Observer {
                articleAdapter.setAdapterAnimion(it)
            })
        }
        //监听全局的收藏信息 收藏的Id跟本列表的数据id匹配则需要更新
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