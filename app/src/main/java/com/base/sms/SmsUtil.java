package com.base.sms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.base.appManager.BaseApplication;
import com.base.log.DebugLog;
import com.base.toast.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class SmsUtil {

    /**
     * sms主要结构：
     　　_id：          短信序号，如100
     　　thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
     　　address：  发件人地址，即手机号，如+86138138000
     　　person：   发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
     　　date：       日期，long型，如1346988516，可以对日期显示格式进行设置
     　　protocol： 协议0SMS_RPOTO短信，1MMS_PROTO彩信
     　　read：      是否阅读0未读，1已读
     　　status：    短信状态-1接收，0complete,64pending,128failed
     　　type：       短信类型1是接收到的，2是已发出
     　　body：      短信具体内容
     　　service_center：短信服务中心号码编号，如+8613800755500 

     既然需要操作数据库，便少不了使用ContentResolver，所以我们应该还需要了解，短信的content uri :
     全部短信：content://sms/
     收件箱：content://sms/inbox
     发件箱：content://sms/sent
     草稿箱：content://sms/draft
     * @param context
     * @param SMS_INBOX  要获取的短信content uri  默认（content://sms/）
     * @param time       获取什么时间的短信（单位分钟）-1 不限制
     * @return
     */
    public static List<SMS> getPhoneSms(Context context, Uri SMS_INBOX, int time) {
        List<SMS> list=new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ToastUtils.showShort(context.getApplicationContext(), "缺少{Manifest.permission.READ_SMS}权限");
            DebugLog.i("ooc","缺少{Manifest.permission.READ_SMS}权限");
            return list;
        }
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ToastUtils.showShort(context.getApplicationContext(), "缺少{Manifest.permission.RECEIVE_SMS}权限");
            DebugLog.i("ooc","缺少{Manifest.permission.RECEIVE_SMS}权限");
            return list;
        }
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.RECEIVE_MMS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ToastUtils.showShort(context.getApplicationContext(), "缺少{Manifest.permission.RECEIVE_MMS}权限");
            DebugLog.i("ooc","缺少{Manifest.permission.RECEIVE_MMS}权限");
            return list;
        }
        if (SMS_INBOX==null){
            SMS_INBOX=Uri.parse("content://sms/");
        }
        ContentResolver cr = BaseApplication.getInstance().getContentResolver();
        String[] projection = new String[] {"_id", "address", "person","body", "date", "type" };
        Cursor cur;
        if (time<0){
            cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        }else {
            //获取10分钟之内的短信
            String where = " date >  "
                    + (System.currentTimeMillis() - time * 60 * 1000);
            cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        }
        if (null == cur) {
            DebugLog.i("ooc","************cur == null");
            return list;
        }
        while(cur.moveToNext()) {
            String id = cur.getString(cur.getColumnIndex("_id"));//短信编号
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));//短信内容
            Long date = cur.getLong(cur.getColumnIndex("date"));//短信时间
            //至此就获得了短信的相关的内容, 以下是把短信加入map中，构建listview,非必要。
            SMS map = new SMS();
            map.setId(id);
            map.setNumber(number);
            map.setBody(body);
            map.setDate(date);
            list.add(map);

            // 下面匹配验证码
//            Pattern pattern = Pattern.compile("\\d{6}");
//            Matcher matcher = pattern.matcher(body);
//            if (matcher.find()) {
//                String smsCodeStr = matcher.group(0);
//                DebugLog.i("fuyanan", "sms find: code=" + matcher.group(0));// 打印出匹配到的验证码
//                break;
//            }
        }
        DebugLog.i("list="+ JSON.toJSONString(list));
        return list;
    }
}
