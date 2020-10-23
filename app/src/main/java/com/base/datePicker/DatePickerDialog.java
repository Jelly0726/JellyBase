package com.base.datePicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.Utils.MyDate;
import com.base.dialog.BaseCircleDialog;
import com.base.log.DebugLog;
import com.base.toast.ToastUtils;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.jelly.jellybase.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 日历选择-自定义
 * Created by hupei on 2017/4/5.
 */
public class DatePickerDialog extends BaseCircleDialog implements CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        CalendarView.OnYearViewChangeListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnWeekChangeListener,
        CalendarView.OnViewChangeListener,
        CalendarView.OnCalendarInterceptListener{
    private Unbinder mUnbinder;
    private View view;
    @BindView(R.id.tv_month_day)
    TextView mTextMonthDay;
    @BindView(R.id.calendarView)
    CalendarView mCalendarView;
    @BindView(R.id.calendarLayout)
    CalendarLayout mCalendarLayout;
    @BindView(R.id.previousTwo)
    ImageView previousTwo;
    @BindView(R.id.nextTwo)
    ImageView nextTwo;
    private int mYear;
    private int mMonth;
    private int mDay;

    public static DatePickerDialog getInstance() {
        DatePickerDialog dialogFragment = new DatePickerDialog();
        dialogFragment.setCanceledBack(true);
        dialogFragment.setCanceledOnTouchOutside(true);
        dialogFragment.setGravity(Gravity.CENTER);
        dialogFragment.setWidth(0.4f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        if (view==null){
            view= inflater.inflate(R.layout.date_picker_dialog, container, false);
            mUnbinder= ButterKnife.bind(this, view);
        }
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniView();
        initData();
    }
    private void iniView(){

        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearViewChangeListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnWeekChangeListener(this);
        mCalendarView.setOnViewChangeListener(this);
        //设置日期拦截事件
        mCalendarView.setOnCalendarInterceptListener(this);
        mYear = mCalendarView.getCurYear();
        mMonth = mCalendarView.getCurMonth();
        mDay = mCalendarView.getCurDay();
        mTextMonthDay.setText(mCalendarView.getCurYear() + "年"+mCalendarView.getCurMonth() + "月" );
    }
    private void initData() {



    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick({ R.id.tv_month_day,R.id.close,R.id.previous,R.id.previousTwo,R.id.next,R.id.nextTwo})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_month_day:
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextMonthDay.setText(String.valueOf(mYear));
                break;
            case R.id.close:
                dismiss();
                break;
            case R.id.previous:
                mCalendarView.scrollToPre(true);
                break;
            case R.id.previousTwo:
                mCalendarView.scrollToCalendar(mYear-1,mMonth,mDay,true);
                break;
            case R.id.next:
                mCalendarView.scrollToNext(true);
                break;
            case R.id.nextTwo:
                mCalendarView.scrollToCalendar(mYear+1,mMonth,mDay,true);
                break;
        }
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        boolean isEnable = onCalendarIntercept(calendar);//日期是否可用，没有被拦截，被拦截的可以置灰
        if (isEnable) {
            mCalendarView.scrollToCalendar(mYear,mMonth,mDay,true);
            return;
        }
        mTextMonthDay.setText(calendar.getYear() + "年" + calendar.getMonth() + "月");
        mYear = calendar.getYear();
        mMonth = calendar.getMonth();
        mDay = calendar.getDay();
        dismiss();
        if (onComplete!=null)
            onComplete.onComplete(mYear,mMonth,mDay);
    }

    @Override
    public void onYearChange(int year) {
        mYear =year;
        mTextMonthDay.setText(year + "年" + mMonth + "月");
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onMonthChange(int year, int month) {
        DebugLog.e("onMonthChange", "  -- " + year + "  --  " + month);
        mTextMonthDay.setText(year + "年" + month + "月");
        mYear = year;
        mMonth = month;
    }

    @Override
    public void onViewChange(boolean isMonthView) {
        DebugLog.e("onViewChange", "  ---  " + (isMonthView ? "月视图" : "周视图"));
    }


    @Override
    public void onWeekChange(List<Calendar> weekCalendars) {
        for (Calendar calendar : weekCalendars) {
            DebugLog.e("onWeekChange", calendar.toString());
        }
    }

    @Override
    public void onYearViewChange(boolean isClose) {
//        if (isClose) {
//            previousTwo.setVisibility(View.VISIBLE);
//            nextTwo.setVisibility(View.VISIBLE);
//        }else {
//            previousTwo.setVisibility(View.INVISIBLE);
//            nextTwo.setVisibility(View.INVISIBLE);
//        }
        DebugLog.e("onYearViewChange", "年视图 -- " + (isClose ? "关闭" : "打开"));
    }

    /**
     * 屏蔽某些不可点击的日期，可根据自己的业务自行修改
     *
     * @param calendar calendar
     * @return 是否屏蔽某些不可点击的日期，MonthView和WeekView有类似的API可调用
     */
    @Override
    public boolean onCalendarIntercept(Calendar calendar) {
        //这里写拦截条件，返回true代表拦截，尽量以最高效的代码执行
        int year = calendar.getYear();
        int month = calendar.getMonth();
        int day = calendar.getDay();
        if (year< MyDate.getYear())return true;
        if (year== MyDate.getYear()&&month<MyDate.getMonth())return true;
        if (year== MyDate.getYear()&&month==MyDate.getMonth()&&day<MyDate.getDay())return true;
        return false;
    }

    @Override
    public void onCalendarInterceptClick(Calendar calendar, boolean isClick) {
        ToastUtils.showShort(getActivity(), calendar.toString() + (isClick ? "拦截不可点击" : "拦截滚动到无效日期"));
    }

    private OnComplete onComplete;

    public OnComplete getOnComplete() {
        return onComplete;
    }

    public void setOnComplete(OnComplete onComplete) {
        this.onComplete = onComplete;
    }

    public interface OnComplete{
        public void onComplete(int year, int month, int day);
    }
}
