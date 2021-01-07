package com.king.imageswitcher.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;


public class ImageItem implements Serializable {
    public String name;
    public String imageId;
    public String thumbnailPath;
    public String imagePath;
    public Uri imageUri;
    private Bitmap bitmapc;
    private Bitmap bitmap;
    public boolean isSelected = false;

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Bitmap getBitmap(Context context) {
        if (bitmap == null) {
            try {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    Log.i("SSSS", "imageUri="+imageUri);
                    bitmap = Bimp.revitionImageSize(context, imageUri);
                    return bitmap;
                }
                bitmap = Bimp.revitionImageSize(imagePath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public Bitmap getCompressBitmap(Context context, int width, int height) {
        if (bitmapc == null) {
            try {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    bitmapc = Bimp.getBitmapFromFile(context, imageUri, width, height);
                    return bitmapc;
                }
                File file = new File(imagePath);
                bitmapc = Bimp.getBitmapFromFile(file, width, height);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmapc;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.bitmapc = bitmap;
    }
}
