package com.base.bottomBar;

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
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoLayoutHelper;


/**
 * @author ChayChan
 * @description: 底部tab条目
 * @date 2017/6/23  9:14
 */

public class BottomBarItem extends LinearLayout {

    private Context mContext;
    private int mIconNormalResourceId;//普通状态图标的资源id
    private int mIconSelectedResourceId;//选中状态图标的资源id
    private String mText;//文本
    private int mTextSize = 12;//文字大小 默认为12sp
    private int mTextColorNormal = 0xFF999999;    //描述文本的默认显示颜色
    private int mTextColorSelected = 0xFF46C01B;  //述文本的默认选中显示颜色
    private int mMarginTop = 0;//文字和图标的距离,默认0dp
    private boolean mOpenTouchBg = false;// 是否开启触摸背景，默认关闭
    private Drawable mTouchDrawable;//触摸时的背景
    private boolean mIsHeave = false;// 是否凸起项,默认关闭
    private boolean mOpenHeaveBg = false;// 是否开启凸起背景，默认关闭
    private Drawable mHeaveDrawable;//凸起的背景
    private int mHeaveWidth;//凸起的宽度
    private int mHeaveHeight;//凸起的高度
    private int mHeaveGravity=1;//凸起的对齐
    private int mIconWidth;//图标的宽度
    private int mIconHeight;//图标的高度
    private int mItemPadding;//BottomBarItem的padding

    private LinearLayout mLinearLayout;
    private ImageView mImageView;
    private TextView mTvUnread;
    private TextView mTvNotify;
    private TextView mTvMsg;
    private TextView mTextView;

    private int mUnreadTextSize = 10; //未读数默认字体大小10sp
    private int mMsgTextSize = 6; //消息默认字体大小6sp

    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);
    @Override
    public AutoLinearLayout.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new AutoLinearLayout.LayoutParams(getContext(), attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (!isInEditMode())
        {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public BottomBarItem(Context context) {
        this(context, null);
    }

    public BottomBarItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BottomBarItem);

        mIconNormalResourceId = ta.getResourceId(R.styleable.BottomBarItem_iconNormal, -1);
        mIconSelectedResourceId = ta.getResourceId(R.styleable.BottomBarItem_iconSelected, -1);


        mText = ta.getString(R.styleable.BottomBarItem_itemText);
        mTextSize = ta.getDimensionPixelSize(R.styleable.BottomBarItem_itemTextSize, UIUtils.sp2px(mContext,mTextSize));

        mTextColorNormal = ta.getColor(R.styleable.BottomBarItem_textColorNormal, mTextColorNormal);
        mTextColorSelected = ta.getColor(R.styleable.BottomBarItem_textColorSelected, mTextColorSelected);

        mMarginTop = ta.getDimensionPixelSize(R.styleable.BottomBarItem_itemMarginTop, UIUtils.dip2Px(mContext, mMarginTop));

        mIsHeave = ta.getBoolean(R.styleable.BottomBarItem_isHeave, mIsHeave);
        mOpenTouchBg = ta.getBoolean(R.styleable.BottomBarItem_openTouchBg, mOpenTouchBg);
        mTouchDrawable = ta.getDrawable(R.styleable.BottomBarItem_touchDrawable);

        mOpenHeaveBg = ta.getBoolean(R.styleable.BottomBarItem_openHeaveBg, mOpenHeaveBg);
        mHeaveDrawable = ta.getDrawable(R.styleable.BottomBarItem_heaveDrawable);
        mHeaveWidth = ta.getDimensionPixelSize(R.styleable.BottomBarItem_heaveWidth, 0);
        mHeaveHeight = ta.getDimensionPixelSize(R.styleable.BottomBarItem_heaveHeight, 0);
        mHeaveGravity = ta.getInt(R.styleable.BottomBarItem_heaveGravity,mHeaveGravity);

        mIconWidth = ta.getDimensionPixelSize(R.styleable.BottomBarItem_iconWidth, 0);
        mIconHeight = ta.getDimensionPixelSize(R.styleable.BottomBarItem_iconHeight, 0);
        mItemPadding = ta.getDimensionPixelSize(R.styleable.BottomBarItem_itemPadding, 0);

        mUnreadTextSize = ta.getDimensionPixelSize(R.styleable.BottomBarItem_unreadTextSize, UIUtils.sp2px(mContext,mUnreadTextSize));
        mMsgTextSize = ta.getDimensionPixelSize(R.styleable.BottomBarItem_msgTextSize, UIUtils.sp2px(mContext,mMsgTextSize));

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

        View view = View.inflate(mContext, R.layout.bottombar_item, null);
        if (mItemPadding != 0){
            //如果有设置item的padding
            view.setPadding(mItemPadding,mItemPadding,mItemPadding,mItemPadding);
        }
        mLinearLayout = (LinearLayout) view.findViewById(R.id.mLinearLayout);
        if (mHeaveWidth != 0 && mHeaveHeight != 0 &&mIsHeave){
            //如果有设置凸起的宽度和高度，则设置LinearLayout的宽高
            LayoutParams imageLayoutParams = (LayoutParams) mLinearLayout.getLayoutParams();
            imageLayoutParams.width = mHeaveWidth;
            imageLayoutParams.height = mHeaveHeight;
            if(mHeaveGravity==0){
                setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
            }
            if(mHeaveGravity==2){
                setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
            }
            mLinearLayout.setLayoutParams(imageLayoutParams);
        }
        mImageView = (ImageView) view.findViewById(R.id.iv_icon);
        mTvUnread = (TextView) view.findViewById(R.id.tv_unred_num);
        mTvMsg = (TextView) view.findViewById(R.id.tv_msg);
        mTvNotify = (TextView) view.findViewById(R.id.tv_point);
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
        mTvUnread.setTextSize(TypedValue.COMPLEX_UNIT_PX,mUnreadTextSize);//设置未读数的字体大小
        mTvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX,mMsgTextSize);//设置提示文字的字体大小

        mTextView.setTextColor(mTextColorNormal);//设置底部文字字体颜色
        mTextView.setText(mText);//设置标签文字

        LayoutParams textLayoutParams = (LayoutParams) mTextView.getLayoutParams();
        textLayoutParams.topMargin = mMarginTop;
        mTextView.setLayoutParams(textLayoutParams);

        if (mOpenTouchBg){
            //如果有开启触摸背景
            //setBackground(mTouchDrawable);
            mLinearLayout.setBackground(mTouchDrawable);
        }
        if (mOpenHeaveBg){
            //如果有开启凸起背景
            mLinearLayout.setBackground(mHeaveDrawable);
        }
        addView(view);
    }
    public boolean getIsHeave(){
        return mIsHeave;
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(mIsHeave && getLayoutParams().height==0){//使凸起的高度和宽度相等
            LayoutParams layoutParams= (LayoutParams) getLayoutParams();
            //layoutParams.height=300;
            layoutParams.height=getWidth()+ UIUtils.dip2Px(mContext, 0);
            setLayoutParams(layoutParams);
        }
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

    private void setTvVisiable(TextView tv){
        //都设置为不可见
        mTvUnread.setVisibility(GONE);
        mTvMsg.setVisibility(GONE);
        mTvNotify.setVisibility(GONE);

        tv.setVisibility(VISIBLE);//设置为可见
    }

    /**
     * 设置未读数
     * @param unreadNum 小于等于0则隐藏，大于0小于99则显示对应数字，超过99显示99+
     */
    public void setUnreadNum(int unreadNum){
        setTvVisiable(mTvUnread);
        if (unreadNum <= 0){
            mTvUnread.setVisibility(GONE);
        }else if (unreadNum <= 99){
            mTvUnread.setText(String.valueOf(unreadNum));
        }else{
            mTvUnread.setText("99+");
        }
    }
    public void setMsg(String msg){
        setTvVisiable(mTvMsg);
        mTvMsg.setText(msg);
    }

    public void hideMsg(){
        mTvMsg.setVisibility(GONE);
    }

    public void showNotify(){
        setTvVisiable(mTvNotify);
    }

    public void hideNotify(){
        mTvNotify.setVisibility(GONE);
    }
}
