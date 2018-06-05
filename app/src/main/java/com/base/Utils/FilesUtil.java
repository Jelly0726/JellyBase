package com.base.Utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Created by Administrator on 2017/10/13.
 * 文件操作工具类
 */

public class FilesUtil {
    private FilesUtil(){

    }
    private static FilesUtil filesUtil;
    public static synchronized FilesUtil getInstance(){
        if (filesUtil==null) {
            filesUtil=new FilesUtil();
        }
        return filesUtil;
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
                sb.append(readline);
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
                sb.append(readline);
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
                    sb.append(in);
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
}