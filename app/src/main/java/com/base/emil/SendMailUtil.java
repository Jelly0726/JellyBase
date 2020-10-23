package com.base.emil;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.base.appManager.BaseApplication;
import com.jelly.jellybase.R;
import com.wenming.library.sendEmail.MailInfo;
import com.wenming.library.sendEmail.MailSender;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/4/10.
 */

public class SendMailUtil {

    //qq
    private static final String HOST = "smtp.qq.com";
    private static final String PORT = "465";//587
    private static final String FROM_ADD = "382807054@qq.com";
    private static final String FROM_PSW = "jziggdzdzwtvbghc";//zvfbqmwyygnxjgab  lbvsgxohqkbxjgci jziggdzdzwtvbghc

    //163
//    private static final String HOST = "smtp.163.com";
//    private static final String PORT = "465"; //或者465  994
//    private static final String FROM_ADD = "vicdaner@163.com";
//    private static final String FROM_PSW = "1097382492email";
    //sina
//    private static final String HOST = "smtp.sina.com";
//    private static final String PORT = "465"; //或者465  994
//    private static final String FROM_ADD = "vicdaner@sina.com";
//    private static final String FROM_PSW = "1097382492email";

    private static final String TO_ADD = "249972465@qq.com";//发到哪个邮件去
//    private static final String TO_ADD = "vicdaner@sina.com";//发到哪个邮件去

    public static void send(final File file, String toAdd){
        if (isEmail(toAdd)){
            new Throwable(new Exception("邮箱地址不正确"));
        }
        final MailInfo mailInfo = creatMail("附件",toAdd);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo,file);
            }
        }).start();
    }
    public static void send(final File file){
        final MailInfo mailInfo = creatMail("附件",TO_ADD);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo,file);
            }
        }).start();
    }


    public static void send(String toAdd){
        if (isEmail(toAdd)){
            final MailInfo mailInfo = creatMail("测试文本",toAdd);
            final MailSender sms = new MailSender();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sms.sendTextMail(mailInfo);
                }
            }).start();
        }else {
            final MailInfo mailInfo = creatMail(toAdd,TO_ADD);
            final MailSender sms = new MailSender();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sms.sendTextMail(mailInfo);
                }
            }).start();
        }

    }

    public static void send(String text,String toAdd){
        if (isEmail(toAdd)){
            new Throwable(new Exception("邮箱地址不正确"));
        }
        final MailInfo mailInfo = creatMail(text,toAdd);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendTextMail(mailInfo);
            }
        }).start();
    }

    @NonNull
    private static MailInfo creatMail(String text,String toAdd) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADD); // 你的邮箱地址
        mailInfo.setPassword(FROM_PSW);// 您的邮箱密码
        mailInfo.setFromAddress(FROM_ADD); // 发送的邮箱
        if (TextUtils.isEmpty(toAdd))
            mailInfo.setToAddress(TO_ADD);// 发到哪个邮件去
        else
        mailInfo.setToAddress(toAdd); // 发到哪个邮件去
        String title="程序【"+BaseApplication.getInstance().getString(R.string.app_name)+"】崩溃啦";
        mailInfo.setSubject(title); // 邮件主题
        mailInfo.setContent(text); // 邮件文本
        return mailInfo;
    }
    /**
     * 判断邮箱是否合法
     * @param email
     * @return
     */
    private static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
//      Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
