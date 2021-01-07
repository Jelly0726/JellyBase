package com.king.imageswitcher.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.king.imageswitcher.PhotoUtils;
import com.king.imageswitcher.R;
import com.king.imageswitcher.util.Bimp;
import com.king.imageswitcher.util.BitmapTool;
import com.king.imageswitcher.util.ImageItem;
import com.king.imageswitcher.util.PublicWay;
import com.king.imageswitcher.util.Res;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Administrator on 2016/3/16.
 */
public class DynamicActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static String FILEJINR = "/android/data/com.king.imageswitcher/"; //包路径
    //======================导航栏begin======================================//
    private LinearLayout back_layout;//返回布局
    private ImageView back_img;//返回图标
    private TextView back_tv;//返回文本
    private LinearLayout tiltle_layout;//标题布局
    private TextView title_tv;//标题
    private ImageView title_img;//标题图片
    private LinearLayout right_layout;//右边布局
    private TextView right_tv;//右边文本
    private ImageView right_img;//右边图片
    //======================导航栏end======================================//
    private EditText edit_connt;
    private ProgressDialog progressDialog;//加载
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;
    private GridAdapter adapter;
    private GridView pic_gri;
    private View parentView;
    private String i_Cont = "无";
    private File picPath;
    private Uri imageUri;
    private final int TO_UPLOAD_FILE = 1; //去上传文件
    private final int UPLOAD_FILE_DONE = 2; //上传文件响应
    private final int TO_SELECT_PHOTO = 3;//选择文件
    private final int UPLOAD_INIT_PROCESS = 4;//上传初始化
    private final int UPLOAD_IN_PROCESS = 5;//上传中
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    ;//可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onDestroy() {
        runnable = null;
        runnable1 = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private int up_show = 0;

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        cachedThreadPool.execute(runnable);
        super.onResume();
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                Bitmap photo = Bimp.tempSelectBitmap.get(i).getBitmap(getApplicationContext());
                //Bitmap photo = BitmapFactory.decodeFile(Bimp.tempSelectBitmap.get(i).getImagePath());
                //photo = BitmapTool.drawableToBitmap(BitmapTool.resizeImage(photo, 200, 200));
                saveCroppedImage(photo, i);
                //Log.i("msg", "i=" + i);
                photo = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.imageswitcher_addpic_unfocused);
        Bimp.tempSelectBitmap.clear();
        //PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.imageswitcher_share_order, null);
        setContentView(parentView);
        progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle("加载提示");
        progressDialog.setMessage("正在加载.....");
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        iniView();
        iniDate();
        Init();
    }

    private void iniView() {
        //======================导航栏begin======================================//
//        back_layout = (LinearLayout)findViewById(R.id.back_layout);
//        back_img= (ImageView) findViewById(R.id.back_img);
//        back_img.setVisibility(View.VISIBLE);
//        back_layout.setOnClickListener(listener);
//        back_tv= (TextView)findViewById(R.id.back_tv);
//        back_tv.setVisibility(View.GONE);
//        tiltle_layout= (LinearLayout)findViewById(R.id.tiltle_layout);
//        title_tv= (TextView) findViewById(R.id.title_tv);
//        title_tv.setVisibility(View.VISIBLE);
//        title_tv.setText("发布动态");
//        title_img= (ImageView) findViewById(R.id.title_img);
//        title_img.setImageResource(R.mipmap.ic_launcher);
//        title_img.setVisibility(View.GONE);
//        right_layout= (LinearLayout)findViewById(R.id.right_layout);
//        right_layout.setOnClickListener(listener);
//        right_tv= (TextView)findViewById(R.id.right_tv);
//        right_tv.setText("发布");
//        right_tv.setVisibility(View.VISIBLE);
//        // right_tv.setOnClickListener(listener);
//        right_img= (ImageView)findViewById(R.id.right_img);
//        right_img.setVisibility(View.GONE);
        //======================导航栏end======================================//
        edit_connt = (EditText) findViewById(R.id.edit_connt);
    }

    private void iniDate() {
    }
//    private View.OnClickListener listener=new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()){
//                case R.id.back_layout://返回
//                    if(bimap!=null){
//                        bimap.recycle();
//                        bimap=null;
//                    }
//                    PublicWay.finshAll();
//                    finish();
//                    break;
//                case R.id.right_layout://提交
//                    System.out.println("msg--111111--");
//                    i_Cont=edit_connt.getText().toString();
//                    if(i_Cont.trim().length()>0) {
//                        if(progressDialog!=null){
//                            if(!progressDialog.isShowing()){
//                                progressDialog.show();
//                            }
//                        }
//                        for(int i = 0; i <Bimp.tempSelectBitmap.size(); i++){
//                            //Bimp.tempSelectBitmap.get(i);
//                            picPath = new File(Environment.getExternalStorageDirectory()
//                                    + Config.FILEJINR+"temp_"+i+".png");
//                            //picPath = new File(Bimp.tempSelectBitmap.get(i).getImagePath());
//                            toUploadFile(picPath);
//                        }
//                        System.out.println("msg--222--"+picPath);
//                    }else{
//                        Toast.makeText(DynamicActivity.this,"内容不能为空！", Toast.LENGTH_LONG).show();
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    /*
 * 保存Bitmap到sd卡中
 */
    public void saveCroppedImage(Bitmap bmp, int i) {
        String newFilePath = Environment.getExternalStorageDirectory()
                + FILEJINR + "temp_" + i + ".png";
        File file = new File(newFilePath);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bmp = null;
    }

    /**
     * 发布动态
     */
    private void SendDynPic(String content_str, String obj) {
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        Map<String, String> mParams = new HashMap<String, String>();
//        mParams.put("USER_ID",login.getUSER_ID());
//        //mParams.put("UID",login.getUID());
//        mParams.put("OBJECT_ID",login.getUID());
//        mParams.put("CONTENT",content_str);
//        mParams.put("DYNAMIC_TYPE","2");
//        mParams.put("PIC_URL",obj+"");
//        mParams= AppUtils.getSign(mParams);
//        JSONObject jsonObject = new JSONObject(mParams);
//        //Log.i("msg","发布动态json="+jsonObject.toString());
//        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST
//                , Config.RELEASEINFO, jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        if(progressDialog!=null){
//                            progressDialog.dismiss();
//                        }
//                        String son=response.toString();
//                        //Log.i("msg","发布动态son="+son);
//                        JsonUtil jsonUtil= GsonUtils.parseJSON(son,JsonUtil.class);
//                        if(jsonUtil.getSTATE().equals("1")){//成功
//                            Bimp.tempSelectBitmap.clear();
//                            ToastUtil.showMessage(jsonUtil.getMESSAGE());
//                            PublicWay.finshAll();
//                            finish();
//                        }else{
//                            new AlertDialog.Builder(DynamicActivity.this)
//                                    .setTitle(R.string.SystemNoti)
//                                    .setMessage(jsonUtil.getMESSAGE())
//                                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//
//                                        }
//                                    }).show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Log.e(TAG, error.getMessage(), error);
//                if(progressDialog!=null){
//                    progressDialog.dismiss();
//                }
//            }
//        })
//        {
//            @Override
//            public Map<String, String> getHeaders() {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Accept", "application/json");
//                headers.put("Content-Type", "application/json; charset=UTF-8");
//
//                return headers;
//            }
//        };
//        requestQueue.add(jsonRequest);
    }

    private void Init() {
        pop = new PopupWindow(DynamicActivity.this);
        View view = getLayoutInflater().inflate(R.layout.imageswitcher_item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //finish();
                Intent intent = new Intent(DynamicActivity.this,
                        AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.imageswitcher_translate_in, R.anim.imageswitcher_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        pic_gri = (GridView) findViewById(R.id.pic_gri);
        pic_gri.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        //adapter.update();
        pic_gri.setAdapter(adapter);
        pic_gri.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    if (pop.isShowing()) {
                        pop.dismiss();
                    }
                    //Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(DynamicActivity.this
                            , R.anim.imageswitcher_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(DynamicActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = BitmapTool.drawableToBitmap(BitmapTool.resizeImage(photo, 200, 200));
            String photo64 = BitmapTool.bitmapToBase64(photo);
        }
    }

    //    @Override
//    public void onUploadDone(int responseCode, String message) {
//        Message msg = Message.obtain();
//        msg.what = UPLOAD_FILE_DONE;
//        msg.arg1 = responseCode;
//        msg.obj = message;
//        handler.sendMessage(msg);
//    }
//
//    @Override
//    public void onUploadProcess(int uploadSize) {
//
//    }
//
//    @Override
//    public void initUpload(int fileSize) {
//
//    }
//
//    private void toUploadFile(File picPath ) {
//        UploadUtil uploadUtil = UploadUtil.getInstance();
//        uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("", "");
//        uploadUtil.uploadFile(picPath,Config.fileKey,Config.updatePhotos, params);
//    }
    private boolean isUpLoad = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_UPLOAD_FILE:
                    //toUploadFile();
                    break;

                case UPLOAD_INIT_PROCESS:
                    //	progressBar.setMax(msg.arg1);
                    break;
                case UPLOAD_IN_PROCESS:
                    //progressBar.setProgress(msg.arg1);
                    break;
                case UPLOAD_FILE_DONE:
                    isUpLoad = true;
                    if (msg.arg1 == 1) {
                        up_show++;
//                        try {
//                            JSONObject obj = new JSONObject((String) msg.obj);
//                            sb.append(obj.getString("msg")+",");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        if (up_show == Bimp.tempSelectBitmap.size()) {
                            if (sb.length() > 0) {
                                sb.deleteCharAt(sb.length() - 1);
                            }
                            SendDynPic(i_Cont, sb.toString());
                        }
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };
    private Runnable runnable1 = new Runnable() {
        public void run() {
//                    if (isStar) {
            if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
//                            isStar=false;
//                            return;
            } else {
                Bimp.max += 1;
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
//                            isStar=false;
//                            return;
            }
//                    }
        }
    };

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == PublicWay.num) {
                return PublicWay.num;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.imageswitcher_item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.imageswitcher_addpic_unfocused));
                if (position == PublicWay.num) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position)
                        .getCompressBitmap(getApplicationContext(),200, 200));
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        private void loading() {
            cachedThreadPool.execute(runnable1);
        }
    }

    private String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;
    private static String fileName = "0";

    private void photo() {
        /**
         * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
         * 文档，you_sdk_path/docs/guide/topics/media/camera.html
         * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
         * 官方文档太长了就不看了，其实是错的，这个地方小马也错了，必须改正
         */
//        Intent intent = new Intent(
//                MediaStore.ACTION_IMAGE_CAPTURE);
//        fileName = String.valueOf(System.currentTimeMillis());
//        //下面这句指定调用相机拍照后的照片存储的路径
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
//                .fromFile(new File(Environment
//                        .getExternalStorageDirectory(),
//                        fileName+".png")));
//        startActivityForResult(intent, TAKE_PICTURE);
        fileName = String.valueOf(System.currentTimeMillis());
        File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/" + fileName + ".jpg");
        imageUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            imageUri = FileProvider.getUriForFile(DynamicActivity.this,
                    getPackageName() + ".fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
        PhotoUtils.takePicture(this, imageUri, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < PublicWay.num && resultCode == RESULT_OK) {

//                    String fileName = String.valueOf(System.currentTimeMillis());
//                    Bitmap bm = (Bitmap) data.getExtras().get("data");
//                    File temp = new File(Environment.getExternalStorageDirectory()
//                            + "/"+fileName+".png");
                    //Bitmap bm=BitmapTool.adjustImage(temp.getPath(),this);
                    //FileUtils.saveBitmap(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setImagePath(imageUri.getPath());
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            // System.exit(0);
            if (bimap != null) {
                bimap.recycle();
                bimap = null;
            }
            finish();
        }
        return true;
    }
}