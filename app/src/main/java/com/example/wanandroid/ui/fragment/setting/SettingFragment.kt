package com.example.wanandroid.ui.fragment.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ColorUtils
import com.example.wanandroid.R
import com.example.wanandroid.app.event.AppViewModel
import com.example.wanandroid.app.ext.initClose
import com.example.wanandroid.app.ext.showMessage
import com.example.wanandroid.app.network.NetWorkApi
import com.example.wanandroid.app.utils.CacheDataManager
import com.example.wanandroid.app.utils.CacheUtil
import com.example.wanandroid.app.utils.ColorUtil
import com.example.wanandroid.app.utils.SettingUtil
import com.example.wanandroid.app.weight.preference.CheckBoxPreference
import com.example.wanandroid.app.weight.preference.IconPreference
import com.example.wanandroid.app.weight.preference.PreferenceCategory
import com.example.wanandroid.data.model.bean.BannerResponse
import com.tencent.bugly.beta.Beta
import me.hgj.jetpackmvvm.ext.getAppViewModel
import me.hgj.jetpackmvvm.ext.nav

/**
 *@ author: lkw
 *created on:2020/7/16 16:34
 *description: 系统设置页面
 *email:lkw@mantoo.com.cn
 */
class SettingFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var toolbar: Toolbar? = null

    //这里不能继承BaseFragment,s所以需要手动获取一下AppViewModel
    val shareViewModel: AppViewModel by lazy { getAppViewModel<AppViewModel>() }
    private var colorPreview: IconPreference? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //这里重写根据PreferenceFragmentCompat 的布局 ，往他的根布局插入了一个toolbar
        val containerView = view.findViewById<FrameLayout>(android.R.id.list_container)
        containerView.let {
            //转为线性布局
            val linearLayout = it.parent as? LinearLayout
            linearLayout?.run {
                val toolbarView =
                    LayoutInflater.from(activity).inflate(R.layout.include_toolbar, null)
                toolbar = toolbarView.findViewById(R.id.toolbar)
                toolbar?.initClose("设置") { toolbar ->
                    nav().navigateUp()
                }
                //添加到第一个
                addView(toolbarView, 0)
            }
        }
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.root_preferences)
        colorPreview = findPreference("color")
        setText()
        findPreference<Preference>("exit")?.isVisible = shareViewModel.isLogin.value
        findPreference<Preference>("exit")?.setOnPreferenceClickListener { preference ->
            showMessage(
                "确定退出登录",
                positiveButtonText = "退出",
                negativeButtonText = "取消",
                postiveAction = {
                    //清空cookie
                    NetWorkApi().cookieJar.clear()
                    shareViewModel.userInfo.postValue(null)
                    CacheUtil.setUser(null)
                    shareViewModel.isLogin.postValue(false)
                    view?.let {
                        nav().navigateUp()
                    }
                })
            false
        }

        findPreference<Preference>("clearCache")?.setOnPreferenceClickListener {
            showMessage(
                "确定清除缓存吗",
                positiveButtonText = "确定",
                negativeButtonText = "取消",
                postiveAction = {
                    activity?.let {
                        CacheDataManager.clearAllCache(it as? AppCompatActivity)
                        setText()
                    }
                })
            false
        }

        findPreference<Preference>("mode")?.setOnPreferenceClickListener {
            activity?.let { activity ->
                MaterialDialog(activity).show {
                    cancelable(false)
                    lifecycleOwner()
                    listItemsSingleChoice(
                        R.array.settings_modes
                        , initialSelection = SettingUtil.getListMode()
                    ) { dialog, index, text ->
                        SettingUtil.setListMode(index)
                        it.summary = text
                        //通知其他页面立即修改配置
                        shareViewModel.appAnimation.postValue(index)
                    }
                    title(text = "设置列表动画")
                    positiveButton(R.string.confirm)
                    negativeButton(R.string.cancel)
                    getActionButton(WhichButton.POSITIVE).updateTextColor(
                        SettingUtil.getColor(activity)
                    )
                    getActionButton(WhichButton.NEGATIVE).updateTextColor(
                        SettingUtil.getColor(activity)
                    )
                }
            }
            false
        }
        findPreference<IconPreference>("color")?.setOnPreferenceClickListener {
            activity?.let { activity ->
                MaterialDialog(activity).show {
                    title(R.string.choose_theme_color)
                    colorChooser(
                        ColorUtil.ACCENT_COLORS,
                        initialSelection = SettingUtil.getColor(activity),
                        subColors = ColorUtil.PRIMARY_COLORS_SUB
                    ) { dialog, color ->
                        //修改颜色
                        SettingUtil.setColor(activity, color)
                        findPreference<PreferenceCategory>("base")?.setTitleColor(color)
                        findPreference<PreferenceCategory>("other")?.setTitleColor(color)
                        findPreference<PreferenceCategory>("about")?.setTitleColor(color)
                        findPreference<CheckBoxPreference>("top")?.setBottonColor()
                        toolbar?.setBackgroundColor(color)
                        //通知其他页面立即修改颜色
                        shareViewModel.appColor.postValue(color)
                    }
                    getActionButton(WhichButton.POSITIVE).updateTextColor(
                        SettingUtil.getColor(activity)
                    )
                    getActionButton(WhichButton.NEGATIVE).updateTextColor(
                        SettingUtil.getColor(activity)
                    )
                    positiveButton(R.string.confirm)
                    negativeButton(R.string.cancel)
                }
            }
            false
        }

        findPreference<Preference>("version")?.setOnPreferenceClickListener {
            Beta.checkUpgrade(true, false)
            false
        }
        findPreference<Preference>("copyRight")?.setOnPreferenceClickListener {
            activity?.let {
                showMessage(it.getString(R.string.copyright_tip))
            }
            false
        }
        findPreference<Preference>("author")?.setOnPreferenceClickListener {
            showMessage(
                title = "联系作者",
                message = "扣　扣：2314861690\n" +
                        "\n" +
                        "微　信：lkw231486\n" +
                        "\n" +
                        "邮　箱：2314861690@qq.com"
            )
            false
        }
        findPreference<Preference>("project")?.setOnPreferenceClickListener {
               val date=BannerResponse(
                   title = "一位练习时长一年半的实习生制作的玩转安卓App",
                   url =findPreference<Preference>("project")?.summary.toString()
               )
              view?.let {
                  nav().navigate(R.id.action_settingFragment_to_webFragment,Bundle().apply { putParcelable("bannerdata",date) })
              }
            false
        }
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "color") {
            colorPreview?.setView()
        }
        if (key == "top") {
            CacheUtil.setIsNeedTop(sharedPreferences.getBoolean("top", true))
        }
    }

    /**
     * 初始化设值
     */
    private fun setText() {
        activity?.let {
            findPreference<CheckBoxPreference>("top")?.isChecked = CacheUtil.isNeedTop()

            findPreference<Preference>("clearCache")?.summary =
                CacheDataManager.getTotalCacheSize(it)
            findPreference<Preference>("version")?.summary = "当前版本" + AppUtils.getAppVersionName()
            val modes = it.resources.getStringArray(R.array.settings_modes)
            findPreference<Preference>("mode")?.summary = modes[SettingUtil.getListMode()]
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

}