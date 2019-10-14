package com.base.Display;

import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;
import android.view.WindowManager;

import com.base.appManager.BaseApplication;

import java.io.ObjectStreamException;

/**
 * 双屏管理
 */
public class DisplayUtils {
    private static int count = 0;
    private static Presentation mPresentation;
    private static DisplayManager mDisplayManager;
    private static int position=0;//当前播放进度
    private  Display[] displays;
    /**
     * 但是jdk 1.5 以后java 编译器允许乱序执行 。所以执行顺序可能是1-3-2 或者 1-2-3.如果是前者先执行3 的话
     * 切换到其他线程，instance 此时 已经是非空了，此线程就会直接取走instance ，直接使用，这样就回出错。DCL 失效。
     * 解决方法 SUN 官方已经给我们了。将instance 定义成 private volatile static Singleton instance =null: 即可
     */
    private DisplayUtils() {
        /**
         * 通过反射获得单例类的构造函数
         * 抵御这种攻击，要防止构造函数被成功调用两次。需要在构造函数中对实例化次数进行统计，大于一次就抛出异常。
         */
        synchronized (DisplayUtils.class) {
            if(count > 0){
                throw new RuntimeException("创建了两个实例");
            }
            count++;
            //开启双屏
            mDisplayManager=(DisplayManager) BaseApplication.getInstance().getSystemService(Context.DISPLAY_SERVICE);
            displays = mDisplayManager.getDisplays();
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        DisplayUtils.position = position;
    }

    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final DisplayUtils instance = new DisplayUtils();
    }
    /**
     * 单一实例
     */
    public static DisplayUtils getInstance() {
        return DisplayUtils.SingletonHolder.instance;
    }
    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return DisplayUtils.SingletonHolder.instance;
    }

    /**
     * 副屏显示收银信息
     * @param context
     */
    public void show(Context context){
        if(displays.length>=2){//小于2代表只有一个屏幕，那么mPresentation就没有必要创建了
//            int Width=displays[displays.length - 1].getWidth();
//            int Height=displays[displays.length - 1].getHeight();
//            DebugLog.i("Width="+Width+",Height="+Height);
//            Point point=new Point();
//            displays[displays.length - 1].getRealSize(point);
//            DebugLog.i("x="+point.x+",y="+point.y);
//            Rect rect=new Rect();
//            displays[displays.length - 1].getRectSize(rect);
//            DebugLog.i("rect="+rect.toString());
            if (mPresentation!=null
                    &&!(mPresentation instanceof DifferentDislay)){
                mPresentation.dismiss();
                mPresentation=null;
            }
            if (mPresentation==null) {
                mPresentation = new DifferentDislay(context, displays[displays.length - 1]);// displays[1]是副屏
                mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
//                mPresentation.setOwnerActivity(context);
            if (!mPresentation.isShowing())
                mPresentation.show();
        }
    }
    /**
     * 副屏播放视频
     * @param context
     */
    public void showVedio(Context context){
        if(displays.length>=2){//小于2代表只有一个屏幕，那么mPresentation就没有必要创建了
            if (mPresentation != null
                    && !(mPresentation instanceof AdvertisingDislay)) {
                mPresentation.dismiss();
                mPresentation = null;
            }
            if (mPresentation == null) {
                mPresentation = new AdvertisingDislay(context, displays[displays.length - 1]);// displays[1]是副屏
                mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
//                mPresentation.setOwnerActivity(context);
            if (!mPresentation.isShowing())
                mPresentation.show();
        }
    }
    /**
     * 切换扫码收银客显
     */
    public void showQR(Context context){
        //开启双屏
        if(displays.length>=2){//小于2代表只有一个屏幕，那么mPresentation就没有必要创建了
            //当前为扫码收银如果客显不是二维码客显 就关闭重新创建扫码收银客显
            if (mPresentation!=null
                    &&!(mPresentation instanceof QRCodeDislay)){
                mPresentation.dismiss();
                mPresentation=null;
            }
            if (mPresentation==null) {
                mPresentation = new QRCodeDislay(context, displays[displays.length - 1]);// displays[1]是副屏
                mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                ((QRCodeDislay)mPresentation).setCode("465468416486461");
            }
            if (!mPresentation.isShowing())
                mPresentation.show();
        }
    }
    /**
     * 关闭双屏
     */
    public void dismiss(){
        if (mPresentation!=null)
            mPresentation.dismiss();
        mPresentation=null;
    }
    public Presentation getPresentation(){
        return mPresentation;
    }
}
