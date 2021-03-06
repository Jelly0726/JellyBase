package com.jelly.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Browser;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Administrator on 2017/10/13.
 * 文件操作工具类
 */

public class FilesUtil {
    //常用文件类型格式
    //*/*
    public static final String ALL="*/*";
    //图片格式
    public static final String IMAGE="image/*";
    //视频格式
    public static final String VIDEO="video/*";
    //音频格式
    public static final String AUDIO="audio/*";
    //.apk
    public static final String APK="application/vnd.android.package-archive";
    //.txt .c .log .cpp .conf .h .java .prop .rc .sh .xml .htm .html
    public static final String TEXT="text/*";
    //.bin
    public static final String BIN="application/octet-stream";
    //.class
    public static final String CLASS="application/octet-stream";
    //.exe
    public static final String EXE="application/octet-stream";
    //.doc .docx .wps
    public static final String WORD="application/msword;" +
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document;" +
            "application/vnd.ms-works";
    //.xls .xlsx
    public static final String EXCEL="application/vnd.ms-excel;" +
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    //.gtar .gz .tar .tgz .zip
    public static final String RAR="application/x-gtar;" +
            "application/x-gzip;" +
            "application/x-tar;" +
            "application/x-compressed;" +
            "application/x-zip-compressed";
    //.jar
    public static final String JAR="application/java-archive";
    //.js
    public static final String JS="application/x-javascript";
    //mpc
    public static final String MPC="application/vnd.mpohun.certificate";
    //msg
    public static final String MSG="application/vnd.ms-outlook";
    //pdf
    public static final String PDF="application/pdf";
    //.pps .ppt .pptx
    public static final String PPT="pplication/vnd.ms-powerpoint;" +
            "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    //.rtf
    public static final String RTF="application/rtf";
    //.z
    public static final String Z="application/x-compress";
    private FilesUtil(){

    }
    private static FilesUtil instance;
    public static synchronized FilesUtil getInstance(){
        if (instance==null) {
            instance=new FilesUtil();
        }
        return instance;
    }
    /**
     * 打开文件选择器
     * @param activity
     * @param requestCode
     * @param type
     */
    public void selectFile(Activity activity,int requestCode,String type){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("image/*");//选择图片
        //intent.setType("audio/*"); //选择音频
        //intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
        //intent.setType("video/*;image/*");//同时选择视频和图片
        intent.setType(type);//
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, requestCode);
    }
    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    /**
     得到资源文件中图片的Uri
     * @param context 上下文对象
     * @param resId 资源id
     * @return Uri
     * @return
     */
    public Uri getUriByResId(Context context,int resId){
        Resources resources = context.getResources();
        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(resId) + "/"
                + resources.getResourceTypeName(resId) + "/"
                + resources.getResourceEntryName(resId);
        return Uri.parse(path);
    }
    /**
     * 根据Android资源ID获取Android资源的uri
     * @param resId
     * @return
     */
    public Uri getUriByAndroidResId(Context context,int resId){
        //*获取Res资源的url  ContentResolver.SCHEME_ANDROID_RESOURCE*/
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
        return uri;
    }
    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName
     * @param c
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    /**
     * 根据Asset资源名称获取Asset资源的uri
     * @param resName
     * @return
     */
    public Uri getUriFromAsset(String resName){
        //*获取asset资源的url,ContentResolver.SCHEME_FILE*/
        Uri uri = Uri.parse("file:///android_asset/" + resName);
        return uri;
    }

    /**
     * 根据Uri获取真实路径(用于相册)
     * @param contentUri
     * @return
     */
    public String getRealPathFromURI(Context context,Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getApplicationContext().getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }
//    public String getFileName(String url) {
//        String fileName = null;
//        if (!TextUtils.isEmpty(url)) {
//            try {
//                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
//                Request request = new Request.Builder()
//                        .url(url)//请求接口。如果需要传参拼接到接口后面。
//                        .build();//创建Request 对象
//                Response response = client.newCall(request).execute();//得到Response 对象
//                HttpUrl realUrl = response.request().url();
//                Log.e("zmm", "real:" + realUrl);
//                if (realUrl != null) {
//                    String temp = realUrl.toString();
//                    fileName = temp.substring(temp.lastIndexOf("/") + 1);
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("zmm", "Get File Name:error" + e);
//            }
//        }
//        Log.e("zmm", "fileName--->" + fileName);
//        return fileName;
//    }

    /**
     * 根据URL得到文件名称
     * @param url
     * @return
     */
    public String getFileName(@NonNull String url) {
        String filename = "";
        if (!TextUtils.isEmpty(url)) {
            try {
                OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                Request request = new Request.Builder()
                        .url(url)//请求接口。如果需要传参拼接到接口后面。
                        .build();//创建Request 对象
                Response response = client.newCall(request).execute();//得到Response 对象
                HttpUrl realUrl = response.request().url();
                Log.e("zmm", "real:" + realUrl);
                if (realUrl != null) {
                    String temp = realUrl.toString();
                    filename = temp.substring(temp.lastIndexOf("/") + 1);

                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("zmm", "Get File Name:error" + e);
            }
            // 从路径中获取
            if (filename == null || "".equals(filename)) {
                filename = url.substring(url.lastIndexOf("/") + 1);
            }
        }
        return filename;
    }

    /**
     * 使用浏览器打开PDF：（APP外部打开，适用于加载网络PDF）
     * @param context
     * @param url
     */
    public void openPDFInBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("error", "Activity was not found for intent, " + intent.toString());
        }
    }

    /**
     * 使用第三方APP打开PDF：（APP外部打开，适用于本地PDF）
     * @param context
     * @param FILE_NAME
     */
    public static void openPDFInNative(Context context, String FILE_NAME) {
        File file = new File(context.getExternalCacheDir(),FILE_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/pdf");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("URLSpan", "Activity was not found for intent, " + intent.toString());
        }
    }
    /**
     *@author chenzheng_Java
     *保存用户输入的内容到文件
     */
    public void saveFile(Context context,String content,String fileName) {
        try {
            /* 根据用户提供的文件名，以及文件的应用模式，打开一个输出流.文件不存系统会为你创建一个的，
             * 至于为什么这个地方还有FileNotFoundException抛出，我也比较纳闷。在Context中是这样定义的
             *   public abstract FileOutputStream openFileOutput(String name, int mode)
             *   throws FileNotFoundException;
             * openFileOutput(String name, int mode);
             * 第一个参数，代表文件名称，注意这里的文件名称不能包括任何的/或者/这种分隔符，只能是文件名
             *          该文件会被保存在/data/data/应用名称/files/chenzheng_java.txt
             * 第二个参数，代表文件的操作模式
             *          MODE_PRIVATE 私有（只能创建它的应用访问） 重复写入时会文件覆盖
             *          MODE_APPEND  私有   重复写入时会在文件的末尾进行追加，而不是覆盖掉原来的文件
             *          MODE_WORLD_READABLE 公用  可读
             *          MODE_WORLD_WRITEABLE 公用 可读写
             *  */
//            FileOutputStream outputStream = openFileOutput(fileName,
//                    Activity.MODE_APPEND);
//            outputStream.write(content.getBytes());
//            outputStream.flush();
//            outputStream.close();

            //使用BufferedWriter，在构造BufferedWriter时，把第二个参数设为true
            FileOutputStream outputStream = context.openFileOutput(fileName,
                    Activity.MODE_APPEND);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    outputStream));
            //第二个参数意义是说是否以append方式添加内容
            //BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true));
            bw.write(content);
            bw.write("\r\n");
            bw.flush();
            bw.close();
            outputStream.flush();
            outputStream.close();
            //Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * android10及以上 >=29
     * 创建并获取公共目录下的文件路径
     * @param context
     * @param fileName  指文件名，不包含路径
     * @param fileType   文件类型
     * @param relativePath   包含某个媒体下的子路径
     * @return
     */
    public Uri insertFileIntoMediaStore(Context context, String fileName, String fileType, String relativePath) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return null;
        }
        ContentResolver resolver = context.getContentResolver();
        //设置文件参数到ContentValues中
        ContentValues values = new ContentValues();
        //设置文件名
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        //设置文件描述，这里以文件名为例子
        values.put(MediaStore.Downloads.BUCKET_DISPLAY_NAME, fileName);
        //设置文件类型 "application/vnd.android.package-archive"
        values.put(MediaStore.Downloads.MIME_TYPE, fileType);
        //注意RELATIVE_PATH需要targetVersion=29
        //故该方法只可在Android10的手机上执行
        values.put(MediaStore.Downloads.RELATIVE_PATH, relativePath);
        //EXTERNAL_CONTENT_URI代表外部存储器
        Uri external = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        //insertUri表示文件保存的uri路径
        Uri insertUri = resolver.insert(external, values);
        return insertUri;
    }

    /**
     * 公共目录下的指定文件夹下创建文件
     * @param context
     * @param insertUri  共享分区路径uri
     * @param sourcePath 要复制的源文件路径
     */
    public void saveFile(Context context, Uri insertUri,String sourcePath) {
        if (insertUri == null) {
            return;
        }
        String mFilePath = insertUri.toString();
        ContentResolver resolver = context.getContentResolver();
        InputStream is = null;
        OutputStream os = null;
        try {
            os = resolver.openOutputStream(insertUri);
            if (os == null) {
                return;
            }
            int read;
            File sourceFile = new File(sourcePath);
            if (sourceFile.exists()) { // 文件存在时
                is = new FileInputStream(sourceFile); // 读入原文件
                byte[] buffer = new byte[1024];
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 使用存储访问框架（SAF）在mUri目录下创建文件
     * @param context
     * @param mUri
     * @param fileName
     * @param mimeType
     */
    public void createFile(Context context,Uri mUri,String fileName,String mimeType) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, mUri);
        DocumentFile file = documentFile.createFile(mimeType, fileName);
        if (file != null && file.exists()) {
//            LogUtil.log(file.getName() + " created");
        }
    }

    /**
     * 使用存储访问框架（SAF）删除mUri目录下的文件
     * @param context
     * @param mUri
     * @param fileName
     */
    private void deleteFile(Context context,Uri mUri,String fileName) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, mUri);
        // listFiles()，列出所有的子文件和文件夹
        for (DocumentFile file : documentFile.listFiles()) {
            if (file.isFile() && fileName.equals(file.getName())) {
                boolean delete = file.delete();
//                LogUtil.log("deleteFile: " + delete);
                break;
            }
        }
    }

    /**
     *在 mUri目录下的文件中写入数据
     * @param context
     * @param uri
     * @param cont     数据
     */
    public void writeFile(Context context,Uri uri,String cont) {
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "w");
            //这种方法会覆盖原来文件内容
            OutputStreamWriter output =
                    new OutputStreamWriter(new FileOutputStream(pfd.getFileDescriptor()));
            // 不能传uri.toString(),否则FileNotFoundException
            // OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(uri.toString(), true));
            output.write(cont);
            output.close();
//            LogUtil.log("写入成功。");
        } catch (IOException e) {
//            LogUtil.log(e);
        }
    }
    /**
     * 使用MediaStore删除文件
     * @param context
     * @param fileUri
     */
    public void deleteFile (Context context, Uri fileUri) {
        context.getContentResolver().delete(fileUri, null, null);
    }
    /**
     * 读取文件夹返回文件夹下的文件组
     * @param patch
     * @return
     */
    public File[] getFils(String patch){
        File file=new File(patch);
        File[] files = file.listFiles();
        return files;
    }
    /**
     *读取刚才用户保存的内容
     * @param patch
     * @return
     */
    public StringBuffer read(String patch){
        StringBuffer sb = new StringBuffer();
        try {
            File file = new File(patch);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                if (!isEmpty(readline)) {
                    sb.append(readline);
                    sb.append("\r\n");
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }
    /**
     *读取刚才用户保存的内容
     * @return
     */
    public StringBuffer read(File file ){
        StringBuffer sb = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                if (!isEmpty(readline)) {
                    sb.append(readline);
                    sb.append("\r\n");
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    /**
     * @author chenzheng_java
     * 读取刚才用户保存的内容
     */
    public StringBuffer read(Context context,String fileName) {
        StringBuffer sb = new StringBuffer();
        try {
            //获取数据库路径
            String path=context.getFileStreamPath(fileName).getPath();
            File file=new File(path);
            //文件是否存在
            if(file.exists()) {
                FileInputStream inputStream = context.openFileInput(fileName);
                //UTF-8编码的指定是很重要的
                InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bfr = new BufferedReader(isr);
                String in = "";
                while ((in = bfr.readLine()) != null) {
                    if (!isEmpty(in)) {
                        sb.append(in);
                        sb.append("\r\n");
                    }
                }
//            byte[] bytes = new byte[1024];
//            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
//            while (inputStream.read(bytes) != -1) {
//                arrayOutputStream.write(bytes, 0, bytes.length);
//            }
                bfr.close();
                inputStream.close();
//            arrayOutputStream.close();
//            String content = new String(arrayOutputStream.toByteArray());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }
    /**
     * 解压zip文件
     */
    public void unzipFile(File zipFile, String destination) throws IOException {
        FileInputStream fileStream = null;
        BufferedInputStream bufferedStream = null;
        ZipInputStream zipStream = null;
        try {
            fileStream = new FileInputStream(zipFile);
            bufferedStream = new BufferedInputStream(fileStream);
            zipStream = new ZipInputStream(bufferedStream);
            ZipEntry entry;

            File destinationFolder = new File(destination);
            if (destinationFolder.exists()) {
                deleteDirectory(destinationFolder);
            }

            destinationFolder.mkdirs();

            byte[] buffer = new byte[1024];
            while ((entry = zipStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                File file = new File(destinationFolder, fileName);
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    FileOutputStream fout = new FileOutputStream(file);
                    try {
                        int numBytesRead;
                        while ((numBytesRead = zipStream.read(buffer)) != -1) {
                            fout.write(buffer, 0, numBytesRead);
                        }
                    } finally {
                        fout.close();
                    }
                }
                long time = entry.getTime();
                if (time > 0) {
                    file.setLastModified(time);
                }
            }
        } finally {
            try {
                if (zipStream != null) {
                    zipStream.close();
                }
                if (bufferedStream != null) {
                    bufferedStream.close();
                }
                if (fileStream != null) {
                    fileStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 删除文件
     */
    public boolean deleteFile(Context context,String fileName){
        //获取数据库路径
        String path=context.getFileStreamPath(fileName).getPath();
        File file=new File(path);
        //文件是否存在
        if(file.exists())
        {
            if(file.delete()) {
                Toast.makeText(context, "删除文件成功!", Toast.LENGTH_LONG).show();
                return true;
            }else{
                Toast.makeText(context, "文件删除失败!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return false;
    }
    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 判断文件是否存在
     * @param strFile
     * @return
     */
    public boolean fileIsExists(String strFile){
        try{
            File f=new File(strFile);
            if(!f.exists()|| f.isDirectory()){
                return false;
            }

        }catch (Exception e){
            return false;
        }
        return true;
    }
    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }
    /**
     * 删除指定的文件或目录（无限递归删除，返回是否删除成功）
     * @param FileOrDirectory    文件
     * @return
     */
    public boolean deleteDirectory(File FileOrDirectory) {
        if (FileOrDirectory.exists()) {
            if (FileOrDirectory.isFile()) {
                return FileOrDirectory.delete();
            } else if (FileOrDirectory.isDirectory()) {
                File files[] = FileOrDirectory.listFiles();
                for (int i = 0; i < files.length; i++) {
                    DeleteFileOrDirectory(files[i]);
                }
            }
        }
        return false;
    }
    /**
     * 删除指定的文件或目录（无限递归删除，返回是否删除成功）
     * @param FileOrDirectory    文件
     * @return
     */
    public boolean DeleteFileOrDirectory(File FileOrDirectory) {
        if (FileOrDirectory.exists()) {
            if (FileOrDirectory.isFile()) {
                return FileOrDirectory.delete();
            } else if (FileOrDirectory.isDirectory()) {
                File files[] = FileOrDirectory.listFiles();
                for (int i = 0; i < files.length; i++) {
                    DeleteFileOrDirectory(files[i]);
                }
            }
        }
        return false;
    }
    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }
    /**
     * 保存HTML文件
     * @param context
     * @param ss
     */
    public void saveFileHtml(Context context, String ss) {
        try{
            String sdd= Environment.getDataDirectory()+"/files/index.html";
            InputStream in =getStringStream(ss);
            int lenght = in.available();
            //创建byte数组
            byte[]  buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            FileOutputStream outStream = context.openFileOutput(sdd, Context.MODE_PRIVATE);
            outStream.write(buffer);
            outStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 将一个字符串转化为输入流
     */
    public InputStream getStringStream(String sInputString){
        if (sInputString != null && !sInputString.trim().equals("")){
            try{
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
                return tInputStringStream;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将一个输入流转化为字符串
     */
    public String getStreamString(InputStream tInputStream){
        if (tInputStream != null){
            try{
                BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(tInputStream));
                StringBuffer tStringBuffer = new StringBuffer();
                String sTempOneLine = new String("");
                while ((sTempOneLine = tBufferedReader.readLine()) != null){
                    tStringBuffer.append(sTempOneLine);
                }
                return tStringBuffer.toString();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 字符串非空判断
     * @param string
     * @return
     */
    private boolean isEmpty(String string){
        if (string!=null){
            if (!string.toLowerCase().equals("null")
                    &&string.trim().length()>0){
                return false;
            }
        }
        return true;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
