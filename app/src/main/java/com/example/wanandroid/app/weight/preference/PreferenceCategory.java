package com.example.wanandroid.app.weight.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceViewHolder;

import com.example.wanandroid.R;
import com.example.wanandroid.app.utils.SettingUtil;

/**
 * @ author: lkw
 * created on:2020/7/17 11:10
 * description:自定义PreferenceCategory 拓展了一下，可以设置标题颜色
 * email:lkw@mantoo.com.cn
 */
public class PreferenceCategory extends PreferenceGroup {
    TextView titleView;

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("RestrictedApi")
    public PreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.preferenceCategoryStyle, android.R.attr.preferenceCategoryStyle));
    }

    public PreferenceCategory(Context context) {
        this(context, null);

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean shouldDisableDependents() {
        return !super.isEnabled();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            holder.itemView.setAccessibilityHeading(true);
            titleView = (TextView) holder.findViewById(android.R.id.title);
            if (titleView == null) {
                return;
            }
            titleView.setTextColor(SettingUtil.INSTANCE.getColor(getContext()));
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            final TypedValue value = new TypedValue();
            if (!getContext().getTheme().resolveAttribute(R.attr.colorAccent, value, true)) {
                return;
            }
            titleView = (TextView) holder.findViewById(android.R.id.title);
            if (titleView == null) {
                return;
            }
            final int fallBackColor = ContextCompat.getColor(getContext(), R.color.preference_fallback_accent_color);
            if (titleView.getCurrentTextColor() != fallBackColor) {
                return;
            }
            titleView.setTextColor(SettingUtil.INSTANCE.getColor(getContext()));
        }
    }

    public void setTitleColor(int color) {
        if (titleView != null) {
            titleView.setTextColor(color);
        }
    }

    @Deprecated
    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            AccessibilityNodeInfoCompat.CollectionItemInfoCompat existingItemInfo = info.getCollectionItemInfo();
            if (existingItemInfo == null) {
                return;
            }

            final AccessibilityNodeInfoCompat.CollectionItemInfoCompat newItemInfo = AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(
                    existingItemInfo.getRowIndex(),
                    existingItemInfo.getRowSpan(),
                    existingItemInfo.getColumnIndex(),
                    existingItemInfo.getColumnSpan(),
                    existingItemInfo.isSelected()
            );
            info.setCollectionInfo(newItemInfo);
        }
    }
}
