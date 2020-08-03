package com.example.wanandroid.app.weight.recyclerview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.blankj.utilcode.util.ConvertUtils
import com.example.wanandroid.R
import com.example.wanandroid.app.utils.SettingUtil
import com.yanzhenjie.recyclerview.SwipeRecyclerView

/**
 *@ author: lkw
 *created on:2020/7/6 15:24
 *description: 这是这个类的主角，如何自定义LoadMoreView。
 *email:lkw@mantoo.com.cn
 */
 class DefineLoadMoreView(context: Context) : LinearLayout(context), SwipeRecyclerView.LoadMoreView,View.OnClickListener {

    val mProgressBar: ProgressBar
    private val mTvMessage: TextView
    private var mLoadMoreListener: SwipeRecyclerView.LoadMoreListener? = null

    fun setmLoadMoreListener(mLoadMoreListener: SwipeRecyclerView.LoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener
    }

    init {
        layoutParams = ViewGroup.LayoutParams(-1, -2)
        gravity = Gravity.CENTER
        visibility = View.GONE
        val minHeight = ConvertUtils.dp2px(36f)
        minimumHeight = minHeight
        View.inflate(context, R.layout.layout_fotter_loadmore, this)
        mProgressBar=findViewById<ProgressBar>(R.id.loading_view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressBar.indeterminateTintMode = PorterDuff.Mode.SRC_ATOP
            mProgressBar.indeterminateTintList = SettingUtil.getOneColorStateList(context)
        }
        mTvMessage = findViewById(R.id.tv_message)
        setOnClickListener(this)
    }

    /**
     * 调用了setAutoLoadMore(false)后，在需要加载更多的时候，这个方法会被调用，并传入加载更多的listener。
     */
    override fun onWaitToLoadMore(loadMoreListener: SwipeRecyclerView.LoadMoreListener?) {
        this.mLoadMoreListener = loadMoreListener
        visibility = View.VISIBLE
        mProgressBar.visibility = View.GONE
        mTvMessage.visibility = View.VISIBLE
        mTvMessage.text = "点我加载更多"
    }

    /**
     *马上开始回调加载更多了，这里应该显示进度条。
     */
    override fun onLoading() {
        visibility = View.VISIBLE
        mProgressBar.visibility = View.VISIBLE
        mTvMessage.visibility = View.VISIBLE
        mTvMessage.text = "正在努力加载，请稍后"
    }

    /**
     * 加载出错啦，下面的错误码和错误信息二选一。
     */
    override fun onLoadError(errorCode: Int, errorMessage: String?) {
        visibility = View.VISIBLE
        mProgressBar.visibility = View.GONE
        mTvMessage.visibility = View.VISIBLE
        // 这里要不直接设置错误信息，要不根据errorCode动态设置错误数据
        mTvMessage.text = errorMessage
        Log.i("lkw", "加载失败啦")
    }

    /**
     *加载更多完成
     */
    override fun onLoadFinish(dataEmpty: Boolean, hasMore: Boolean) {
        if (!hasMore) {
            visibility = View.VISIBLE
            if (dataEmpty) {
                mProgressBar.visibility = View.GONE
                mTvMessage.visibility = View.VISIBLE
                mTvMessage.text = "暂时没有数据"
            } else {
                mProgressBar.visibility = View.GONE
                mTvMessage.visibility = View.VISIBLE
                mTvMessage.text = "没有更多的数据啦"
            }
        } else {
            visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        //为什么加后面那个判断，因为Wandroid第0页能够请求完所有数据的情况下， 再去请求第1页 也能取到值，
        // 所以这里要判断没有更多数据的时候禁止在响应点击事件了,同时在加载中时也不能触发加载更多的监听
        mLoadMoreListener.let {
            if (mTvMessage.text != "没有更多的数据啦" && mProgressBar.visibility != View.VISIBLE) {
                it?.onLoadMore()
            }
        }
    }

    fun setLoadViewColor(colorStateList: ColorStateList) {
        mProgressBar.indeterminateTintMode = PorterDuff.Mode.SRC_ATOP
        mProgressBar.indeterminateTintList = colorStateList
    }
}