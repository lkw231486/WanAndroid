package com.example.wanandroid.ui.fragment.tree

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.app.weight.recyclerview.SpaceItemDecoration
import com.example.wanandroid.data.model.bean.AriticleResponse
import com.example.wanandroid.databinding.IncludeListBinding
import com.example.wanandroid.ui.adapter.NavigationAdapter
import com.example.wanandroid.viewmodel.request.RequestTreeViewModel
import com.example.wanandroid.viewmodel.state.TreeViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on: 2020/7/29 17:37
 *description: 导航
 *email:lkw@mantoo.com.cn
 */
class NavigationFragment : BaseFragment<TreeViewModel, IncludeListBinding>() {

    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //初始化体系适配器，arrayListOf()相当于java里null对象
    private val navigationAdapter: NavigationAdapter by lazy { NavigationAdapter(arrayListOf()) }

    /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestTreeViewModel: RequestTreeViewModel by lazy { getViewModel<RequestTreeViewModel>() }
    override fun layoutId(): Int = R.layout.include_list

    override fun initView(savedInstanceState: Bundle?) {
        //状态也配置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击重试时触发的操作
            loadSir.showCallback(LoadingCallback::class.java)
            requestTreeViewModel.getNavigationData()
        }
        //初始化recycleView
        recyclerView.init(LinearLayoutManager(context), navigationAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            it.initFloatBtn(floatbtn)
        }
        //初始化swipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求
            requestTreeViewModel.getNavigationData()
        }
        navigationAdapter.setNavigationClickInterFace(object :
            NavigationAdapter.NavigationClickInterFace {
            override fun onNavigationClickListener(item: AriticleResponse, view: View) {
                nav().navigate(R.id.action_mainFragment_to_webFragment, Bundle().apply {
                    putParcelable("articleData", item)
                })
            }
        })

    }

    override fun lazyLoadData() {
        requestTreeViewModel.getNavigationData()
    }

    override fun createObserver() {
        requestTreeViewModel.nagivationDataState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            if (it.isSuccess) {
                loadSir.showSuccess()
                navigationAdapter.setNewInstance(it.listDate)
            } else {
                loadSir.setErrorText(it.errorMsg)
                loadSir.showCallback(ErrorCallback::class.java)
            }
        })
        shareViewModel.run {
            //监听全局主题颜色改变
            appColor.observe(viewLifecycleOwner, Observer {
                setUiTheme(it, floatbtn, swipeRefresh, loadSir)
            })
            //监听动画列表变化
            appAnimation.observe(viewLifecycleOwner, Observer {
                navigationAdapter.setAdapterAnimion(it)
            })
        }
    }
}