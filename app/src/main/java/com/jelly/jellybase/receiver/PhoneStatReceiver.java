package com.jelly.jellybase.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 0�绰���ؼ�������˾�����ڿ�ʼ����ʱ������������
 * @author Administrator
 *
 */
public class PhoneStatReceiver extends BroadcastReceiver {

  String TAG = "tag";
  TelephonyManager telMgr;
  //SharePreferenceUtil util;
  @Override
  public void onReceive(Context context, Intent intent) {
      telMgr = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
    //  util=new SharePreferenceUtil(context.getApplicationContext(), Constants.SAVE_USER);
      switch (telMgr.getCallState()) {
          case TelephonyManager.CALL_STATE_RINGING:
              String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
              //Log.v(TAG, "number:" + number);
              //if (util.getIsBegin()) {
                  SharedPreferences phonenumSP = context.getSharedPreferences("in_phone_num", Context.MODE_PRIVATE);
                  Editor editor = phonenumSP.edit();
                  editor.putString(number,number);
                  editor.commit();
                 // endCall();
             // }
              break;
          case TelephonyManager.CALL_STATE_OFFHOOK:
              break;
          case TelephonyManager.CALL_STATE_IDLE:
              break;
      }

  }
  /**
   * �Ҷϵ绰
   */
  private void endCall()
  {
      Class<TelephonyManager> c = TelephonyManager.class;
      try
      {
          Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
          getITelephonyMethod.setAccessible(true);
          ITelephony iTelephony = null;
          //Log.e(TAG, "End call.");
          iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr, (Object[]) null);
          iTelephony.endCall();
      }
      catch (Exception e)
      {
          //Log.e(TAG, "Fail to answer ring call.", e);
      }
  }
  private ArrayList<String> getPhoneNum(Context context) {
      ArrayList<String> numList = new ArrayList<String>();
      //�õ�ContentResolver����
      ContentResolver cr = context.getContentResolver();
      //ȡ�õ绰���п�ʼһ��Ĺ��
      Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
      while (cursor.moveToNext())
      {
          // ȡ����ϵ��ID
          String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
          Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                  ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
          // ȡ�õ绰����(���ܴ��ڶ������)
          while (phone.moveToNext())
          {
              String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
              numList.add(strPhoneNumber);
              //Log.v("tag", "strPhoneNumber:" + strPhoneNumber);
          }

          phone.close();
      }
      cursor.close();
      return numList;
  }
}