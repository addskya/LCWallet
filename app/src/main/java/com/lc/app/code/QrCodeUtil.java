package com.lc.app.code;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
class QrCodeUtil {


    /**
     * 生成二维码Bitmap
     *
     * @param content    内容
     * @param widthPix   图片宽度
     * @param heightPix  图片高度
     * @param outputFile 用于存储二维码图片的文件路径
     * @return 生成二维码及保存文件是否成功
     */
    public static boolean createQRImage(CharSequence content,
                                        int widthPix,
                                        int heightPix,
                                        File outputFile) {
        try {
            if (TextUtils.isEmpty(content)) {
                return false;
            }

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            BitMatrix bitMatrix = new QRCodeWriter().encode(String.valueOf(content),
                    BarcodeFormat.QR_CODE,
                    widthPix, heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

            FileOutputStream fos = new FileOutputStream(outputFile);
            boolean success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            return success;

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
