package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.base.emil.SendMailUtil;
import com.jelly.jellybase.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SendEmailActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendemail_activity);
        editText = (EditText) findViewById(R.id.toAddEt);
    }


    public void senTextMail(View view) {
        SendMailUtil.send(editText.getText().toString());
    }

    public void sendFileMail(View view) {

        File file = new File(Environment.getExternalStorageDirectory()+ File.separator+"test.txt");
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            String str = "hello world";
            byte[] data = str.getBytes();
            os.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (os != null)os.close();
            } catch (IOException e) {
            }
        }
        SendMailUtil.send(file,editText.getText().toString());
    }




}
