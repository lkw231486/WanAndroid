package com.example.wanandroid.ui.fragment.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.wanandroid.R
import com.example.wanandroid.app.base.BaseActivity
import com.example.wanandroid.app.base.BaseFragment
import com.example.wanandroid.app.ext.*
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.data.model.bean.ClassifyResponse
import com.example.wanandroid.databinding.FragmentViewpagerBinding
import com.example.wanandroid.viewmodel.request.RequestProjectViewModel
import com.example.wanandroid.viewmodel.state.ProjectViewModel
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import kotlinx.android.synthetic.main.include_viewpager.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState
import java.lang.reflect.Array.newInstance

/**
 *@ author: lkw
 *created on:2020/7/3 17:48
 *description: 项目页面
 *email:lkw@mantoo.com.cn
 */
class ProjectFragment : BaseFragment<ProjectViewModel, FragmentViewpagerBinding>() {

    //界面状态管理者
    private lateinit var loadSir: LoadService<Any>

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    var mDateList: ArrayList<ClassifyResponse> = arrayListOf()

    /** 注意，在by lazy中使用getViewModel一定要使用泛型，虽然他提示不报错，但是你不写是不行的 */
    private val requestProjectViewModel: RequestProjectViewModel by lazy { getViewModel<RequestProjectViewModel>() }

    override fun layoutId(): Int = R.layout.fragment_viewpager

    override fun initView(savedInstanceState: Bundle?) {
        //状态页配置
        loadSir = LoadServiceInit(view_pager) {
            //点击重试时处罚的操作
            loadSir.showCallback(LoadingCallback::class.java)
            requestProjectViewModel.getProjectTitleData()
        }
        //初始化viewpager2
        view_pager.init(this, fragments)
        //初始化magic_indicator
        magic_indicator.bindViewPager2(view_pager, mDateList)
        //初始化设置主题颜色
        shareViewModel.appColor.value.let { viewpager_linear.setBackgroundColor(it) }

    }

    override fun lazyLoadData() {
        //请求标题数据
        requestProjectViewModel.getProjectTitleData()
    }

    override fun createObserver() {
        requestProjectViewModel.titleData.observe(viewLifecycleOwner, Observer { data ->
            parseState(data, {
                mDateList.clear()
                fragments.clear()
                mDateList.add(0, ClassifyResponse(name = "最新项目"))
                mDateList.addAll(it)
                fragments.add(ProjectChildFragment.newInstance(0, true))
                it.forEach { classify ->
                    fragments.add(ProjectChildFragment.newInstance(classify.id, false))
                }
                magic_indicator.navigator.notifyDataSetChanged()
                view_pager.adapter?.notifyDataSetChanged()
                view_pager.offscreenPageLimit = mDateList.size
                loadSir.showSuccess()
            }, {
                //请求项目标题失败
                loadSir.showCallback(ErrorCallback::class.java)
                loadSir.setErrorText(it.errorMsg)
            })
        })
        shareViewModel.appColor.observe(viewLifecycleOwner, Observer {
            setUiTheme(it, viewpager_linear, loadSir)
        })
    }
}