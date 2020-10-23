package com.base.zxing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @类功能说明: 生成二维码图片示例
 * @作者:饶正勇
 * @时间:2013-4-18上午11:10:42
 * @版本:V1.0
 */
public class CreateQRImage
{
	//private static CreateQRImage createQRImage;
	private ImageView sweepIV;
	private static int QR_WIDTH = 460, QR_HEIGHT =460;
	private static final int IMAGE_HALFWIDTH = 40;//宽度值，影响中间图片大小
	private static final int IMAGE_HALFHEIGHT = 40;//高度值，
	private static final int MARGIN=2;//默认空白边距的宽度
	/**
	 * @方法功能说明: 生成二维码图片,实际使用时要初始化sweepIV,不然会报空指针错误
	 * @作者:饶正勇
	 * @时间:2013-4-18上午11:14:16
	 * @参数: @param url 要转换的地址或字符串,可以是中文
	 * @return void
	 * @throws
	 */

	//要转换的地址或字符串,可以是中文
	public static Bitmap createQRImage(@NonNull String url)
	{
		try
		{
			//判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1)
			{
				return null;
			}
			//配置参数
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
			hints.put(EncodeHintType.MARGIN, MARGIN); //default is 2
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH,QR_HEIGHT);
			//显示到一个ImageView上面
			//sweepIV.setImageBitmap(bitmap);
			return bitmap;
		}
		catch (WriterException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	//要转换的地址或字符串,可以是中文
	public static boolean createQRImage(@NonNull String url,@NonNull ImageView sweepIV)
	{
		try
		{
			//判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1)
			{
				return false;
			}
			//配置参数
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
			hints.put(EncodeHintType.MARGIN,MARGIN); //default is 2
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH,QR_HEIGHT);
			//显示到一个ImageView上面
			sweepIV.setImageBitmap(bitmap);
			return true;
		}
		catch (WriterException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	//要转换的地址或字符串,可以是中文
	public static boolean createQRImage(@NonNull String url,@NonNull ImageView sweepIV,@NonNull String filePath)
	{
		try
		{
			//判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1)
			{
				return false;
			}
			//配置参数
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
			hints.put(EncodeHintType.MARGIN, MARGIN); //default is 2
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH,QR_HEIGHT);
			//显示到一个ImageView上面
			sweepIV.setImageBitmap(bitmap);
			//必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
			return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
			//return bitmap;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 生成二维码Bitmap
	 *
	 * @param content   内容
	 * @param logoBm    二维码中心的Logo图标（可以为null）
	 * @param watermark 二维码下方中间水印图标（可以为null）
	 * @return 返回 生成二维码Bitmap的结果
	 */
	public static boolean createQRImage(@NonNull Context gContext,@NonNull String content,
										Bitmap logoBm, Bitmap watermark, String filePath) {
		try {
			if (content == null || "".equals(content)) {
				return false;
			}

			//配置参数
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
			hints.put(EncodeHintType.MARGIN,MARGIN); //default is 2
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter()
					.encode(content, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * (QR_HEIGHT)];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

			if (logoBm != null) {
				bitmap = addLogo(bitmap, logoBm);
			}
			if(watermark!=null) {
				bitmap = addWatermark(bitmap, watermark);
			}
			bitmap = drawTextToBitmap(gContext,bitmap,content);//添加文字
			//必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
			return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
			//return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	/**
	 * 生成二维码Bitmap
	 * @return 返回 生成二维码Bitmap的结果
	 */
	public static Bitmap createQRImage(@NonNull Context gContext, CreateQRParams params) {
		try {
			if (params.str == null || "".equals(params.str)) {
				return null;
			}
			if(params.QR_WIDTH>0 || params.QR_HEIGHT>0){
				if(params.QR_WIDTH>0 && params.QR_HEIGHT>0){
					QR_WIDTH=params.QR_WIDTH;
					QR_HEIGHT=params.QR_HEIGHT;
				}else if(params.QR_HEIGHT>0){
					QR_WIDTH=params.QR_HEIGHT;
					QR_HEIGHT=params.QR_HEIGHT;
				}else if(params.QR_WIDTH>0){
					QR_WIDTH=params.QR_WIDTH;
					QR_HEIGHT=params.QR_WIDTH;
				}
			}
			//配置参数
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
			hints.put(EncodeHintType.MARGIN,params.margin); //default is 2
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter()
					.encode(params.str, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * (QR_HEIGHT)];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

			if (params.conterBitmap != null) {
				bitmap = addLogo(bitmap, params.conterBitmap);
			}
			if(params.watermarkBitmap!=null) {
				bitmap = addWatermark(bitmap, params.watermarkBitmap);
			}
			if(params.watermarkText != null && !"".equals(params.watermarkText)){
				bitmap = drawTextToBitmap(gContext,bitmap,params.watermarkText);//添加水印文字
			}
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 生成二维码Bitmap
	 * @return 返回 生成二维码Bitmap的结果
	 */
	public static Bitmap createQRImage(@NonNull Context gContext,@NonNull CreateQRParams params,
									   @NonNull ImageView imageView) {
		try {
			if (params.str == null || "".equals(params.str)) {
				return null;
			}
			if(params.QR_WIDTH>0 || params.QR_HEIGHT>0){
				if(params.QR_WIDTH>0 && params.QR_HEIGHT>0){
					QR_WIDTH=params.QR_WIDTH;
					QR_HEIGHT=params.QR_HEIGHT;
				}else if(params.QR_HEIGHT>0){
					QR_WIDTH=params.QR_HEIGHT;
					QR_HEIGHT=params.QR_HEIGHT;
				}else if(params.QR_WIDTH>0){
					QR_WIDTH=params.QR_WIDTH;
					QR_HEIGHT=params.QR_WIDTH;
				}
			}
			//配置参数
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
			hints.put(EncodeHintType.MARGIN, params.margin); //default is 2
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter()
					.encode(params.str, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * (QR_HEIGHT)];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

			if (params.conterBitmap != null) {
				bitmap = addLogo(bitmap, params.conterBitmap);
			}
			if(params.watermarkBitmap!=null) {
				bitmap = addWatermark(bitmap, params.watermarkBitmap);
			}
			if(params.watermarkText != null && !"".equals(params.watermarkText)){
				bitmap = drawTextToBitmap(gContext,bitmap,params.watermarkText);//添加水印文字
			}
			imageView.setImageBitmap(bitmap);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 生成二维码Bitmap
	 * @return 返回 生成二维码Bitmap的结果
	 */
	public static boolean createQRImage(@NonNull Context gContext,@NonNull CreateQRParams params,
										@NonNull String filePath) {
		try {
			if (params.str == null || "".equals(params.str)) {
				return false;
			}
			if(params.QR_WIDTH>0 || params.QR_HEIGHT>0){
				if(params.QR_WIDTH>0 && params.QR_HEIGHT>0){
					QR_WIDTH=params.QR_WIDTH;
					QR_HEIGHT=params.QR_HEIGHT;
				}else if(params.QR_HEIGHT>0){
					QR_WIDTH=params.QR_HEIGHT;
					QR_HEIGHT=params.QR_HEIGHT;
				}else if(params.QR_WIDTH>0){
					QR_WIDTH=params.QR_WIDTH;
					QR_HEIGHT=params.QR_WIDTH;
				}
			}
			//配置参数
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
			hints.put(EncodeHintType.MARGIN,params.margin); //default is 2
			//图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter()
					.encode(params.str, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * (QR_HEIGHT)];
			//下面这里按照二维码的算法，逐个生成二维码的图片，
			//两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++)
			{
				for (int x = 0; x < QR_WIDTH; x++)
				{
					if (bitMatrix.get(x, y))
					{
						pixels[y * QR_WIDTH + x] = 0xff000000;
					}
					else
					{
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			//生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

			if (params.conterBitmap != null) {
				bitmap = addLogo(bitmap, params.conterBitmap);
			}
			if(params.watermarkBitmap!=null) {
				bitmap = addWatermark(bitmap, params.watermarkBitmap);
			}
			if(params.watermarkText != null && !"".equals(params.watermarkText)){
				bitmap = drawTextToBitmap(gContext,bitmap,params.watermarkText);//添加水印文字
			}
			if(filePath != null && !"".equals(filePath)){
				//必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
				return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
			}else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 添加水印
	 * */
	private	static Bitmap addWatermark(Bitmap src, Bitmap watermark )
	{
		if( src ==null){
			return  null;
		}
		int  w = src.getWidth();
		int  h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		//create the new blank  bitmap
		Bitmap newb = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );//创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas( newb );
		//draw src into
		cv.drawBitmap( src,0,0,null);//在 0，0坐标开始画入src
		//draw watermark into
		//cv.drawBitmap( watermark, w - ww +5, h - wh +5,null);//在src的右下角画入水印
		cv.drawBitmap( watermark,(w - ww)/2, h - wh+20,null);//在src的下方中间画入水印
		//save all clip
		//cv.drawText(watermark,w/2, h-20, null);
		cv.save();//保存
		//store
		cv.restore();//存储
		return newb;
	}

	/**
	 * bitmap 添加文字
	 * @param gContext  上下文
	 * @param gText   文本
	 * @param bitmap   图像位图
	 * @return
	 */
	private static Bitmap drawTextToBitmap(Context gContext,
										   Bitmap bitmap ,
										   String gText) {
		Resources resources = gContext.getResources();
		float scale = resources.getDisplayMetrics().density;
		//Bitmap bitmap =
		//		BitmapFactory.decodeResource(resources, gResId);
		Bitmap.Config bitmapConfig =
				bitmap.getConfig();
		// set default bitmap config if none
		if(bitmapConfig == null) {
			bitmapConfig = Bitmap.Config.ARGB_8888;
		}
		// resource bitmaps are imutable,
		// so we need to convert it to mutable one
		bitmap = bitmap.copy(bitmapConfig, true);
		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.rgb(00,00,00));
		// text size in pixels
		paint.setTextSize((int) (20 * scale));
		paint.setTextAlign(Paint.Align.LEFT);
		// text shadow
		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
		// draw text to the Canvas center
		Rect bounds = new Rect();
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width())/2;
		int y = (bitmap.getHeight() + bounds.height())/2+50;
		Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		//int baseline = (bitmap.getHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
		//canvas.drawText(gText, x * scale,y * scale, paint);
		canvas.drawText(gText,x, bitmap.getHeight()-(fontMetrics.bottom*2) , paint);
		return bitmap;
	}
	/**
	 * 在二维码中间添加Logo图案
	 */
	private static Bitmap addLogo(Bitmap src, Bitmap logo) {
		if (src == null) {
			return null;
		}
		if (logo == null) {
			return src;
		}
		//获取图片的宽高
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();
		if (srcWidth == 0 || srcHeight == 0) {
			return null;
		}
		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}
		//logo大小为二维码整体大小的1/5
		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
			//Bitmap：图片对象，left:偏移左边的位置，top： 偏移顶部的位置
			//drawBitmap(Bitmap bitmap, float left, float top, Paint paint)
			//canvas.drawBitmap(logo_cr, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
			canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2,srcHeight, null);
			canvas.save();
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}

		return bitmap;
	}

}
