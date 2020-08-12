package com.example.wanandroid.ui.fragment.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.app.utils.CacheUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.databinding.FragmentSearchBinding
import com.example.wanandroid.ui.adapter.SearcHistoryAdapter
import com.example.wanandroid.ui.adapter.SearchHotAdapter
import com.example.wanandroid.viewmodel.request.RequestSearchViewModel
import com.example.wanandroid.viewmodel.state.SearchViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.parseState
import me.hgj.jetpackmvvm.ext.util.toJson

/**
 *@ author: lkw
 *created on:2020/7/21 10:51
 *description: 搜索页面
 *email:lkw@mantoo.com.cn
 */
class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>() {

    //热搜列表适配器
    private val searchHotAdapter: SearchHotAdapter by lazy { SearchHotAdapter(arrayListOf()) }

    //搜索历史记录列表适配器
    private val searcHistoryAdapter: SearcHistoryAdapter by lazy { SearcHistoryAdapter(arrayListOf()) }

    private val requestSearchViewModel: RequestSearchViewModel by lazy { getViewModel<RequestSearchViewModel>() }
    override fun layoutId(): Int = R.layout.fragment_search

    override fun initView(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        toolbar.run {
            //设置menu关键代码
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            initClose {
                nav().navigateUp()
            }
        }
        //获取主题颜色去设置字体颜色
        shareViewModel.appColor.value.let { setUiTheme(it, search_text1, search_text2) }
        //初始化搜搜历史RecycleView
        search_historyRv.init(LinearLayoutManager(context), searcHistoryAdapter, false)
        //初始化热门搜索RecycleView
        val layoutManager = FlexboxLayoutManager(context)
        //方向 主轴为水平方向，起点为左短
        layoutManager.flexDirection = FlexDirection.ROW
        //左对齐
        layoutManager.justifyContent = JustifyContent.FLEX_START
        search_hotRv.init(layoutManager, searchHotAdapter, false)
        searcHistoryAdapter.run {
            setNbOnItemClickListener { adapter, view, position ->
                val queryStr = searcHistoryAdapter.data[position]
                upDataKey(queryStr)
                //跳转到搜索结果页面
                nav().navigate(R.id.action_searchFragment_to_searchResultFragment,
                    Bundle().apply {
                        putString("searchKey", queryStr)
                    })
            }
            addChildClickViewIds(R.id.item_history_del)
            setNbOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.item_history_del -> {
                        requestSearchViewModel.historyData.value?.let {
                            it.removeAt(position)
                            requestSearchViewModel.historyData.postValue(it)
                        }
                    }
                }
            }
        }
        searchHotAdapter.run {
            setNbOnItemClickListener { adapter, view, position ->
                val queryStr = searchHotAdapter.data[position].name
                upDataKey(queryStr)
                //跳转页面
                nav().navigate(R.id.action_searchFragment_to_searchResultFragment,
                    Bundle().apply {
                        putString("searchKey", queryStr)
                    })
            }
        }
        search_clear.setOnClickListener {
            activity?.let {
                MaterialDialog(it)
                    .cancelable(false)
                    .lifecycleOwner(this)
                    .show {
                        title(text = "温馨提示")
                        message(text = "确认清空吗?")
                        negativeButton(text = "取消")
                        positiveButton(text = "清空") {
                            //清空
                            requestSearchViewModel.historyData.postValue(arrayListOf())
                        }
                        getActionButton(WhichButton.NEGATIVE).updateTextColor(
                            SettingUtil.getColor(
                                it
                            )
                        )
                        getActionButton(WhichButton.POSITIVE).updateTextColor(
                            SettingUtil.getColor(
                                it
                            )
                        )
                    }
            }
        }
    }

    override fun lazyLoadData() {
        //获取历史搜索数据
        requestSearchViewModel.getHistoryData()
        //获取热门搜索数据
        requestSearchViewModel.getHotData()
    }


    override fun createObserver() {
        requestSearchViewModel.run {
            //监听热门数据变化
            hotData.observe(viewLifecycleOwner, Observer {
                parseState(it, {
                    searchHotAdapter.setNewInstance(it)
                })
            })
            //监听历史数据变化
            historyData.observe(viewLifecycleOwner, Observer {
                searcHistoryAdapter.data = it
                searcHistoryAdapter.notifyDataSetChanged()
                CacheUtil.setSearchHistoryData(it.toJson())
            })
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView.run {
            maxWidth = Integer.MAX_VALUE
            onActionViewExpanded()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                //searchView监听
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //当点击搜索时 输入法的搜索，和右边的搜索都会触发
                    query?.let { queryStr ->
                        upDataKey(queryStr)
                        //跳转页面
                        nav().navigate(R.id.action_searchFragment_to_searchResultFragment,
                            Bundle().apply {
                                putString("searchKey", queryStr)
                            })
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
            isSubmitButtonEnabled = true//是否展示右面图表
            val field = javaClass.getDeclaredField("mGoButton")
            field.run {
                isAccessible = true
                val mGoButton = get(searchView) as ImageView
                mGoButton.setImageResource(R.mipmap.ic_search)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * 更新搜索词
     */
    fun upDataKey(keyStr: String) {
        requestSearchViewModel.historyData.value?.let {
            if (it.contains(keyStr)) {
                //当搜索历史中包含改数据时 ，删除
                it.remove(keyStr)
            } else if (it.size >= 10) {
                //如果集合的size 有10个以上了，删除最后一个
                it.removeAt(it.size - 1)
            }
            //添加新数据到第一条
            it.add(0, keyStr)
            requestSearchViewModel.historyData.postValue(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as? AppCompatActivity)?.setSupportActionBar(null)
    }
}