package com.example.wanandroid.ui.fragment.tree

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.bindViewPager2
import com.example.wanandroid.app.ext.init
import com.example.wanandroid.app.ext.initClose
import com.example.wanandroid.data.model.bean.SystemResponse
import com.example.wanandroid.databinding.FragmentSystemBinding
import com.example.wanandroid.ui.adapter.SystemChildAdapter
import com.example.wanandroid.viewmodel.state.TreeViewModel
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.include_viewpager.*
import me.hgj.jetpackmvvm.base.fragment.BaseVmDbFragment
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on: 2020/8/2 11:32
 *description: 体系子页面
 *email:lkw@mantoo.com.cn
 */
class SystemArrFragment : BaseFragment<TreeViewModel, FragmentSystemBinding>() {

    lateinit var data: SystemResponse
    var index = 0
    private var fragments: ArrayList<Fragment> = arrayListOf()

    override fun layoutId(): Int = R.layout.fragment_system

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            data= it.getParcelable("data")!!
            index = it.getInt("index")
        }
        toolbar.initClose(data.name) {
            nav().navigateUp()
        }
        //初始化是设置主题颜色
        shareViewModel.appColor.value.let {
            viewpager_linear.setBackgroundColor(it)
        }
        //设置栏目标题居左显示
        (magic_indicator.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.LEFT
    }

    override fun lazyLoadData() {
        data.children.forEach {
            fragments.add(SystemChildFragment.newInstance(it.id))
        }
        //初始化viewPager2
        view_pager.init(this, fragments).offscreenPageLimit = fragments.size
        //初始化magic_indicator
        magic_indicator.bindViewPager2(view_pager, data.children)
        view_pager.postDelayed({ view_pager.currentItem = index }, 100)
    }

    override fun createObserver() {

    }
}