package vite.demo;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vite.base64.Base64;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText inputori;
    private Button btn_copy_sign;
    private Button btn_past_sign;
    private TextView text_result;
    private Button btn_en;
    private Button btn_den;

    private ClipboardManager mClipboardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mClipboardManager =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

        inputori=(EditText)findViewById(R.id.input_ori);
        btn_copy_sign=(Button)findViewById(R.id.btn_copy_sign);
        btn_past_sign=(Button)findViewById(R.id.btn_past_sign);
        text_result=(TextView) findViewById(R.id.text_result);
        btn_en=(Button)findViewById(R.id.btn_en);
        btn_den=(Button)findViewById(R.id.btn_den);

        btn_copy_sign.setOnClickListener(this);
        btn_past_sign.setOnClickListener(this);
        btn_en.setOnClickListener(this);
        btn_den.setOnClickListener(this);
        Log.v("test", Base64.encode2String("sssssss".getBytes()));
    }

    @Override
    public void onClick(View v) {
        String src=inputori.getText().toString();
        String result="";
        switch (v.getId()){
            case R.id.btn_copy_sign:
                String ssss=text_result.getText().toString();
                ClipData mClipData =ClipData.newPlainText("Label", ssss);         //‘Label’这是任意文字标签
                mClipboardManager.setPrimaryClip(mClipData);
                break;
            case R.id.btn_past_sign:
                // 粘贴板有数据，并且是文本
                if (mClipboardManager.hasPrimaryClip()
                        && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                    CharSequence text = item.getText();
                    if (text == null) {
                        return;
                    }
                    inputori.setText(text);
                    inputori.setSelection(text.length());
                }
                break;
            case R.id.btn_en:
                result=Base64.encode2String(src.getBytes());
                break;
            case R.id.btn_den:
                result=new String(Base64.decode(src.getBytes()));
                break;
        }
        text_result.setText(result);
    }
}
