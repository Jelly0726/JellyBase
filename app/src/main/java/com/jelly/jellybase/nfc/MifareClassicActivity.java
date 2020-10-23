package com.jelly.jellybase.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jelly.jellybase.R;

import java.io.IOException;

/**
 * Created by gyg on 2017/6/5.
 */
public class MifareClassicActivity extends AppCompatActivity implements View.OnClickListener{
    private NfcAdapter mNfcAdapter;//nfc适配器对象
    private PendingIntent mPendingIntent;//延迟Intent
    private Tag mTag;//nfc标签对象

    private EditText sectorNum;//扇区
    private EditText blockNum;//块
    private EditText writeData;//写入数据
    private EditText readData;//读出的数据

    private boolean haveMifareClissic=false;//标签是否支持MifareClassic

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_write_mifareclassic_activity);
        sectorNum= (EditText) findViewById(R.id.sector_num);
        blockNum= (EditText) findViewById(R.id.block_num);
        writeData= (EditText) findViewById(R.id.write_data);
        readData= (EditText) findViewById(R.id.read_data);
        findViewById(R.id.write_bn).setOnClickListener(this);
        findViewById(R.id.read_bn).setOnClickListener(this);
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
        mPendingIntent=PendingIntent.getActivity(this,0,new Intent(this,getClass()),0);//创建PendingIntent对象,当检测到一个Tag标签就会执行此Intent
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mTag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);//获取到Tag标签对象
        String[] techList=mTag.getTechList();
        System.out.println("标签支持的tachnology类型：");
        for (String tech:techList){
            System.out.println(tech);
            if (tech.indexOf("MifareClassic")>0){
                haveMifareClissic=true;
            }
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.write_bn://写块
                writeBlock();
                break;
            case R.id.read_bn://读块
                readBlock();
                break;
        }
    }

    //写块
    private void writeBlock(){
        if (mTag==null){
            Toast.makeText(this,"无法识别的标签！",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!haveMifareClissic){
            Toast.makeText(this,"不支持MifareClassic",Toast.LENGTH_SHORT).show();
           finish();
            return;
        }
        MifareClassic mfc=MifareClassic.get(mTag);
        try {
            mfc.connect();//打开连接
            boolean auth;
            int sector=Integer.parseInt(sectorNum.getText().toString().trim());//写入的扇区
            int block=Integer.parseInt(blockNum.getText().toString().trim());//写入的块区
            auth=mfc.authenticateSectorWithKeyA(sector,MifareClassic.KEY_DEFAULT);//keyA验证扇区
            if (auth){
                mfc.writeBlock(block,"0123456789012345".getBytes());//写入数据
                Toast.makeText(this,"写入成功!",Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                mfc.close();//关闭连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //读取块
    private void readBlock(){
        if (mTag==null){
            Toast.makeText(this,"无法识别的标签！",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!haveMifareClissic){
            Toast.makeText(this,"不支持MifareClassic",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        MifareClassic mfc=MifareClassic.get(mTag);
        try {
            mfc.connect();//打开连接
            boolean auth;
            int sector=Integer.parseInt(sectorNum.getText().toString().trim());//写入的扇区
            int block=Integer.parseInt(blockNum.getText().toString().trim());//写入的块区
            auth=mfc.authenticateSectorWithKeyA(sector,MifareClassic.KEY_DEFAULT);//keyA验证扇区
            if (auth){
                readData.setText(bytesToHexString(mfc.readBlock(block)));
            }
        } catch (IOException e) {
            System.out.println("SSSS1="+e.getMessage());
        }finally {
            try {
                mfc.close();//关闭连接
            } catch (IOException e) {
                System.out.println("SSSS2="+e.getMessage());
            }
        }
    }

    //字符序列转换为16进制字符串
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }
}
