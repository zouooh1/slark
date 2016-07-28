package me.zouooh.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zouooh on 2016/7/26.
 */
public class BitmapUtils {

    public static File compress(int size, int quality, File src,File ta) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(src.getAbsolutePath(), options);

        int wantWidth = 960;
        int wantHeight = 960;

        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;

        if (width<=height) {
            if (width > wantWidth) {
                inSampleSize = Math
                        .round((float) width / (float) wantWidth);
            }
        }else {
            if (height > wantHeight) {
                inSampleSize = Math
                        .round((float) height / (float) wantHeight);
            }
        }

        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;


        try {
            Bitmap bitmap = BitmapFactory.decodeFile(src.getAbsolutePath(), options);
            int rotate = getOrientation(src.getAbsolutePath());
            if (rotate > 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotate);
                Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        options.outWidth, options.outHeight, matrix, true);
                if (rotateBitmap != null) {
                    bitmap.recycle();
                    bitmap = rotateBitmap;
                }
            }
            FileOutputStream out = new FileOutputStream(ta);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            bitmap.recycle();
            return ta;
        } catch (Throwable e) {

        }
        return null;
    }

    public static int getOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }
}
