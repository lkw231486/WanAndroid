package com.example.wanandroid.ui.fragment.tree

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.app.weight.recyclerview.SpaceItemDecoration
import com.example.wanandroid.data.model.bean.SystemResponse
import com.example.wanandroid.databinding.IncludeListBinding
import com.example.wanandroid.ui.adapter.SystemAdapter
import com.example.wanandroid.viewmodel.request.RequestTreeViewModel
import com.example.wanandroid.viewmodel.state.TreeViewModel
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on: 2020/8/2 10:34
 *description: 体系
 *email:lkw@mantoo.com.cn
 */
class SystemFragment : BaseFragment<TreeViewModel, IncludeListBinding>() {

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>
    private val systemAdapter: SystemAdapter by lazy { SystemAdapter(arrayListOf()) }

    /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestTreeViewModel: RequestTreeViewModel by lazy { getViewModel<RequestTreeViewModel>() }

    override fun layoutId() = R.layout.include_list

    override fun initView(savedInstanceState: Bundle?) {
        //状态页配置
        loadsir = LoadServiceInit(swipeRefresh) {
            //点击重试时触发的操作
            loadsir.showCallback(LoadingCallback::class.java)
            requestTreeViewModel.getSystemData()
        }
        //初始化recyclerView
        recyclerView.init(LinearLayoutManager(context), systemAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            it.initFloatBtn(floatbtn)
        }
        //初始化 SwipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求数据
            requestTreeViewModel.getSystemData()
        }
        systemAdapter.run {
            setNbOnItemClickListener { _, view, position ->
                nav().navigate(R.id.action_mainFragment_to_systemArrFragment,
                    Bundle().apply {
                        putParcelable("data", systemAdapter.data[position])
                    }
                )
            }
            setChildClick { data: SystemResponse, view, position ->
                nav().navigate(R.id.action_mainFragment_to_systemArrFragment,
                    Bundle().apply {
                        putParcelable("data", data)
                        putInt("index", position)
                    })
            }
        }

//        systemAdapter.run {
//            setChildClick { data:SystemResponse, view, position ->
//                nav().navigate(R.id.action_mainFragment_to_systemChildFragment, Bundle().apply {
////                    putString("aaa","你好") 60
////                    data.children= arrayListOf()
//                    putParcelable("data", data)
//                    putInt("cid", 60)
//                })
//            }
//        }
    }

    override fun lazyLoadData() {
        requestTreeViewModel.getSystemData()
    }

    override fun createObserver() {
        requestTreeViewModel.systemDataState.observe(viewLifecycleOwner, Observer {
            swipeRefresh.isRefreshing = false
            if (it.isSuccess) {
                loadsir.showSuccess()
                systemAdapter.setNewInstance(it.listDate)
            } else {
                loadsir.setErrorText(it.errorMsg)
                loadsir.showCallback(ErrorCallback::class.java)
            }
        })

        shareViewModel.run {
            //监听全局的主题颜色改变
            appColor.observe(viewLifecycleOwner, Observer {
                setUiTheme(it, floatbtn, swipeRefresh, loadsir)
            })
            //监听全局的列表动画改编
            appAnimation.observe(viewLifecycleOwner, Observer {
                systemAdapter.setAdapterAnimion(it)
            })
        }

    }
}