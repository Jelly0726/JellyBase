package com.jelly.applicationUtil;

import android.hardware.Camera;
import android.os.Build;

/**
 * 检查设备是否有摄像头 
 */
public class CameraProvider {
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    /** 
      * 检查设备是否有摄像头 
      * @return true,有相机；false,无相机
      */
    public static boolean hasCamera() {
        return hasBackFacingCamera() || hasFrontFacingCamera();
    }

    /**检查设备是否有后置摄像头 
      * @returntrue,有后置摄像头；false,后置摄像头
      */
    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }

    /**检查设备是否有前置摄像头 
      * @returntrue,有前置摄像头；false,前置摄像头
      */
    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_BACK = 1;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }




    /**
      * 判断相机是否可用
      * @return true,相机驱动可用，false,相机驱动不可用
      */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            mCamera.release();
            mCamera = null;
        }
        return canUse;
    }

}