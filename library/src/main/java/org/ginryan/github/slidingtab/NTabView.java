package org.ginryan.github.slidingtab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class NTabView extends FrameLayout implements State, TabChild {
    public static final String TAG = "NTabView";

    public static final int DEFAULT_TITLE_SIZE_IN_SP = 18;

    //标题属性
    String nTabTitleState;
    float nTabTitleSize;
    int nTabTitleColor;
    boolean nTabTitleChecked;

    View customView;
    //计算字体大小
    float mComputeFontSize;

    //Tab被选中时的放大率
    public final float TAB_TEXT_SCALED_UP_ON_CHECKED = 1.34f;
    //boolean useTitleScaleRate = true;
    //Tab的状态是Check与否
    int mCheckState = State.STATE_CODE_UNCHECK;
    int mLastCheckState = State.STATE_CODE_UNCHECK;

    void resetTabTitleState() {
        inBoldFontState = false;
    }

    boolean inBoldFontState = false;

    Paint mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    boolean showTitle = true;
    //自己在父Tab中的位置索引
    int mItemIndexInTab = -1;

    //显示调试辅助线
    boolean showGuideline = false;

    public NTabView(@NonNull Context context) {
        super(context);

        init();
    }

    public NTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initAttrs(attrs);
        init();
    }

    public NTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(attrs);
        init();
    }


    void initAttrs(@Nullable AttributeSet attrs) {
        TypedArray op = getContext().obtainStyledAttributes(attrs, R.styleable.NTabView);

        nTabTitleState = (String) op.getText(R.styleable.NTabView_nTabTitleText);
        nTabTitleChecked = op.getBoolean(R.styleable.NTabView_nTabTitleChecked, false);
        nTabTitleSize = op.getDimensionPixelSize(R.styleable.NTabView_nTabTitleSize,
                DEFAULT_TITLE_SIZE_IN_SP);

        nTabTitleColor = op.getColor(R.styleable.NTabView_nTabTitleColor,
                Color.parseColor("#000000"));
        //useAnimScale = op.getBoolean(R.styleable.NTabView_nTabUseAnimationFontScale, true);
        op.recycle();
    }


    void init() {

        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);

        mLastCheckState = mCheckState;
        mCheckState = nTabTitleChecked ? State.STATE_CODE_CHECKED : State.STATE_CODE_UNCHECK;

        mComputeFontSize = nTabTitleSize;

        updateState(false);

        setOnClickListener(v -> {
            checkItemIndexInTab();
            getTabLayoutParent().notifyUpdateParent(mItemIndexInTab);
        });

    }

    void updatePaints() {
        mTitlePaint.setTextSize(mComputeFontSize * currentFontScaleRate);
        mTitlePaint.setColor(nTabTitleColor);
        mTitlePaint.setDither(true);
        mTitlePaint.setStyle(Paint.Style.FILL);
        mTitlePaint.setFakeBoldText(inBoldFontState);

        mLinePaint.setColor(Color.argb(255, 220, 0, 20));
        mLinePaint.setStyle(Paint.Style.FILL);
        invalidate();
        requestLayout();
    }

    public NTabView setCustomView(View customView) {
        this.customView = customView;
        if (customView != null) {
            super.addView(
                    customView,
                    0,
                    new FrameLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT));
        }
        updateState(false);
        return this;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.d(TAG, "onDraw: " + nTabTitleState);

        //1. 居中显示Tab标题
        //文本宽度
        float textWidth = mTitlePaint.measureText(nTabTitleState);
        //文本宽度的一半
        float halfTextWidth = textWidth / 2;
        //左边推进的宽度
        float leftOffset = getMeasuredWidth() / 2 - halfTextWidth;
        float textBaseline = (getMeasuredHeight() / 2 + mComputeFontSize / 2);

        if (showTitle) {
            canvas.save();
            canvas.drawText(nTabTitleState, leftOffset, textBaseline, mTitlePaint);
            canvas.restore();
        }

        float textUpperLine = (getMeasuredHeight() / 2 - mComputeFontSize / 2);
        float leftLine = leftOffset;
        float rightLine = leftOffset + textWidth;

        if (showGuideline) {
            canvas.save();
            canvas.drawLine(0, textBaseline, getMeasuredWidth(), textBaseline, mLinePaint);
            canvas.drawLine(0, textUpperLine, getMeasuredWidth(), textUpperLine, mLinePaint);
            canvas.drawLine(leftLine, 0, leftLine, getMeasuredHeight(), mLinePaint);
            canvas.drawLine(rightLine, 0, rightLine, getMeasuredHeight(), mLinePaint);
            canvas.restore();
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //不困在当前的TabView中
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            setClipToOutline(false);
        }

        updateState(false);
    }

    @Override
    public void updateState(boolean byCheck) {
        updateBy(byCheck);
    }

    /**
     * Update 函数，用于更新所有绘制内容的状态
     *
     * @param byCheck
     */
    public void updateBy(boolean byCheck) {
        if (byCheck) {
            checkItemIndexInTab();
        }
        if (mItemIndexInTab < 0) {
            //buggy 此时还不知道mItemIndexInTab
        } else {
            if (mCheckState == STATE_CODE_CHECKED) {
                Log.i(TAG, "INDEX: " + mItemIndexInTab + "--> ✔");
            } else if (mCheckState == STATE_CODE_UNCHECK) {
                Log.i(TAG, "INDEX: " + mItemIndexInTab + "--> ✖");
            }
        }
        if (mCheckState == STATE_CODE_CHECKED) {
            //FIXME inBoldFontState = true;

            if (byCheck) {

                NTabLayout parent = (NTabLayout) getParent().getParent();

                //这部分用来求出不可见部分宽度
                Rect vRect = new Rect();
                getGlobalVisibleRect(vRect);
                int visibleWidth = vRect.width();
                int measuredWidth = getMeasuredWidth();
                int invisibleWidth = measuredWidth - visibleWidth;

                //父控件宽度

                Rect parentVisibleRect = new Rect();
                parent.getGlobalVisibleRect(parentVisibleRect);
                int parentVisibleWidth = parentVisibleRect.width();

                int sign = 0;
                if (invisibleWidth > 0) {
                    /*  left                    right
                     *    |-----------+-----------|
                     *    |---------------------------|
                     *                      |---+---|    <--
                     *             |---+---|
                     * -->   |---+---|
                     *    scrollX
                     */
                    int halfParentMeasuredWidth = parentVisibleWidth / 2;
                    int middleOfThisViewOffset = getLeft() - parent.getScrollX() + getWidth() / 2;
                    int middleDelta = middleOfThisViewOffset - halfParentMeasuredWidth;

                    sign = middleDelta / Math.abs(middleDelta);
                }
                int slideTx = invisibleWidth + parentVisibleWidth / 2 - measuredWidth / 2;
                smoothScrollToMidBy(slideTx * sign);
            }
            setCurrentFontScaleRate(TAB_TEXT_SCALED_UP_ON_CHECKED);
            if (mLastCheckState == STATE_CODE_UNCHECK) {
                if (byCheck && useAnimScale) {
                    animateScaleIn();
                }
            }
        } else if (mCheckState == STATE_CODE_UNCHECK) {
            setCurrentFontScaleRate(1);
            if (mLastCheckState == STATE_CODE_CHECKED) {
                resetTabTitleState();
                if (byCheck && useAnimScale) {
                    animateScaleOut();
                }
            }
        } else {
            resetTabTitleState();
        }
        updatePaints();
    }

    //使用动画
    boolean useAnimScale = true;
    //单帧时长
    private static final float DURING_PER_FRAME = 16.67f;
    //补间动画时长(ms)
    final float animationDuring = 100;

    //目标缩放率(%)
    float mFontScaleRateLowestLimit = 1;
    //起始缩放率(%)
    float mFontScaleRateUpperLimit = 1;
    //当前缩放率(%)
    float currentFontScaleRate = 1;
    //缩放率步长
    float rateDelta = 0;

    //补间动画总帧数(fpc)
    int mTotalTweenFpc = 0;
    //当前帧编号
    int mCurrentFpc = 0;

    int sign = 1;

    private void scaleInStateInit() {
        mFontScaleRateUpperLimit = 1;
        mFontScaleRateLowestLimit = TAB_TEXT_SCALED_UP_ON_CHECKED;

        currentFontScaleRate = mFontScaleRateUpperLimit;
        sign = 1;
        computeArgs();
    }

    private void scaleOutStateInit() {
        mFontScaleRateUpperLimit = TAB_TEXT_SCALED_UP_ON_CHECKED;
        mFontScaleRateLowestLimit = 1;

        currentFontScaleRate = mFontScaleRateUpperLimit;
        sign = -1;
        computeArgs();
    }

    private void computeArgs() {
        mTotalTweenFpc = (int) (animationDuring / DURING_PER_FRAME);
        rateDelta = sign * Math.abs(mFontScaleRateUpperLimit - mFontScaleRateLowestLimit) / mTotalTweenFpc;
    }

    Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case HANDLER_VIEW_MSG_ANIM:
                postDelayed(() -> {
                    currentFontScaleRate += rateDelta;
                    mCurrentFpc++;
                    if (mCurrentFpc < mTotalTweenFpc) {
                        nextTickAnim();
                    } else {
                        //Vital!!! or scale will not work.
                        mCurrentFpc = 0;
                    }
                    updatePaints();
                    invalidate();
                }, (long) DURING_PER_FRAME);

                break;
        }
        return true;
    });
    public static final int HANDLER_VIEW_MSG_ANIM = 999100;

    private void nextTickAnim() {
        handler.sendEmptyMessage(HANDLER_VIEW_MSG_ANIM);
    }

    private void animateScaleIn() {
        Log.i(TAG, "animateScaleIn : " + mItemIndexInTab);
        scaleInStateInit();

        nextTickAnim();
    }

    private void animateScaleOut() {
        Log.i(TAG, "animateScaleOut: " + mItemIndexInTab);
        scaleOutStateInit();

        nextTickAnim();
    }


    public void setCurrentFontScaleRate(float currentFontScaleRate) {
        this.currentFontScaleRate = currentFontScaleRate;
        invalidate();
    }

    public float getCurrentFontScaleRate() {
        return currentFontScaleRate;
    }

    public NTabView setUseAnimScale(boolean useAnimScale) {
        this.useAnimScale = useAnimScale;
        return this;
    }

    public void smoothScrollToMidBy(int dx) {
        NTabLayout parent = (NTabLayout) getParent().getParent();
        parent.smoothScrollBy(dx, 0);
    }

    public void checkItemIndexInTab() {
        if (mItemIndexInTab < 0) {
            mItemIndexInTab = getTabLayoutParent().getItemIndexByView(this);
        }
    }

    @Override
    public void checkState(int stateCode) {
        mLastCheckState = mCheckState;
        mCheckState = stateCode;
        updateState(true);
    }

    public void notifyUpdateParent() {
        ViewParent parent = getParent();
        if (parent instanceof State) {
            State nTabLayout = (State) parent;
            nTabLayout.updateState(false);
        } else {
            ViewGroup con = (ViewGroup) parent;
            con.invalidate();
            con.requestLayout();
        }
    }

    public NTabView setNTabTitleSize(float nTabTitleSize) {
        this.nTabTitleSize = nTabTitleSize;
        this.mComputeFontSize = nTabTitleSize;
        invalidate();
        requestLayout();
        return this;
    }

    public float getNTabTitleSize() {
        return nTabTitleSize;
    }


    @Override
    public TabLayoutParent getTabLayoutParent() {
        ViewParent parent = getParent().getParent();
        if (parent instanceof TabLayoutParent) {
            return (TabLayoutParent) parent;
        }
        throw new RuntimeException("View Parent is not TabLayoutParent");
    }
}
