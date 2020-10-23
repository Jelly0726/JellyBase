package com.base.Animator;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * 收起展开动画的工具类
 * 使用说明:
 * 将需要展开收缩的那部分布局的透明度在xml文件里默认设置为0
 *
 * 创建自定义的Adapter，继承自RecyclerView.Adapter，泛型传入自定义的ViewHolder，
 * 这个ViewHolder同样的继承自RecyclerView.ViewHolder，这里的ViewHolder需要实现我们工具类中的Expandable接口：
 */
public class ExpandableUtil {

    //自定义处理列表中右侧图标，这里是一个旋转动画
    public static void rotateExpandIcon(final ImageView mImage, float from, float to) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);//属性动画
            valueAnimator.setDuration(500);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mImage.setRotation((Float) valueAnimator.getAnimatedValue());
                }
            });
            valueAnimator.start();
        }
    }

    //参数介绍：1、holder对象 2、展开部分的View，由holder.getExpandView()方法获取 3、animate参数为true，则有动画效果
    public static void openHolder(final RecyclerView.ViewHolder holder, final View expandView, final boolean animate) {
        if (animate) {
            expandView.setVisibility(View.VISIBLE);
            //改变高度的动画
            final Animator animator = ViewHolderAnimator.ofItemViewHeight(holder);
            //扩展的动画，结束后透明度动画开始
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(expandView, View.ALPHA, 1);
                    alphaAnimator.addListener(new ViewHolderAnimator.ViewHolderAnimatorListener(holder));
                    alphaAnimator.start();
                }
            });
            animator.start();
        } else { //为false时直接显示
            expandView.setVisibility(View.VISIBLE);
            expandView.setAlpha(1);
        }
    }

    //类似于打开的方法
    public static void closeHolder(final RecyclerView.ViewHolder holder, final View expandView, final boolean animate) {
        if (animate) {
            expandView.setVisibility(View.GONE);
            final Animator animator = ViewHolderAnimator.ofItemViewHeight(holder);
            expandView.setVisibility(View.VISIBLE);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    expandView.setVisibility(View.GONE);
                    expandView.setAlpha(0);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    expandView.setVisibility(View.GONE);
                    expandView.setAlpha(0);
                }
            });
            animator.start();
        } else {
            expandView.setVisibility(View.GONE);
            expandView.setAlpha(0);
        }
    }

    //获取展开部分的View
    public interface Expandable {
        View getExpandView();
    }

    @SuppressWarnings("deprecation")
    public static class KeepOneHolder<VH extends RecyclerView.ViewHolder & Expandable> {
        //-1表示所有item是关闭状态，opend为pos值的表示pos位置的item为展开的状态
        private int opened = -1;

        /**
         * 此方法是在Adapter的onBindViewHolder()方法中调用
         *
         * @param holder holder对象
         * @param pos    下标
         */
        public void bind(VH holder, int pos) {
            if (pos == opened) //展开ExpandView
                ExpandableUtil.openHolder(holder, holder.getExpandView(), false);
            else //关闭ExpandView
                ExpandableUtil.closeHolder(holder, holder.getExpandView(), false);
        }

        /**
         * 响应ViewHolder的点击事件
         *
         * @param holder    holder对象
         * @param imageView 这里我传入了一个ImageView对象，为了处理图片旋转的动画，为了处理内部业务
         */
        @SuppressWarnings("unchecked")
        public void toggle(VH holder, ImageView imageView) {
            if (opened == holder.getPosition()) { //点击的就是打开的Item，则关闭item，并将opend置为-1
                opened = -1;
                ExpandableUtil.rotateExpandIcon(imageView, 180, 0);
                ExpandableUtil.closeHolder(holder, holder.getExpandView(), true);
            } else { //点击的是本来关闭的Item，则把opend值换成当前pos，把之前打开的Item给关掉
                int previous = opened;
                opened = holder.getPosition();
                ExpandableUtil.rotateExpandIcon(imageView, 0, 180);
                ExpandableUtil.openHolder(holder, holder.getExpandView(), true);
                //动画关闭之前打开的Item
                final VH oldHolder = (VH) ((RecyclerView) holder.itemView.getParent()).findViewHolderForPosition(previous);
                if (oldHolder != null)
                    ExpandableUtil.closeHolder(oldHolder, oldHolder.getExpandView(), true);
            }
        }
    }

    /**
     * 其他View的展开动画
     * @param v  展开的View
     * @param end  展开后的高度
     */
    public static void animateOpen(View v,int end) {
        int origHeight = v.getHeight();
        if (origHeight==0)//当前高度为0表示该View已隐藏需调用显示
            v.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(v, origHeight,
                end);
        animator.start();

    }

    /**
     * 其他View的收起动画
     * @param view 收起的View
     * @param end  收起后的高度 为0表示全部隐藏
     */
    public static void animateClose(final View view,final int end) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, end);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (end==0)
                    view.setVisibility(View.GONE);
            }

        });
        animator.start();
    }

    private static ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                //动态实时给View设置高度
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);

            }
        });
        return animator;
    }
}