package com.example.wanandroid.ui.fragment.collect

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
import com.example.wanandroid.app.weight.recyclerview.SpaceItemDecoration
import com.example.wanandroid.data.model.bean.CollectUrlResponse
import com.example.wanandroid.databinding.IncludeListBinding
import com.example.wanandroid.ui.adapter.CollectUrlAdapter
import com.example.wanandroid.viewmodel.request.RequestCollectViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/9 14:53
 *description: 网址收藏fragment
 *email:lkw@mantoo.com.cn
 */
class CollectUrlFragment : BaseFragment<RequestCollectViewModel, IncludeListBinding>() {

    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>
    private val collectUrlAdapter: CollectUrlAdapter by lazy { CollectUrlAdapter(arrayListOf()) }
    override fun layoutId(): Int = R.layout.include_list

    override fun initView(savedInstanceState: Bundle?) {
        //状态页设置
        loadSir = LoadServiceInit(swipeRefresh) {
            //点击从试操作
            loadSir.showCallback(LoadingCallback::class.java)
            mViewModel.getCollectUrlData()
        }
        //初始化recyclerView
        recyclerView.init(LinearLayoutManager(context), collectUrlAdapter).let {
            it.addItemDecoration(SpaceItemDecoration(0, ConvertUtils.dp2px(8f)))
            //初始化FloatingActionButton
            it.initFloatBtn(floatbtn)
        }
        //初始化SwipeRefreshLayout
        swipeRefresh.init {
            //触发监听请求事件
            mViewModel.getCollectUrlData()
        }
        collectUrlAdapter.run {
            setOnCollectViewClickListener(object : CollectUrlAdapter.OnCollectViewClickListener {
                override fun onClick(item: CollectUrlResponse, v: CollectView, position: Int) {
                    if (v.isChecked) {
                        mViewModel.unCollectUrl(item.id)
                    } else {
                        mViewModel.collectUrl(item.name, item.link)
                    }
                }
            })
            setNbOnItemClickListener { adapter, view, position ->
                nav().navigate(R.id.action_collectFragment_to_webFragment, Bundle().apply {
                    putParcelable("collectUrl", collectUrlAdapter.data[position])
                })
            }
        }
    }

    override fun lazyLoadData() {
        //获取收藏网址数据
        mViewModel.getCollectUrlData()
    }

    override fun createObserver() {
        mViewModel.urlDateState.observe(viewLifecycleOwner, Observer {
            //请求成功,关闭加载框
            swipeRefresh.isRefreshing = false
            //设置列表是否还可以加载出更多数据
            recyclerView.loadMoreFinish(it.isEmpty, it.hasMore)
            if (it.isSuccess) {
                //请求数据成功
                when {
                    //第一页若没有数据，显示空页面
                    it.isEmpty -> {
                        loadSir.showCallback(EmptyCallback::class.java)
                    }
                    else -> {
                        //数据不为空，刷新适配器数据源
                        loadSir.showSuccess()
                        collectUrlAdapter.setNewInstance(it.listDate)
                    }
                }
            } else {
                //失败
                loadSir.setErrorText(it.errorMsg) //setErrorText拓展函数，提示错误信息
                loadSir.showCallback(ErrorCallback::class.java)
            }

        })

        mViewModel.collectUrlUiState.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                for (index in collectUrlAdapter.data.indices) {
                    if (collectUrlAdapter.data[index].id == it.id) {
                        collectUrlAdapter.remove(index)
                        if (collectUrlAdapter.data.size == 0) {
                            loadSir.showCallback(EmptyCallback::class.java)
                        }
                        return@Observer
                    }
                }
            } else {
                showMessage(it.errorMsg)
                for (index in collectUrlAdapter.data.indices) {
                    if (collectUrlAdapter.data[index].id == it.id) {
                        collectUrlAdapter.notifyItemChanged(index)
                        break
                    }
                }
            }
        })
        eventViewModel.run {
            //监听全局的收藏信息 收藏的Id跟本列表的数据id匹配则 需要删除他 否则则请求最新收藏数据
            collect.observe(viewLifecycleOwner, Observer {
                for (index in collectUrlAdapter.data.indices) {
                    if (collectUrlAdapter.data[index].id == it.id) {
                        collectUrlAdapter.data.removeAt(index)
                        collectUrlAdapter.notifyItemChanged(index)
                        if (collectUrlAdapter.data.size == 0) {
                            loadSir.showCallback(EmptyCallback::class.java)
                        }
                        return@Observer
                    }
                }
                mViewModel.getCollectUrlData()
            })
        }
    }
}