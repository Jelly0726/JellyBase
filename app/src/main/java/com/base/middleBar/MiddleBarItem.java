package com.base.middleBar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jelly.jellybase.R;


/**
 * @author ChayChan
 * @description: 顶部tab条目
 * @date 2017/6/23  9:14
 */

public class MiddleBarItem extends LinearLayout {

    private Context mContext;
    private int mOrientation=0;//图标与文字的布局 0 水平 1垂直
    private int mIconNormalResourceId;//普通状态图标的资源id
    private int mIconSelectedResourceId;//选中状态图标的资源id
    private String mText;//文本
    private int mItemHeight = 45;//item的高度 默认为45dp
    private int mTextSize = 12;//文字大小 默认为12sp
    private int mTextColorNormal = 0xFF999999;    //描述文本的默认显示颜色
    private int mTextColorSelected = 0xFF46C01B;  //述文本的默认选中显示颜色
    private boolean mOpenTouchBg = false;// 是否开启触摸背景，默认关闭
    private Drawable mTouchDrawable;//触摸时的背景
    private int mIconWidth;//图标的宽度
    private int mIconHeight;//图标的高度
    private int mItemPadding;//BottomBarItem的padding

    private LinearLayout mLinearLayout;
    private ImageView mImageView;
    private TextView mTextView;

    public MiddleBarItem(Context context) {
        this(context, null);
    }

    public MiddleBarItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiddleBarItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MiddleBarItem);

        mOrientation = ta.getInt(R.styleable.MiddleBarItem_mOrientation,mOrientation);
        mIconNormalResourceId = ta.getResourceId(R.styleable.MiddleBarItem_mIconNormal, -1);
        mIconSelectedResourceId = ta.getResourceId(R.styleable.MiddleBarItem_mIconSelected, -1);

        mItemHeight = ta.getDimensionPixelSize(R.styleable.MiddleBarItem_mItemHeight,
                UIUtils.sp2px(mContext,mItemHeight));

        mText = ta.getString(R.styleable.MiddleBarItem_mItemText);
        mTextSize = ta.getDimensionPixelSize(R.styleable.MiddleBarItem_mItemTextSize, UIUtils.sp2px(mContext,mTextSize));

        mTextColorNormal = ta.getColor(R.styleable.MiddleBarItem_mTextColorNormal, mTextColorNormal);
        mTextColorSelected = ta.getColor(R.styleable.MiddleBarItem_mTextColorSelected, mTextColorSelected);

        mOpenTouchBg = ta.getBoolean(R.styleable.MiddleBarItem_mOpenTouchBg, mOpenTouchBg);
        mTouchDrawable = ta.getDrawable(R.styleable.MiddleBarItem_mTouchDrawable);

        mIconWidth = ta.getDimensionPixelSize(R.styleable.MiddleBarItem_mIconWidth, 0);
        mIconHeight = ta.getDimensionPixelSize(R.styleable.MiddleBarItem_mIconHeight, 0);
        mItemPadding = ta.getDimensionPixelSize(R.styleable.MiddleBarItem_mItemPadding, 0);


        ta.recycle();

        checkValues();
        init();
    }

    /**
     * 检查传入的值是否完善
     */
    private void checkValues() {
        if (mIconNormalResourceId == -1) {
            throw new IllegalStateException("您还没有设置默认状态下的图标，请指定iconNormal的图标");
        }

        if (mIconSelectedResourceId == -1) {
            throw new IllegalStateException("您还没有设置选中状态下的图标，请指定iconSelected的图标");
        }

        if (mOpenTouchBg && mTouchDrawable == null){
            //如果有开启触摸背景效果但是没有传对应的drawable
            throw new IllegalStateException("开启了触摸效果，但是没有指定touchDrawable");
        }
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        View view = View.inflate(mContext, R.layout.middlebar_item_v, null);
        if (mOrientation==1){
            view = View.inflate(mContext, R.layout.middlebar_item_v, null);
        }
        if (mItemPadding != 0){
            //如果有设置item的padding
            view.setPadding(mItemPadding,mItemPadding,mItemPadding,mItemPadding);
        }
        mLinearLayout = (LinearLayout) view.findViewById(R.id.mLinearLayout);

        mImageView = (ImageView) view.findViewById(R.id.iv_icon);
        mTextView = (TextView) view.findViewById(R.id.tv_text);

        mImageView.setImageResource(mIconNormalResourceId);

        if (mIconWidth != 0 && mIconHeight != 0){
            //如果有设置图标的宽度和高度，则设置ImageView的宽高
            FrameLayout.LayoutParams imageLayoutParams = (FrameLayout.LayoutParams) mImageView.getLayoutParams();
            imageLayoutParams.width = mIconWidth;
            imageLayoutParams.height = mIconHeight;
            mImageView.setLayoutParams(imageLayoutParams);
        }

        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);//设置底部文字字体大小
        mTextView.setTextColor(mTextColorNormal);//设置文字字体颜色
        mTextView.setText(mText);//设置标签文字

        if (mOpenTouchBg){
            //如果有开启触摸背景
            //setBackground(mTouchDrawable);
            mLinearLayout.setBackground(mTouchDrawable);
        }
        //设置Item的高度
        LayoutParams imageLayoutParams = (LayoutParams) mLinearLayout.getLayoutParams();
        imageLayoutParams.height = mItemHeight;
        mLinearLayout.setLayoutParams(imageLayoutParams);

        addView(view);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setIconNormalResourceId(int mIconNormalResourceId) {
        this.mIconNormalResourceId = mIconNormalResourceId;
    }

    public void setIconSelectedResourceId(int mIconSelectedResourceId) {
        this.mIconSelectedResourceId = mIconSelectedResourceId;
    }

    public void setStatus(boolean isSelected){
        mImageView.setImageResource(isSelected?mIconSelectedResourceId:mIconNormalResourceId);
        mTextView.setTextColor(isSelected?mTextColorSelected:mTextColorNormal);
    }
}
