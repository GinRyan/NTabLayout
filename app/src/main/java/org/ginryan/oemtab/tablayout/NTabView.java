package org.ginryan.oemtab.tablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ginryan.oemtab.R;

public class NTabView extends FrameLayout implements State, TabChild {
    public static final String TAG = "NTabView";

    public static final int DEFAULT_TITLE_SIZE_IN_SP = 18;

    //标题属性
    String nTabTitleState;
    float nTabTitleSize;
    int nTabTitleColor;
    boolean nTabTitleChecked;

    View customView;

    //Tab被选中时的放大率
    public float tabTitleScaleRateOnChecked = 1.34f;
    boolean useTitleScaleRate = true;
    float mTabTitleScaleRateBuf = 1;
    //Tab的状态是Check与否
    int mCheckState = State.STATE_CODE_UNCHECK;

    void resetTabTitleState() {
        mTabTitleScaleRateBuf = 1;
        useBoldFont = false;
    }

    boolean useBoldFont = false;

    Paint mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    boolean showGuideline = true;
    boolean showTitle = true;
    //自己在父Tab中的位置索引
    int mItemIndexInTab = -1;


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

        op.recycle();

//        Log.d(TAG, "nTabTitleState: " + nTabTitleState);
//        Log.d(TAG, "nTabTitleSize: " + nTabTitleSize);
//        Log.d(TAG, "nTabTitleColor: " + nTabTitleColor);
//        Log.d(TAG, "nTabTitleChecked: " + nTabTitleChecked);
    }

    void init() {
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);

        mCheckState = nTabTitleChecked ? State.STATE_CODE_CHECKED : State.STATE_CODE_UNCHECK;

        updateState(false);

        setOnClickListener(v -> {
            checkItemIndexInTab();
            getTabLayoutParent().notifyUpdateParent(mItemIndexInTab);
        });

    }

    void updatePaints() {
        mTitlePaint.setTextSize(nTabTitleSize * (useTitleScaleRate ? mTabTitleScaleRateBuf : 1));
        mTitlePaint.setColor(nTabTitleColor);
        mTitlePaint.setDither(true);
        mTitlePaint.setStyle(Paint.Style.FILL);
        mTitlePaint.setFakeBoldText(useBoldFont);

        mLinePaint.setColor(Color.argb(255, 220, 0, 20));
        mLinePaint.setStyle(Paint.Style.FILL);

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
        float textBaseline = (getMeasuredHeight() / 2 + nTabTitleSize / 2);

        if (showTitle) {
            canvas.save();
            canvas.drawText(nTabTitleState, leftOffset, textBaseline, mTitlePaint);
            canvas.restore();
        }

        float textUpperLine = (getMeasuredHeight() / 2 - nTabTitleSize / 2);
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

    public void updateBy(boolean byCheck) {
        if (byCheck) {
            checkItemIndexInTab();
        }
        if (mItemIndexInTab < 0) {
            //此时还不知道mItemIndexInTab
            // Log.i(TAG, "mItemIndexInTab: not assigned valid index.");
        } else {
            if (mCheckState == STATE_CODE_CHECKED) {
                Log.i(TAG, "INDEX: " + mItemIndexInTab + "--> ✔");
            } else if (mCheckState == STATE_CODE_UNCHECK) {
                Log.i(TAG, "INDEX: " + mItemIndexInTab + "--> ✖");
            }
        }
        if (mCheckState == STATE_CODE_CHECKED) {
            mTabTitleScaleRateBuf = tabTitleScaleRateOnChecked;
            useBoldFont = true;

            if (byCheck) {

                NTabLayout parent = (NTabLayout) getParent().getParent();

                //这部分用来求出不可见部分宽度
                Rect vRect = new Rect();
                getGlobalVisibleRect(vRect);
                int visibleWidth = vRect.width();
                int thizViewMeasuredWidth = getMeasuredWidth();
                int invisibleWidth = thizViewMeasuredWidth - visibleWidth;

//                Log.i(TAG, "Index: " + mItemIndexInTab + "  -- visibleWidth: " + visibleWidth + " -- thizViewMeasuredWidth: " + thizViewMeasuredWidth + " -- invisibleWidth: " + invisibleWidth);
                //父控件宽度

                Rect parentVisibleRect = new Rect();
                parent.getGlobalVisibleRect(parentVisibleRect);
                int parentVisibleWidth = parentVisibleRect.width();

                int parentMeasuredWidth = parent.getMeasuredWidth();

//                Log.i(TAG, "Index: " + mItemIndexInTab + "  -- Left: "
//                        + getLeft() + "  -- Right: " + getRight()
//                        + "  -- parentVisibleWidth: " + parentVisibleWidth + " -- parent Width: " + parentMeasuredWidth);

                Log.i(TAG, "MIDScroll left :[" + getLeft() + "] -- this view scrolled: x:[" + parent.getScrollX() + "] --  middle: [" + getWidth() / 2 + "]");
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
                Log.i(TAG, "Sign: " + sign);
                //滑动位移translate不如一步到位
                int slideTx = invisibleWidth + parentVisibleWidth / 2 - thizViewMeasuredWidth / 2;

                smoothScrollToMidBy(slideTx * sign);
            }

        } else if (mCheckState == STATE_CODE_UNCHECK) {
            resetTabTitleState();
        } else {
            resetTabTitleState();
        }
        updatePaints();

        invalidate();
        requestLayout();
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

    public int dip2px(int value) {
        return (int) Utils.dipToPx(this, value);
    }

    public int sp2px(int value) {
        return (int) Utils.spToPx(this, value);
    }

    public int pt2px(int value) {
        return (int) Utils.ptToPx(this, value);
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
