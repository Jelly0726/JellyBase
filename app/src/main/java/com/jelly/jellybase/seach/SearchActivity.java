package com.jelly.jellybase.seach;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.base.BaseApplication;
import com.base.sqldao.HistoryDaoUtils;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.multiClick.OnMultiClickListener;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.SearchActivityBinding;

import java.util.List;

import systemdb.SearchHistory;

/**
 * Created by Administrator on 2017/9/28.
 */

public class SearchActivity extends BaseActivity<SearchActivityBinding> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
        iniHistory();
    }
    private void iniView(){
        getBinding().cancelTv.setOnClickListener(listener);

        //监听键盘搜索按钮
        getBinding().searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String search= getBinding().searchEdit.getText().toString().trim();
                    if(search.length()>0) {
                        SearchHistory history = new SearchHistory();
                        history.setTime(System.currentTimeMillis());
                        history.setHistory(search);
                        HistoryDaoUtils.getInstance(BaseApplication.getInstance()).addTable(history);
                    }
                    setBackData(search);
                    return true;
                }
                return false;
            }
        });
        String search=getIntent().getStringExtra("search");
        if(search!=null){
            getBinding().searchEdit.setText(search);
            getBinding().searchEdit.setSelection(search.length());
        }

        getBinding().clearHistory.setOnClickListener(listener);
    }
    /**
     * 初始化历史搜索
     * */
    private void iniHistory(){
        if(getBinding().searchHistory.getChildCount()>0){
            getBinding().searchHistory.removeAllViewsInLayout();
        }
        List<SearchHistory> historyList= HistoryDaoUtils.getInstance(BaseApplication.getInstance()).getAllList();
        if(historyList!=null){
            for (int i = 0; i < historyList.size(); i++) {
                TextView tv = (TextView) getLayoutInflater().inflate(
                        R.layout.search_history_tv, getBinding().searchHistory, false);
                tv.setText(historyList.get(i).getHistory());
                tv.setTag(historyList.get(i));
                //点击事件
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchHistory history= (SearchHistory) v.getTag();

                        history.setTime(System.currentTimeMillis());
                        HistoryDaoUtils.getInstance(BaseApplication.getInstance()).update(history);

                        setBackData(history.getHistory());
                    }
                });
                getBinding().searchHistory.addView(tv);
            }
        }
    }
    private void setBackData(String search){
        Intent intent=new Intent(this,SearchResultActivity.class);
        intent.putExtra("search",search);
        //setResult(getIntent().getIntExtra("requestCode",-1),intent);
        startActivity(intent);
        finish();
    }
    private void clearHistory(){
        HistoryDaoUtils.getInstance(BaseApplication.getInstance()).clear();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                iniHistory();
                ToastUtils.show(SearchActivity.this,"清除成功！");
            }
        },1000);

    }
    private OnMultiClickListener listener=new OnMultiClickListener() {
        @Override
        public void onMultiClick(View v) {
            switch (v.getId()){
                case R.id.cancel_tv:
                    finish();
                    break;
                case R.id.clear_history:
                    clearHistory();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

}
