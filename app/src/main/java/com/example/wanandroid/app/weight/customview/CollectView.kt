package com.example.wanandroid.app.weight.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.blankj.utilcode.util.VibrateUtils

import com.example.wanandroid.R
import per.goweii.reveallayout.RevealLayout

/**
 *@ author: lkw
 *created on:2020/7/6 9:47
 *description: 自定义收藏view
 *email:lkw@mantoo.com.cn
 */
class CollectView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RevealLayout(context, attributeSet, defStyleAttr), View.OnTouchListener {

    //创建收藏view点击事件接口对象
    private var mOnCollectViewClickListener: OnCollectViewClickListener? = null

    override fun initAttr(attrs: AttributeSet?) {
        super.initAttr(attrs)
        setCheckWithExpand(true)
        setUncheckWithExpand(false)
        setCheckedLayoutId(R.layout.layout_collect_view_checked)
        setUncheckedLayoutId(R.layout.layout_collect_view_unchecked)
        setAnimDuration(300)
        setAllowRevert(true)
        setOnTouchListener(this)
    }

    /**
     * 从写点击事件方法
     */
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                //震动一下
                VibrateUtils.vibrate(40)
                mOnCollectViewClickListener?.onClick(this)
            }
        }
        return false
    }

    /**
     * 接口对象赋值
     */
    fun setOnCollectViewClickListener(onCollectViewClickListener: OnCollectViewClickListener) {
        this.mOnCollectViewClickListener = onCollectViewClickListener
    }

    //定义收藏view点击事件接口
    interface OnCollectViewClickListener {
        fun onClick(v: CollectView)
    }
}