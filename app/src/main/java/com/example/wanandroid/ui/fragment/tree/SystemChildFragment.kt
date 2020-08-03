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
import me.hgj.jetpackmvvm.ext.util.toHtml

/**
 *@ author: lkw
 *created on: 2020/8/2 11:47
 *description: 体系子页面
 *email:lkw@mantoo.com.cn
 */
class SystemChildFragment : BaseFragment<TreeViewModel, IncludeListBinding>() {

    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>
    private var cid = -1

    //recyclerview的底部加载view 因为在首页要动态改变他的颜色，所以加了他这个字段
    private lateinit var footView: DefineLoadMoreView

    //适配器
    private val articleAdapter: ArticleAdapter by lazy { ArticleAdapter(arrayListOf()) }

    //收藏viewmodel 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的
    private val requestCollectViewModel: RequestCollectViewModel by lazy { getViewModel<RequestCollectViewModel>() }

    /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestTreeViewModel: RequestTreeViewModel by lazy { getViewModel<RequestTreeViewModel>() }
    override fun layoutId(): Int = R.layout.include_list

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            cid = it.getInt("cid")
        }
        //状态也配置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击重试时触发的操作
            loadSir.showCallback(LoadingCallback::class.java)
            requestTreeViewModel.getSystemChildData(true, cid)
        }
        //初始化recycleView
        recyclerView.init(LinearLayoutManager(context), articleAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            footView = it.initFooter(SwipeRecyclerView.LoadMoreListener {
                requestTreeViewModel.getSystemChildData(false, cid)
            })
            //初始化floatingActionButton
            it.initFloatBtn(floatbtn)
        }
        //初始化SwipeRefreshLayout
        swipeRefresh.init {
            //触发刷新时请求数据
            requestTreeViewModel.getSystemChildData(true, cid)
        }

        articleAdapter.run {
            setOnCollectViewClickListener(object : ArticleAdapter.OnCollectViewClickListener {
                override fun onClick(item: AriticleResponse, v: CollectView, position: Int) {
                    //如果登录
                    if (shareViewModel.isLogin.value) {
                        //如果关注了
                        if (v.isChecked) {
                            //取消关注
                            requestCollectViewModel.unCollect(item.id)
                        } else {
                            //关注
                            requestCollectViewModel.collect(item.id)
                        }
                    } else {
                        v.isChecked = true
                        nav().navigate(R.id.action_systemArrFragment_to_loginFragment)
                    }
                }
            })
            setNbOnItemClickListener { adapter, view, position ->
                nav().navigate(R.id.action_systemArrFragment_to_webFragment, Bundle().apply {
                    putParcelable("articleData", articleAdapter.data[position])
                })
            }
            addChildClickViewIds(R.id.item_home_author, R.id.item_project_author)
            setNbOnItemChildClickListener { adapter, view, position ->
                nav().navigate(R.id.action_systemArrFragment_to_lookInfoFragment, Bundle().apply {
                    putInt("id", articleAdapter.data[position].userId)
                })
            }
        }
    }

    override fun lazyLoadData() {
        requestTreeViewModel.getSystemChildData(true, cid)
    }

    override fun createObserver() {
        requestTreeViewModel.systemChileDataState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
            if (it.isSuccess) {
                //请求成功
                when {
                    //是第一页，并且没有数据,显示空布局
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
                    loadSir.setErrorText(it.errorMsg)
                    loadSir.showCallback(ErrorCallback::class.java)
                } else {
                    recyclerView.loadMoreError(0, it.errorMsg)
                }
            }
        })

        requestCollectViewModel.collectUiState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                //收藏获取取消收藏成功，发送全局消息
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
                            if (it.id.toInt() == item.id) {
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

    companion object {
        fun newInstance(cid: Int): SystemChildFragment {
            return SystemChildFragment().apply {
                arguments = Bundle().apply {
                    putInt("cid", cid);
                }
            }
        }
    }
}