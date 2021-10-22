package org.ginryan.github.slidingtab;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Sliding tab layout.
 *
 * @author GinRyan Xelloss
 */
public class NTabLayout extends HorizontalScrollView implements State, TabLayoutParent {
    private static final String TAG = "NTabLayout";
    ScrollableLayout mScrollableLayout;
    //minimum height
    int mMinHeightDp = 32;
    //current selected tab num.
    int mSelectedTabNum = 0;
    //cached NTabViews
    ArrayList<NTabView> mNTabViews = new ArrayList<>();

    public interface OnTabListener {
        void onTabItemSelected(int itemIndex);
    }

    public int getNTabViewsCount() {
        return mNTabViews.size();
    }

    LinkedList<OnTabListener> onTabListener = new LinkedList<>();

    boolean nTabUseAnimationFontScale;

    public NTabLayout addOnTabListener(OnTabListener onTabListener) {
        this.onTabListener.add(onTabListener);
        return this;
    }

    public NTabLayout removeHeadOnTabListener() {
        this.onTabListener.remove();
        return this;
    }

    public NTabLayout(Context context) {
        super(context);
        init();
    }

    public NTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    public NTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        init();
    }

    void initAttr(AttributeSet attrs) {
        TypedArray op = getContext().obtainStyledAttributes(attrs, R.styleable.NTabLayout);
        nTabUseAnimationFontScale = op.getBoolean(R.styleable.NTabLayout_nTabUseAnimationFontScale, true);
    }

    void init() {
        //setMinimumHeight
        setMinimumHeight((int) Utils.dipToPx(this, mMinHeightDp));
        mScrollableLayout = new ScrollableLayout(getContext());
        super.addView(
                mScrollableLayout,
                0,
                //have to filled by MATCH_PARENT
                new HorizontalScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            setClipToOutline(true);
//        }

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);

    }

    /**
     * Add child NTabView into ScrollableIndicatorLayout
     *
     * @param child child NTabView
     */
    @Override
    public final void addView(View child) {
        if (child instanceof NTabView) {
            mScrollableLayout.addView(child);
            addTabViewToArray((NTabView) child);
        }
    }

    /**
     * Add child NTabView into ScrollableIndicatorLayout
     *
     * @param child child NTabView
     */
    @Override
    public final void addView(View child, int index) {
        if (child instanceof NTabView) {
            mScrollableLayout.addView(child, index);
            addTabViewToArray((NTabView) child, index);
        }
    }

    /**
     * Add child NTabView into ScrollableIndicatorLayout
     *
     * @param child child NTabView
     */
    @Override
    public final void addView(View child, ViewGroup.LayoutParams params) {
        if (child instanceof NTabView) {
            mScrollableLayout.addView(child, params);
            addTabViewToArray((NTabView) child);
        }
    }

    /**
     * Add child NTabView into ScrollableIndicatorLayout
     *
     * @param child child NTabView
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
     * add tab to array
     *
     * @param nTabView
     */
    final void addTabViewToArray(NTabView nTabView) {
        nTabView.setUseAnimScale(nTabUseAnimationFontScale);
        mNTabViews.add(nTabView);
    }

    /**
     * add tab to array
     *
     * @param nTabView
     * @param index
     */
    final void addTabViewToArray(NTabView nTabView, int index) {
        nTabView.setUseAnimScale(nTabUseAnimationFontScale);
        mNTabViews.add(index, nTabView);
    }

    /**
     * remove view from array
     *
     * @param nTabView
     */
    final void removeTabViewFromArray(NTabView nTabView) {
        mNTabViews.remove(nTabView);
    }

    /**
     * remove view from array
     *
     * @param index
     */
    final void removeTabViewFromArray(int index) {
        mNTabViews.remove(index);
    }

    /**
     * change tab to index
     *
     * @param index which tab will selected.
     */
    public final void setSelectedTab(int index) {
        if (index != mSelectedTabNum) {
            notifyUpdateParent(index);
        }
    }

    /**
     * read index of ntabview by interface TabChild
     *
     * @param nTabView ntabview as TabChild
     * @return which tab selected
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

        for (int i = 0; i < onTabListener.size(); i++) {
            onTabListener.get(i).onTabItemSelected(mSelectedTabNum);
        }
    }


    /**
     *
     */
    static class ScrollableLayout extends LinearLayout implements State {
        int mSelectedTabNum = 0;

        public ScrollableLayout(Context context) {
            super(context);
        }

        public ScrollableLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ScrollableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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

}
