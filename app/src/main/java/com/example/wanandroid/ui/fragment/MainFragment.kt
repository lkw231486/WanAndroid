package com.example.wanandroid.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.init
import com.example.wanandroid.app.ext.setUiTheme
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.databinding.FragmentMainBinding
import com.example.wanandroid.ui.fragment.home.HomeFragment
import com.example.wanandroid.ui.fragment.me.MeFragment
import com.example.wanandroid.ui.fragment.project.ProjectFragment
import com.example.wanandroid.ui.fragment.publicnumber.PublicNumberFragment
import com.example.wanandroid.ui.fragment.tree.TreeArrFragment
import com.example.wanandroid.viewmodel.state.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*

/**
 *@ author: lkw
 *created on:2020/7/3 14:09
 *description: 主页面Fragment
 *email:lkw@mantoo.com.cn
 */
@Suppress("DEPRECATION")
class MainFragment : BaseFragment<MainViewModel, FragmentMainBinding>() {

    /**
     * fragment容器
     */
    var fragments = arrayListOf<Fragment>()
    private val homeFragment: HomeFragment by lazy { HomeFragment() }
    private val projectFragment:ProjectFragment by lazy { ProjectFragment() }
    private val treeArrFragment:TreeArrFragment by lazy { TreeArrFragment() }
    private val publicNumberViewModel:PublicNumberFragment by lazy { PublicNumberFragment() }
    private val meFragment: MeFragment by lazy { MeFragment() }

    init {
        //将fragment添加进去
        fragments.apply {
            add(homeFragment)
            add(projectFragment)
            add(treeArrFragment)
            add(publicNumberViewModel)
            add(meFragment)
        }
    }

    override fun layoutId(): Int = R.layout.fragment_main

    override fun initView(savedInstanceState: Bundle?) {
        //初始化viewPager2
        main_viewpager.init(this, fragments, false).run {
            offscreenPageLimit = fragments.size //设置缓存页数等于容器的长度，意思缓存所有fragment
        }
        //初始化bottomBar
        main_bottom.run {
            enableAnimation(false)
            enableShiftingMode(false)
            enableItemShiftingMode(false)
            shareViewModel.appColor.value.let {
                itemIconTintList = SettingUtil.getOneColorStateList(it)
                itemTextColor = SettingUtil.getOneColorStateList(it)
            }
            setTextSize(12F)
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_main -> main_viewpager.setCurrentItem(0, false)
                    R.id.menu_project -> main_viewpager.setCurrentItem(1, false)
                    R.id.menu_system -> main_viewpager.setCurrentItem(2, false)
                    R.id.menu_public -> main_viewpager.setCurrentItem(3, false)
                    R.id.menu_me -> main_viewpager.setCurrentItem(4, false)
                }
                true
            }
        }
    }

    override fun lazyLoadData() {
    }

    override fun createObserver() {
        shareViewModel.appColor.observe(viewLifecycleOwner, Observer {
            setUiTheme(it, main_bottom)
        })
    }
}