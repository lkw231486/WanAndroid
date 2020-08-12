package com.example.wanandroid.app.ext

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.wanandroid.R
import com.example.wanandroid.app.App
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.app.weight.loadCallBack.ErrorCallback
import com.example.wanandroid.app.weight.loadCallBack.LoadingCallback
import com.example.wanandroid.app.weight.recyclerview.DefineLoadMoreView
import com.example.wanandroid.app.weight.viewpager.ScaleTransitionPagerTitleView
import com.example.wanandroid.data.model.bean.ClassifyResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import com.zhpan.bannerview.BannerViewPager
import me.hgj.jetpackmvvm.ext.util.toHtml
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 *@ author: lkw
 *created on:2020/7/2 17:15
 *description: 项目中自定义类的拓展函数
 *email:lkw@mantoo.com.cn
 */


fun BannerViewPager<*, *>.setPageListener(onPageSelected: (Int) -> Unit) {
    this.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }

    })
}

/**
 * 隐藏键盘
 */
fun hideSoftKetBoard(activity: Activity?) {
    activity?.let { act ->
        val view = act.currentFocus
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}

/**
 * 初始化普通的toolbar，只初始化标题
 */
fun Toolbar.init(titleStr: String = ""): Toolbar {
    setBackgroundColor(SettingUtil.getColor(App.instant))
    title = titleStr
    return this
}

/**
 *设置适配器的列表动画
 */
fun BaseQuickAdapter<*, *>.setAdapterAnimion(model: Int) {
    //等于0，关闭列表动画，否则开启
    if (model == 0) {
        this.animationEnable = false
    } else {
        this.animationEnable = true
        this.setAnimationWithDefault(BaseQuickAdapter.AnimationType.values()[model - 1])
    }

}

fun LoadServiceInit(view: View, callback: () -> Unit): LoadService<Any> {
    var loadsir = LoadSir.getDefault().register(view) {
        //点击时候出发操作
        callback.invoke()
    }
    loadsir.showCallback(LoadingCallback::class.java)
    SettingUtil.setLoadingColor(SettingUtil.getColor(App.instant), loadsir)
    return loadsir
}

/**
 * 设置登录页面标题
 */
fun Toolbar.initClose(
    titleStr: String = "",
    backImage: Int = R.drawable.ic_back,
    onBack: (toolbar: Toolbar) -> Unit
): Toolbar {
    setBackgroundColor(SettingUtil.getColor(App.instant))
    title = titleStr.toHtml()
    setNavigationIcon(backImage)
    setNavigationOnClickListener {
        onBack.invoke(this)
    }
    return this
}

fun ViewPager2.init(
    fragment: Fragment,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}

fun setUiTheme(color: Int, vararg anyList: Any) {
    anyList.forEach {
        when (it) {
            is LoadService<*> -> SettingUtil.setLoadingColor(color, it as LoadService<Any>)
            is FloatingActionButton -> it.backgroundTintList =
                SettingUtil.getOneColorStateList(color)
            is SwipeRefreshLayout -> it.setColorSchemeColors(color)
            is DefineLoadMoreView -> it.setLoadViewColor(SettingUtil.getOneColorStateList(color))
            is BottomNavigationViewEx -> {
                it.itemIconTintList = SettingUtil.getColorStateList(color)
                it.itemTextColor = SettingUtil.getColorStateList(color)
            }
            is Toolbar -> it.setBackgroundColor(color)
            is TextView -> it.setTextColor(color)
            is LinearLayout -> it.setBackgroundColor(color)
            is ConstraintLayout -> it.setBackgroundColor(color)
            is FrameLayout -> it.setBackgroundColor(color)
        }
    }
}

//绑定SwipeRecyclerView
fun SwipeRecyclerView.init(
    layoutManager1: RecyclerView.LayoutManager,
    binderAdapter: RecyclerView.Adapter<*>,
    isScroll: Boolean = true
): SwipeRecyclerView {
    layoutManager = layoutManager1
    setHasFixedSize(true)
    adapter = binderAdapter
    isNestedScrollingEnabled = isScroll
    return this
}

fun SwipeRecyclerView.initFooter(loadMoreListener: SwipeRecyclerView.LoadMoreListener): DefineLoadMoreView {
    val footerView = DefineLoadMoreView(App.instant)
    //给尾部设置颜色
    footerView.setLoadViewColor(SettingUtil.getOneColorStateList(App.instant))
    //设置尾部点击事件回调
    footerView.setmLoadMoreListener(SwipeRecyclerView.LoadMoreListener {
        footerView.onLoading()
        loadMoreListener.onLoadMore()
    })
    this.run {
        //添加加载更多尾部
        addFooterView(footerView)
        setLoadMoreView(footerView)
        //设置加载更多回调
        setLoadMoreListener(loadMoreListener)
    }
    return footerView
}

fun RecyclerView.initFloatBtn(floatBtn: FloatingActionButton) {
    //监听recyclerview滑动到顶部的时候，需要把向上返回顶部的按钮隐藏
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!canScrollVertically(-1)) {
                floatBtn.visibility = View.INVISIBLE
            }
        }
    })
    floatBtn.backgroundTintList = SettingUtil.getOneColorStateList(App.instant)
    floatBtn.setOnClickListener {
        val layoutManager = layoutManager as LinearLayoutManager
        //如果当前recyclerview 最后一个视图位置的索引大于等于40，则迅速返回顶部，否则带有滚动动画效果返回到顶部
        if (layoutManager.findLastVisibleItemPosition() >= 40) {
            scrollToPosition(0)//没有动画迅速返回到顶部(马上)
        } else {
            smoothScrollToPosition(0) //有滚动动画返回到顶部(有点慢)
        }
    }
}

//初始化 SwipeRefreshLayout
fun SwipeRefreshLayout.init(onRefreshListener: () -> Unit) {
    this.run {
        setOnRefreshListener {
            onRefreshListener.invoke()
        }
        //设置主题颜色
        setColorSchemeColors(SettingUtil.getColor(App.instant))
    }
}

fun LoadService<*>.setErrorMsg(message: String) {
    this.setCallBack(ErrorCallback::class.java) { _, view ->
        view.findViewById<TextView>(R.id.error_text).text = message
    }
}

fun MagicIndicator.bindViewPager2(
    viewPager: ViewPager2,
    mDataList: ArrayList<ClassifyResponse> = arrayListOf(),
    mStringList: ArrayList<String> = arrayListOf(),
    action: (index: Int) -> Unit = {}
) {
    val commonNavigator = CommonNavigator(App.instant)
    commonNavigator.adapter = object : CommonNavigatorAdapter() {
        override fun getCount(): Int {
            return if (mDataList.size != 0) {
                mDataList.size
            } else {
                mStringList.size
            }
        }

        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            return ScaleTransitionPagerTitleView(App.instant).apply {
                text = if (mDataList.size != 0) {
                    mDataList[index].name.toHtml()
                } else {
                    mStringList[index].toHtml()
                }
                textSize = 17f
                normalColor = Color.WHITE
                selectedColor = Color.WHITE
                setOnClickListener {
                    viewPager.currentItem = index
                    action.invoke(index)
                }
            }
        }

        override fun getIndicator(context: Context): IPagerIndicator {
            return LinePagerIndicator(context).apply {
                mode = LinePagerIndicator.MODE_EXACTLY
                //线条的宽高度
                lineHeight = UIUtil.dip2px(App.instant, 3.0).toFloat()
                lineWidth = UIUtil.dip2px(App.instant, 30.0).toFloat()
                //线条的圆角
                roundRadius = UIUtil.dip2px(App.instant, 6.0).toFloat()
                startInterpolator = AccelerateInterpolator()
                endInterpolator = DecelerateInterpolator(2.0f)
                //线条的颜色
                setColors(Color.WHITE)
            }
        }
    }
    this.navigator = commonNavigator

    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            this@bindViewPager2.onPageSelected(position)
            action.invoke(position)
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            this@bindViewPager2.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            this@bindViewPager2.onPageScrollStateChanged(state)
        }
    })
}

fun LoadService<*>.setErrorText(message: String) {
    this.setCallBack(ErrorCallback::class.java) { _, view ->
        view.findViewById<TextView>(R.id.error_text).text = message
    }
}

//绑定普通的recycleView
fun RecyclerView.init(
    layoutManager1: RecyclerView.LayoutManager,
    binderAdapter: RecyclerView.Adapter<*>,
    isScroll: Boolean = true
): RecyclerView {
    layoutManager = layoutManager1
    setHasFixedSize(true)
    adapter = binderAdapter
    isNestedScrollingEnabled = isScroll
    return this
}


