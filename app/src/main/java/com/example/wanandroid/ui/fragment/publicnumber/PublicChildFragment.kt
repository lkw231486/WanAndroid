package com.example.wanandroid.ui.fragment.publicnumber

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
import com.example.wanandroid.viewmodel.request.RequestPublicNumberViewModel
import com.example.wanandroid.viewmodel.state.PublicNumberViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on: 2020/8/3 14:22
 *description: 公众号子页面
 *email:lkw@mantoo.com.cn
 */
class PublicChildFragment : BaseFragment<PublicNumberViewModel, IncludeListBinding>() {

    //适配器
    private val articleAdapter: ArticleAdapter by lazy { ArticleAdapter(arrayListOf()) }

    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //recyclerview的底部加载view 因为在首页要动态改变他的颜色，所以加了他这个字段
    private lateinit var footView: DefineLoadMoreView

    //改项目对应的id
    private var cid = 0

    //收藏viewModel 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的
    private val requestCollectViewModel: RequestCollectViewModel by lazy { getViewModel<RequestCollectViewModel>() }

    //请求viewModel 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的
    private val requestPublicNumberViewModel: RequestPublicNumberViewModel by lazy { getViewModel<RequestPublicNumberViewModel>() }

    override fun layoutId(): Int = R.layout.include_list

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            cid = it.getInt("cid")
        }
    }

    /**
     * 懒加载，优化运行效率
     */
    override fun lazyLoadData() {
        //状态页配置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击重试时触发刷新操作
            loadSir.showCallback(LoadingCallback::class.java)
            requestPublicNumberViewModel.getPublicData(true, cid)
        }
        //初始化recycleView
        recyclerView.init(LinearLayoutManager(context), articleAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            footView = it.initFooter(SwipeRecyclerView.LoadMoreListener {
                //触发加载更多时请求数据
                requestPublicNumberViewModel.getPublicData(false, cid)
            })
            //初始化FloatingActionButton
            it.initFloatBtn(floatbtn)
        }
        //初始化SwipeRefreshLayout
        swipeRefresh.init {
            //出发监听时请求数据
            requestPublicNumberViewModel.getPublicData(true, cid)
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
                    putParcelable("articleData", articleAdapter.data[position])
                })
            }
            addChildClickViewIds(R.id.item_home_author, R.id.item_project_author)
            setNbOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.item_home_author, R.id.item_project_author -> {
                        nav().navigate(
                            R.id.action_mainFragment_to_lookInfoFragment,
                            Bundle().apply {
                                putInt("id", articleAdapter.data[position].userId)
                            })
                    }
                }
            }
        }
        requestPublicNumberViewModel.getPublicData(true, cid)
    }

    override fun createObserver() {
        requestPublicNumberViewModel.publicDataState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
            if (it.isSuccess) {
                //成功
                when {
                    //第一页，并没有数据，显示空布局
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
                    //如果是第一页
                    loadSir.setErrorText(it.errorMsg)
                    loadSir.showCallback(ErrorCallback::class.java)
                } else {
                    recyclerView.loadMoreError(0, it.errorMsg)
                }
            }
        })
        requestCollectViewModel.collectUiState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
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
            //监听全局的主题颜色改变
            appColor.observe(viewLifecycleOwner, Observer {
                setUiTheme(it, floatbtn, swipeRefresh, loadSir, footView)
            })
            //监听全局的列表动画改编
            appAnimation.observe(viewLifecycleOwner, Observer {
                articleAdapter.setAdapterAnimion(it)
            })
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

    companion object {
        fun newInstance(cid: Int): PublicChildFragment {
            val args = Bundle()
            args.putInt("cid", cid)
            val fragment = PublicChildFragment()
            fragment.arguments = args
            return fragment
        }
    }
}