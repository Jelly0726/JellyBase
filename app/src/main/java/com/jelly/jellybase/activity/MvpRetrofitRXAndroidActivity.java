package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.base.applicationUtil.ToastUtils;
import com.base.http.DataBean.BankInfoGson;
import com.base.http.Mode.NetIPSourceMode;
import com.base.http.Presenter.NetIPInter;
import com.base.http.Presenter.NetIPPresenter;
import com.base.http.RetrofitApi.HttpMethods;
import com.jelly.jellybase.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/11/7.
 */

public class MvpRetrofitRXAndroidActivity extends AppCompatActivity implements NetIPInter.View{
    @BindView(R.id.query)Button query;
    @BindView(R.id.iptv)TextView iptv;
    private NetIPPresenter netIPPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mvp_retrofit_rxandroid_activity);
        // 进行绑定
        ButterKnife.bind(this);
        iniView();
        netIPPresenter=new NetIPPresenter(this, NetIPSourceMode.newInstance(this));
    }
    private void iniView(){
    }
    @OnClick(R.id.query)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.query:
                //netIPPresenter.getData(false);
                getBankInfo();
                break;
        }
    }

    @Override
    public void refresh_ok() {
        Log.i("ss","refresh_ok");
        iptv.setText(netIPPresenter.getAdapterData().get(0).getIp());
    }

    @Override
    public void refresh_error() {
        Log.i("ss","refresh_error");
    }

    @Override
    public void loadMore_ok() {
        Log.i("ss","loadMore_ok");
        iptv.setText(netIPPresenter.getAdapterData().get(0).getIp());
    }

    @Override
    public void loadMore_error() {
        Log.i("ss","loadMore_error");
    }

    @Override
    public void toastShowShort(String message) {
        ToastUtils.showToast(this,message);
    }

    @Override
    public void toastShowLong(String message) {

    }
    /***
     * 获取银行列表信息
     */
    private void getBankInfo(){
        Subscriber<List<BankInfoGson.DataBean>> subscriber = new Subscriber<List<BankInfoGson.DataBean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                toastShowShort(e.getMessage());
            }

            @Override
            public void onNext(List<BankInfoGson.DataBean> dataBeen) {
                iptv.setText(dataBeen.get(0).getBankname());
            }
        };
        HttpMethods.getInstance().getBankInfo(subscriber);
    }
}
