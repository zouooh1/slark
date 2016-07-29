package me.zouooh.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;

import org.nutz.lang.Strings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zouooh on 2016/7/26.
 */
public class BitmapUtils {

    public static Bitmap getResizedBitmap(Context context, Uri uri, int widthLimit, int heightLimit) throws
            IOException {
        String path;
        Bitmap result;

        if (uri.getScheme().equals("file")) {
            path = uri.getPath();
        } else if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
            cursor.moveToFirst();
            path = cursor.getString(0);
            cursor.close();
        } else {
            return null;
        }

        ExifInterface exifInterface = new ExifInterface(path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface
                .ORIENTATION_UNDEFINED);

        if ((orientation == ExifInterface.ORIENTATION_ROTATE_90) || (orientation == ExifInterface
                .ORIENTATION_ROTATE_270) || (orientation == ExifInterface.ORIENTATION_TRANSPOSE) || (orientation ==
                ExifInterface.ORIENTATION_TRANSVERSE)) {
            int tmp = widthLimit;
            widthLimit = heightLimit;
            heightLimit = tmp;
        }

        int width = options.outWidth;
        int height = options.outHeight;
        int sampleW = 1;
        int sampleH = 1;
        while (width / 2 > widthLimit) {
            width /= 2;
            sampleW <<= 1;
        }

        while (height / 2 > heightLimit) {
            height /= 2;
            sampleH <<= 1;
        }

        int sampleSize;
        options = new BitmapFactory.Options();
        if ((widthLimit == Integer.MAX_VALUE) || (heightLimit == Integer.MAX_VALUE))
            sampleSize = Math.max(sampleW, sampleH);
        else {
            sampleSize = Math.max(sampleW, sampleH);
        }
        options.inSampleSize = sampleSize;
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            options.inSampleSize <<= 1;
            bitmap = BitmapFactory.decodeFile(path, options);
        }

        Matrix matrix = new Matrix();
        if (bitmap == null) {
            return null;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        if ((orientation == 6) || (orientation == 8) || (orientation == 5) || (orientation == 7)) {
            int tmp = w;
            w = h;
            h = tmp;
        }
        switch (orientation) {
            case 6:
                matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
                break;
            case 3:
                matrix.setRotate(180.0F, w / 2.0F, h / 2.0F);
                break;
            case 8:
                matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
                break;
            case 2:
                matrix.preScale(-1.0F, 1.0F);
                break;
            case 4:
                matrix.preScale(1.0F, -1.0F);
                break;
            case 5:
                matrix.setRotate(90.0F, w / 2.0F, h / 2.0F);
                matrix.preScale(1.0F, -1.0F);
                break;
            case 7:
                matrix.setRotate(270.0F, w / 2.0F, h / 2.0F);
                matrix.preScale(1.0F, -1.0F);
        }

        float xS = widthLimit / bitmap.getWidth();
        float yS = heightLimit / bitmap.getHeight();

        matrix.postScale(Math.min(xS, yS), Math.min(xS, yS));
        try {
            result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public static Bitmap getRotateBitmap(float degrees, Bitmap bm) {
        int bmpW = bm.getWidth();
        int bmpH = bm.getHeight();

        Matrix mt = new Matrix();

        mt.setRotate(degrees);
        return Bitmap.createBitmap(bm, 0, 0, bmpW, bmpH, mt, true);
    }


    public static String getBase64FromBitmap(Bitmap bitmap) {
        String base64Str = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] bitmapBytes = baos.toByteArray();
                base64Str = Base64.encodeToString(bitmapBytes, 2);
                baos.flush();
                baos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return base64Str;
    }

    public static Bitmap getBitmapFromBase64(String base64Str) {
        if (Strings.isEmpty(base64Str)) {
            return null;
        }

        byte[] bytes = Base64.decode(base64Str, 2);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private static BitmapFactory.Options decodeBitmapOptionsInfo(Context context, Uri uri) {
        InputStream input = null;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        try {
            if (uri.getScheme().equals("content"))
                input = context.getContentResolver().openInputStream(uri);
            else if (uri.getScheme().equals("file")) {
                input = new FileInputStream(uri.getPath());
            }
            if (input == null) {
                return null;
            }
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opt);
            return opt;
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            if (null != input)
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static Bitmap rotateBitmap(String srcFilePath, Bitmap bitmap) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(srcFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        float degree = 0.0F;

        if (exif != null) {
            switch (exif.getAttributeInt("Orientation", 0)) {
                case 6:
                    degree = 90.0F;
                    break;
                case 3:
                    degree = 180.0F;
                    break;
                case 8:
                    degree = 270.0F;
                    break;
            }

        }

        if (degree != 0.0F) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, bitmap.getWidth(), bitmap.getHeight());
            Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            if ((b2 != null) && (bitmap != b2)) {
                bitmap.recycle();
                bitmap = b2;
            }
        }

        return bitmap;
    }

    public static InputStream getFileInputStream(String path) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileInputStream;
    }
}
