package com.example.wanandroid.ui.fragment.tree

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.bindViewPager2
import com.example.wanandroid.app.ext.init
import com.example.wanandroid.app.ext.setUiTheme
import com.example.wanandroid.databinding.FragmentViewpagerBinding
import com.example.wanandroid.viewmodel.state.TreeViewModel
import kotlinx.android.synthetic.main.include_viewpager.*
import kotlinx.android.synthetic.main.include_viewpager.view.*
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/27 15:22
 *description: 广场模块父fragment，管理四个子fragment
 *email:lkw@mantoo.com.cn
 */
class TreeArrFragment : BaseFragment<TreeViewModel, FragmentViewpagerBinding>() {
    val titleData = arrayListOf("广场", "每日一问", "体系", "导航")

    override fun layoutId(): Int = R.layout.fragment_viewpager

    private var fragments: ArrayList<Fragment> = arrayListOf()

    init {
        fragments.add(PlazaFragment())
        fragments.add(AskFragment())
        fragments.add(SystemFragment())
        fragments.add(NavigationFragment())
    }

    override fun initView(savedInstanceState: Bundle?) {
        //初始化是设置主题颜色
        shareViewModel.appColor.value.let { viewpager_linear.setBackgroundColor(it) }
        include_viewpager_toolbar.run {
            inflateMenu(R.menu.todo_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.todo_add -> {
                        if (shareViewModel.isLogin.value) {
                            nav().navigate(R.id.action_mainFragment_to_addArticleFragment)
                        } else {
                            nav().navigate(R.id.action_mainFragment_to_loginFragment)
                        }
                    }
                }
                true
            }
        }
    }

    override fun lazyLoadData() {
        //初始化viewPager2
        view_pager.init(this, fragments).offscreenPageLimit = fragments.size
        //初始化magic_indicator
        magic_indicator.bindViewPager2(view_pager, mStringList = titleData) {
            if (it != 0) {
                include_viewpager_toolbar.menu.clear()
            } else {
                include_viewpager_toolbar.menu.hasVisibleItems().let { flag ->
                    if (!flag) include_viewpager_toolbar.inflateMenu(R.menu.todo_menu)
                }
            }
        }
    }

    override fun createObserver() {
        shareViewModel.appColor.observe(viewLifecycleOwner, Observer {
            setUiTheme(it, viewpager_linear)
        })
    }
}