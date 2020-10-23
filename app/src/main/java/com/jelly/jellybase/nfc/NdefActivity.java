package com.jelly.jellybase.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jelly.jellybase.R;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by gyg on 2017/6/5.
 */
public class NdefActivity extends AppCompatActivity implements View.OnClickListener{

    EditText writeEdt,readEdt;

    protected NfcAdapter mNfcAdapter;//nfc适配器对象
    protected PendingIntent mPendingIntent;//延迟Intent
    protected Tag mTag;//nfc标签对象
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_ndef_activity);
        writeEdt= (EditText) findViewById(R.id.write_data);
        readEdt= (EditText) findViewById(R.id.read_data);
        findViewById(R.id.write_bn).setOnClickListener(this);
        findViewById(R.id.read_bn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.write_bn://写数据
                writeNdef();
                break;
            case R.id.read_bn://读数据
                readNdef();
                break;
        }
    }
    //启动activity,界面可见时
    @Override
    protected void onStart() {
        super.onStart();
        mNfcAdapter= NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter==null){//判断设备是否支持NFC功能
            Toast.makeText(this,"设备不支持NFC功能!",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()){//判断设备NFC功能是否打开
            Toast.makeText(this,"请到系统设置中打开NFC功能!",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mPendingIntent= PendingIntent.getActivity(this,0,new Intent(this,getClass()),0);//创建PendingIntent对象,当检测到一个Tag标签就会执行此Intent
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mTag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);//获取到Tag标签对象
        String[] techList=mTag.getTechList();
        System.out.println("标签支持的tachnology类型：");
        for (String tech:techList){
            System.out.println(tech);
        }
    }

    //页面获取到焦点
    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter!=null){
            mNfcAdapter.enableForegroundDispatch(this,mPendingIntent,null,null);//打开前台发布系统，使页面优于其它nfc处理.当检测到一个Tag标签就会执行mPendingItent
        }
    }

    //页面失去焦点
    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter!=null){
            mNfcAdapter.disableForegroundDispatch(this);//关闭前台发布系统
        }
    }
    //往Ndef标签中写数据
    private void writeNdef(){
        if (mTag==null){
            Toast.makeText(this,"不能识别的标签类型！",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Ndef ndef=Ndef.get(mTag);//获取ndef对象
        if (!ndef.isWritable()){
            Toast.makeText(this,"该标签不能写入数据!",Toast.LENGTH_SHORT).show();
            return;
        }
        NdefRecord ndefRecord=createTextRecord(writeEdt.getText().toString());//创建一个NdefRecord对象
        NdefMessage ndefMessage=new NdefMessage(new NdefRecord[]{ndefRecord});//根据NdefRecord数组，创建一个NdefMessage对象
        int size=ndefMessage.getByteArrayLength();
        if (ndef.getMaxSize()<size){
            Toast.makeText(this,"标签容量不足！",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ndef.connect();//连接
            ndef.writeNdefMessage(ndefMessage);
            Toast.makeText(this,"数据写入成功！",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }finally {
            try {
                ndef.close();//关闭连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建NDEF文本数据
     * @param text
     * @return
     */
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        //将文本转换为UTF-8格式
        byte[] textBytes = text.getBytes(utfEncoding);
        //设置状态字节编码最高位数为0
        int utfBit = 0;
        //定义状态字节
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //设置第一个状态字节，先将状态码转换成字节
        data[0] = (byte) status;
        //设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
        //到textBytes.length的位置
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        //通过字节传入NdefRecord对象
        //NdefRecord.RTD_TEXT：传入类型 读写
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }

    //读取Ndef标签中数据
    private void readNdef(){
        if (mTag==null){
            Toast.makeText(this,"不能识别的标签类型！",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        System.out.println("SSSS  mTag="+mTag);
        Ndef ndef=Ndef.get(mTag);//获取ndef对象
        if (ndef==null){
            Toast.makeText(this,"不能识别！",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        try {
            ndef.connect();
            NdefMessage ndefMessage=ndef.getNdefMessage();
            if (ndefMessage!=null)
                readEdt.setText(parseTextRecord(ndefMessage.getRecords()[0]));
            Toast.makeText(this,"数据读取成功！",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            System.out.println("SSSS1="+e.getMessage());
        } catch (FormatException e) {
            System.out.println("SSSS2="+e.getMessage());
        }finally {
            try {
                if (ndef!=null)
                ndef.close();//关闭链接
            } catch (IOException e) {
                System.out.println("SSSS2="+e.getMessage());
            }
        }
    }

    /**
     * 解析NDEF文本数据，从第三个字节开始，后面的文本数据
     * @param ndefRecord
     * @return
     */
    public static String parseTextRecord(NdefRecord ndefRecord) {
        /**
         * 判断数据是否为NDEF格式
         */
        //判断TNF
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        //判断可变的长度的类型
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }
        try {
            //获得字节数组，然后进行分析
            byte[] payload = ndefRecord.getPayload();
            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            int languageCodeLength = payload[0] & 0x3f;
            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            //下面开始NDEF文本数据后面的字节，解析出文本
            String textRecord = new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }
}
