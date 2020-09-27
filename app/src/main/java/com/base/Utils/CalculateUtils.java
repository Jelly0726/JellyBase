package com.base.Utils;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * 计算的工具类
 */
class CalculateUtils {
    /**
     * 根据分辨率计算对应的屏幕尺寸
     * @param width   横向分辨率
     * @param height  纵向分辨率
     */
    public static void getScreenSizeByResolution(float width,float height){
        //如果您在 layout 文件中使用 pt 作为单位进行布局
        //则可以根据公式 (sqrt(纵向分辨率^2+横向分辨率^2))/72 求出屏幕尺寸
        double size0=(sqrt(pow(height,2)+pow(width,2)))/72;
        System.out.println("布局单位为PT时屏幕尺寸："+size0);
        //如果您在 layout 文件中使用 in 作为单位进行布局
        //则可以根据公式 sqrt(纵向分辨率^2+横向分辨率^2) 求出屏幕尺寸
        double size1=sqrt(pow(height,2)+pow(width,2));
        System.out.println("布局单位为IN时屏幕尺寸："+size1);
        //如果您在 layout 文件中使用 mm 作为单位进行布局
        //则可以根据公式 (sqrt(纵向分辨率^2+横向分辨率^2))/25.4 求出屏幕尺寸
        double size=(sqrt(pow(height,2)+pow(width,2)))/25.4;
        System.out.println("布局单位为MM时屏幕尺寸："+size);
    }
    public static void main(String[] args){
        getScreenSizeByResolution(375,812);
    }
}
