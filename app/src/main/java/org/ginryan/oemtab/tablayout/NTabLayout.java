package org.ginryan.oemtab.tablayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * 横向滚动的布局
 */
public class NTabLayout extends HorizontalScrollView implements State, TabLayoutParent {
    private static final String TAG = "NTabLayout";
    ScrollableLayout mScrollableLayout;
    IndicatorView mIndicatorView;
    //最低高度
    int mMinHeightDp = 32;

    //当前被选择的Item
    int mSelectedTabNum = 0;
    //当前缓存的NTabView
    ArrayList<NTabView> mNTabViews = new ArrayList<>();


    public NTabLayout(Context context) {
        super(context);
        init();
    }

    public NTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        mIndicatorView = new IndicatorView(getContext());
        //设置最低高度
        setMinimumHeight((int) Utils.dipToPx(this, mMinHeightDp));
        mScrollableLayout = new ScrollableLayout(getContext());
        super.addView(
                mScrollableLayout,
                0,
                //必须以MATCH_PARENT填充宽高，与TabLayout形成完全相同的大小
                new HorizontalScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            //要困在当前的轮廓中
            setClipToOutline(true);
        }

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);

    }

    /**
     * 将Add进ScrollableIndicatorLayout中
     *
     * @param child
     */
    @Override
    public final void addView(View child) {
        if (child instanceof NTabView) {
            mScrollableLayout.addView(child);
            addTabViewToArray((NTabView) child);
        }
    }

    /**
     * 将Add进ScrollableIndicatorLayout中
     *
     * @param child
     */
    @Override
    public final void addView(View child, int index) {
        if (child instanceof NTabView) {
            mScrollableLayout.addView(child, index);
            addTabViewToArray((NTabView) child, index);
        }
    }

    /**
     * 将Add进ScrollableIndicatorLayout中
     *
     * @param child
     */
    @Override
    public final void addView(View child, ViewGroup.LayoutParams params) {
        if (child instanceof NTabView) {
            mScrollableLayout.addView(child, params);
            addTabViewToArray((NTabView) child);
        }
    }

    /**
     * 将Add进ScrollableIndicatorLayout中
     *
     * @param child
     */
    @Override
    public final void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof NTabView) {
            mScrollableLayout.addView(child, index, params);
            addTabViewToArray((NTabView) child, index);
        }
    }

    @Override
    public final void removeView(View view) {
        if (view instanceof NTabView) {
            super.removeView(view);
            removeTabViewFromArray((NTabView) view);
        }
    }

    @Override
    public final void removeViewAt(int index) {
        super.removeViewAt(index);
        removeTabViewFromArray(index);
    }


    /**
     * 添加Tab到数组
     *
     * @param nTabView
     */
    final void addTabViewToArray(NTabView nTabView) {
        mNTabViews.add(nTabView);
    }

    /**
     * 添加TabView到数组
     *
     * @param nTabView
     * @param index
     */
    final void addTabViewToArray(NTabView nTabView, int index) {
        mNTabViews.add(index, nTabView);
    }

    /**
     * 移除
     *
     * @param nTabView
     */
    final void removeTabViewFromArray(NTabView nTabView) {
        mNTabViews.remove(nTabView);
    }

    /**
     * 移除
     *
     * @param index
     */
    final void removeTabViewFromArray(int index) {
        mNTabViews.remove(index);
    }

    /**
     * 选择Tab变更Tab
     *
     * @param index
     */
    public final void setSelectedTab(int index) {
        this.mSelectedTabNum = index;
        updateState(false);
    }

    /**
     * 通过View读取ItemIndex值
     *
     * @param nTabView
     * @return
     */
    @Override
    public int getItemIndexByView(TabChild nTabView) {
        return mNTabViews.indexOf(nTabView);
    }

    @Override
    public void updateState(boolean byCheck) {
        for (int i = 0; i < mNTabViews.size(); i++) {
            mNTabViews.get(i).checkState(i == mSelectedTabNum ? STATE_CODE_CHECKED : STATE_CODE_UNCHECK);
        }
        mIndicatorView.updateState(byCheck);
        mScrollableLayout.updateState(byCheck);
        invalidate();
        requestLayout();
    }

    @Override
    public void checkState(int stateCode) {
        // do nothing.
    }

    @Override
    public void notifyUpdateParent(int item) {
        Log.i(TAG, "MItem Index: " + item);
        mSelectedTabNum = item;
        updateState(true);
    }

    /**
     * 将在ScrollableIndicatorLayout中显示Tab内容以及背景指示器
     */
    class ScrollableLayout extends LinearLayout implements State {
        int mSelectedTabNum = 0;

        public ScrollableLayout(Context context) {
            super(context);
        }

        public ScrollableLayout(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public ScrollableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setWillNotDraw(false);
        }

        public ScrollableLayout setSelectedTabNum(int selectedTabNum) {
            this.mSelectedTabNum = selectedTabNum;

            return this;
        }

        @Override
        public void updateState(boolean byCheck) {

        }

        @Override
        public void checkState(int stateCode) {

        }

    }

    /**
     * 指示条动画View
     */
    class IndicatorView extends FrameLayout implements State {
        Drawable mBackground;

        public IndicatorView(@NonNull Context context) {
            super(context);
        }

        public IndicatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public IndicatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setWillNotDraw(false);
        }

        public IndicatorView setIndicatorBackground(Drawable background) {
            this.mBackground = background;
            return this;
        }

        @Override
        public void updateState(boolean byCheck) {
            //invalidate();
        }

        @Override
        public void checkState(int stateCode) {

        }

    }
}
