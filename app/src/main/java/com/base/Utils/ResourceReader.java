package com.base.Utils;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

/**
 * @author : 
 * date    : 2012-7-8
 * 
 */
public class ResourceReader {
	private ResourceReader(){}
	public static String readString (Context context, int id) {
		return context.getResources().getString(id);
	}
	public static int readDimen(Context context, int id){
		return context.getResources().getDimensionPixelOffset(id);
	}
	public static Drawable readDrawable(Context context, int id){
		return context.getResources().getDrawable(id);
	}
	public static int readColor(Context context, int id){
		return context.getResources().getColor(id);
	}
	public static ColorStateList readColorStateList(Context context, int id){
		return context.getResources().getColorStateList(id);
	}
	
}