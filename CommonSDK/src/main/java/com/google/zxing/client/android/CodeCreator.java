package com.google.zxing.client.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

public class CodeCreator {
    private String code;//加密的二维码
    private int imageHeight, imageWidth;
    private BarcodeFormat codeFormat;
    private Bitmap logo;
    public CodeCreator code(String code) {
        this.code = code;
        return this;
    }

    public CodeCreator logo(Bitmap logo) {
        this.logo = logo;
        return this;
    }

    public CodeCreator size( int imageWidth,int imageHeight) {
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        return this;
    }

    public CodeCreator size(int size) {
        this.imageHeight = size;
        this.imageWidth = size;
        return this;
    }

    public CodeCreator format(BarcodeFormat codeFormat) {
        this.codeFormat = codeFormat;
        return this;
    }

    public Bitmap create() {
        if (TextUtils.isEmpty(code)) {
            throw new IllegalArgumentException("CodeCreator the code can not be empty!");
        }
        if (this.codeFormat == null) {
            throw new IllegalArgumentException("CodeCreator the BarcodeFormat can not be empty!");
        }

        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.QR_VERSION,5);
            BitMatrix matrix = new MultiFormatWriter().encode(code, codeFormat, imageWidth,
                    imageHeight,hints);
            CodeBitmapCreator bitmapCreator = getCodeBitmapCreator();
            if (bitmapCreator != null) {
                return bitmapCreator.onCreateCodeBitmap(matrix, imageWidth, imageHeight,logo);
            } else {
                throw new RuntimeException("not support “" + codeFormat.name() + "” " +
                        "barcode format");
            }
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private CodeBitmapCreator getCodeBitmapCreator() {
        if (codeFormat == BarcodeFormat.DATA_MATRIX) {
            return new DMCodeBitmapCreator();
        } else
            return new DefaultCodeBitmapCreator();
    }


    public interface CodeBitmapCreator {
        /**
         * @param matrix
         * @param imageWidth
         * @param imageHeight
         * @return
         */
        Bitmap onCreateCodeBitmap(BitMatrix matrix, int imageWidth, int imageHeight, Bitmap logo);
    }

    /**
     * 默认的二维码/一维码位图创建器
     */
    public static class DefaultCodeBitmapCreator implements CodeBitmapCreator {

        @Override
        public Bitmap onCreateCodeBitmap(BitMatrix matrix, int imageWidth, int imageHeight,Bitmap logoBitmap) {

            // 二维矩阵转为一维像素数组,也就是一直横着排了

            int[] rectInt=matrix.getEnclosingRectangle();
            //code码区域
            Rect rect=new Rect(rectInt[0],rectInt[1],rectInt[0]+rectInt[2],rectInt[1]+rectInt[3]);
            int width = rect.width();
            int height = rect.height();
            int[] pixels = new int[rect.width() * rect.height()];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x+rect.left, y+rect.top)) {
                        pixels[y * width + x] = 0xff000000;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(rect.width(), rect.height(),
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if (logoBitmap != null) {


                final Canvas canvas = new Canvas(bitmap);
                final int logoSize = rect.width() / 4;
                final int padding = (rect.width() - logoSize) / 2;
                final Rect pictureRect = new Rect(0, 0, logoBitmap.getWidth(), logoBitmap.getHeight());
                final RectF dst = new RectF(padding, padding, logoSize + padding, logoSize + padding);
//                dst.offset(rectInt[0],rectInt[1]);
                canvas.drawBitmap(logoBitmap, pictureRect, dst, null);
                logoBitmap.recycle();
            }
            return bitmap;
        }
    }

    /**
     * DMCODE
     */
    public static class DMCodeBitmapCreator implements CodeBitmapCreator {

        @Override
        public Bitmap onCreateCodeBitmap(BitMatrix matrix, int imageWidth, int imageHeight,Bitmap logo) {
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            //reset image size
            int widthRange = imageWidth / width;
            widthRange = Math.max(widthRange, 1);
            int scaleImageWidth = widthRange * width;

            int heightRange = imageHeight / height;
            heightRange = Math.max(heightRange, 1);
            int scaleImageHeight = heightRange * height;
            // 二维矩阵转为一维像素数组,也就是一直横着排了
            int[] pixels = new int[scaleImageWidth * scaleImageHeight];

            for (int y = 0; y < scaleImageHeight; y++) {
                for (int x = 0; x < scaleImageWidth; x++) {
                    if (matrix.get(x / widthRange, y / heightRange)) {
                        pixels[y * scaleImageWidth + x] = 0xff000000;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(scaleImageWidth, scaleImageHeight,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, scaleImageWidth, 0, 0, scaleImageWidth, scaleImageHeight);

            return bitmap;
        }
    }
}
