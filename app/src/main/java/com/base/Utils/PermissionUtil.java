package com.base.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BYPC006 on 2016/8/2.
 */
public class PermissionUtil {

	private static void showMessageOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(activity)
				.setMessage(message)
				.setPositiveButton("开启", okListener)
				.setNegativeButton("取消", null)
				.create()
				.show();
	}

	private static boolean addPermission(Activity activity, List<String> permissionsList, String permission) {
		if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
//        if (!AppUtils.isPermission(MainActivity.this, permission, getPackageName())){
			permissionsList.add(permission);
			// Check for Rationale Option
			if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
				return false;
			}
		}
		return true;
	}
	/***
	 * GPS定位权限请求
	 */
	public static void requestLocationPermission(final Activity activity, final int requestCode) {
		List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();

		if (!addPermission(activity, permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)){
			permissionsNeeded.add("GPS定位权限");
		}
		if (!addPermission(activity, permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)){
			permissionsNeeded.add("网络定位权限");
		}
		if (!addPermission(activity, permissionsList, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)){
			permissionsNeeded.add("程序访问额外的定位提供者指令权限");
		}
		if (permissionsList.size() > 0){
			if (permissionsNeeded.size() > 0){
				// Need Rationale
				String message = "程序运行需要获取以下权限: " + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++){
					message = message + ", " + permissionsNeeded.get(i);
				}
				showMessageOKCancel(activity, message,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
										requestCode);
							}
						});
				return;
			}
			ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
			return;
		}
	}

	/***
	 * 申请摄像头权限
	 * @param activity
	 * @param requestCode
	 */
	public static void requestCameraPermission(final Activity activity, final int requestCode) {
		List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();

		if (!addPermission(activity, permissionsList, Manifest.permission.CAMERA)){
			permissionsNeeded.add("摄像头权限");
		}


		if (permissionsList.size() > 0){
			if (permissionsNeeded.size() > 0){
				// Need Rationale
				String message = "程序运行需要获取以下权限: " + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++){
					message = message + ", " + permissionsNeeded.get(i);
				}
				showMessageOKCancel(activity, message,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
										requestCode);
							}
						});
				return;
			}
			ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
			return;
		}
	}
	/***
	 * 申请外部存储文件读写权限
	 * @param activity
	 * @param requestCode
	 */
	public static void requestWRExternalStoragePermission(final Activity activity, final int requestCode) {
		List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();

		if (!addPermission(activity, permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
			permissionsNeeded.add("外部文件读写权限");
		}


		if (permissionsList.size() > 0){
			if (permissionsNeeded.size() > 0){
				// Need Rationale
				String message = "程序运行需要获取以下权限: " + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++){
					message = message + ", " + permissionsNeeded.get(i);
				}
				showMessageOKCancel(activity, message,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
										requestCode);
							}
						});
				return;
			}
			ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
			return;
		}
	}

	/***
	 * 申请打电话
	 * @param activity
	 * @param requestCode
	 */
	public static void requestCallPhonePermission(final Activity activity, final int requestCode) {
		List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();

		if (!addPermission(activity, permissionsList, Manifest.permission.CALL_PHONE)){
			permissionsNeeded.add("拨打电话权限");
		}

		if (permissionsList.size() > 0){
			if (permissionsNeeded.size() > 0){
				// Need Rationale
				String message = "程序运行需要获取以下权限: " + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++){
					message = message + ", " + permissionsNeeded.get(i);
				}
				showMessageOKCancel(activity, message,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
										requestCode);
							}
						});
				return;
			}
			ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
			return;
		}
	}

	/***
	 * 申请打电话和写外部文件权限
	 * @param activity
	 * @param requestCode
	 */
	public static void requestCallPhoneAndWriteExStoragePermission(final Activity activity, final int requestCode) {
		List<String> permissionsNeeded = new ArrayList<String>();
		final List<String> permissionsList = new ArrayList<String>();

		if (!addPermission(activity, permissionsList, Manifest.permission.CALL_PHONE)){
			permissionsNeeded.add("拨打电话权限");
		}
		if (!addPermission(activity, permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
			permissionsNeeded.add("外部文件读写权限");
		}

		if (permissionsList.size() > 0){
			if (permissionsNeeded.size() > 0){
				// Need Rationale
				String message = "程序运行需要获取以下权限: " + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++){
					message = message + ", " + permissionsNeeded.get(i);
				}
				showMessageOKCancel(activity, message,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
										requestCode);
							}
						});
				return;
			}
			ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), requestCode);
			return;
		}
	}
}
