package com.jelly.baselibrary.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelly.baselibrary.R;
import com.mylhyl.circledialog.AbsBaseCircleDialog;


/**
 * 注销框-自定义
 * Created by hupei on 2017/4/5.
 */
public class DialogLogout extends AbsBaseCircleDialog implements View.OnClickListener {

    public static DialogLogout getInstance() {
        DialogLogout dialogFragment = new DialogLogout();
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        return dialogFragment;
    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.circle_dialog_logout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        view.findViewById(R.id.but_cancle).setOnClickListener(this);
        view.findViewById(R.id.logout_ok).setOnClickListener(this);
        view.findViewById(R.id.logout_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout_ok) {
            //注销逻辑
        } else {
            dismiss();
        }
    }

}
