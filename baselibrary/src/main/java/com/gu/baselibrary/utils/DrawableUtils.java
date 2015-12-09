package com.gu.baselibrary.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.text.AndroidCharacter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gu.baselibrary.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author nate
 * @ClassName: DrawableUtils
 * @Description: 图片展示工具类
 * @date 2015-5-26 下午4:09:56
 */
public class DrawableUtils {

    private DrawableUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static DisplayImageOptions DISPLAY_OPTIONS =
            new DisplayImageOptions.Builder().showImageForEmptyUri(R.mipmap.empty_photo)
                    .showImageOnFail(R.mipmap.empty_photo)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Config.RGB_565)
                    .build();

    /**
     * 加载网络圆形图片
     *
     * @param img
     * @param url
     */
    public static void displayRoundImgOnNet(final ImageView img, final String url) {
        if (TextUtils.isEmpty(url)) {
            img.setImageBitmap(readBitMap(img.getContext(), R.mipmap.empty_photo));
            return;
        }
        ImageLoader.getInstance().displayImage(url,
                img,
                DISPLAY_OPTIONS,
                new AnimateFirstDisplayRoundListener());
    }

    /**
     * 加载网络圆角图片
     *
     * @param img
     * @param url
     */
    public static void displayRoundCornerImgOnNet(final ImageView img, final String url) {
        if (TextUtils.isEmpty(url)) {
            img.setImageBitmap(readBitMap(img.getContext(), R.mipmap.empty_photo));
            return;
        }
        ImageLoader.getInstance().displayImage(url,
                img,
                DISPLAY_OPTIONS,
                new AnimateFirstDisplayRoundCornerListener());
    }

    /**
     * 本地圆角图片 ---drawable
     *
     * @param img
     * @param resourceId
     */
    public static void disPlayLocRoundCornerImg(final ImageView img, int resourceId) {
        Bitmap mBitmap = BitmapFactory.decodeResource(img.getContext().getResources(), resourceId);
        img.setImageBitmap(toRoundCorner(mBitmap));
    }

    /**
     * 本地圆形图片 ---drawable
     *
     * @param img
     * @param resourceId
     */
    public static void disPlayLocRoundImg(final ImageView img, int resourceId) {
        Bitmap mBitmap = BitmapFactory.decodeResource(img.getContext().getResources(), resourceId);
        img.setImageBitmap(toRoundBitmap(mBitmap));
    }

    /**
     * 显示本地正常图片 --file
     *
     * @param img
     * @param url
     */
    public static void displayLocImg(ImageView img, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith("file:") == false) {
            url = "file:/" + url;
        }
        ImageLoader.getInstance().displayImage(url, img, DISPLAY_OPTIONS, new AnimateFirstDisplayListener());
    }

    /**
     * 本地圆角图片 ---file
     *
     * @param img
     * @param url
     */
    public static void disPlayLocRoundCornerImg(ImageView img, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith("file:") == false) {
            url = "file://" + url;
        }
        ImageLoader.getInstance().displayImage(url, img, DISPLAY_OPTIONS, new AnimateFirstDisplayRoundCornerListener());
    }

    /**
     * 本地圆形图片 ---file
     *
     * @param img
     * @param url
     */
    public static void disPlayLocRoundImg(ImageView img, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith("file:") == false) {
            url = "file:/" + url;
        }
        ImageLoader.getInstance().displayImage(url, img, DISPLAY_OPTIONS, new AnimateFirstDisplayRoundListener());
    }

    /**
     * 用于显示普通的网络图片
     *
     * @param img
     * @param url
     */
    public static void displayNormalImgOnNet(final ImageView img, final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        ImageLoader.getInstance().displayImage(url, img, DISPLAY_OPTIONS, new AnimateFirstDisplayListener());
    }

    /**
     * 显示根据布局自适应大小的网络正常图片
     *
     * @param imageView
     * @param url
     */
    public static void displayAutoImgOnNet(final ImageView imageView, final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        ImageLoader.getInstance().displayImage(url,
                imageView,
                DISPLAY_OPTIONS,
                new SimpleImageLoadingListener() {
                    final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        float[] cons = ScreenUtils.getBitmapConfiguration(loadedImage, imageView, 1.0f);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) cons[0], (int) cons[1]);
                        layoutParams.setMargins(0, 0, 0, ScreenUtils.dp2px(imageView.getContext(), 8));
                        imageView.setLayoutParams(layoutParams);
                        boolean firstDisplay = !displayedImages.contains(imageUri);
                        if (firstDisplay) {
                            FadeInBitmapDisplayer.animate(imageView, 500);
                            displayedImages.add(imageUri);
                        } else {
                            imageView.setImageBitmap(loadedImage);
                        }
                    }
                });
    }

    /**
     * 获取圆角位图的方法
     */
    public static Bitmap toRoundCorner(Bitmap bitmap) {
        // TODO: 2015/9/16  全局配置图片圆角
        float roundPx = 5;
        if (null == bitmap || bitmap.isRecycled()) {
            return null;
        }
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
            final RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG));
            roundPx = bitmap.getWidth() / 10;// 此处如除以2了 则就是圆图了 就不是圆角了
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 产生一个fadeIn动画显示Round图片
     */
    public static class AnimateFirstDisplayRoundListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    displayedImages.add(imageUri);
                    FadeInDisplay(imageView, toRoundBitmap(loadedImage));
                } else
                    imageView.setImageBitmap(toRoundBitmap(loadedImage));
            }
        }
    }

    /**
     * 产生一个fadeIn动画显示Corner图片
     */
    public static class AnimateFirstDisplayRoundCornerListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    displayedImages.add(imageUri);
                    FadeInDisplay(imageView, toRoundCorner(loadedImage));
                } else {
                    imageView.setImageBitmap(toRoundCorner(loadedImage));
                }
            }
        }
    }

    /**
     * 产生一个fadeIn动画显示正常图片
     */
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                } else {
                    imageView.setImageBitmap(loadedImage);
                }
            }
        }
    }

    /**
     * 这是为了能够对Bitmap进行处理之后再展示
     *
     * @param imageView
     * @param bitmap
     */
    public static void FadeInDisplay(ImageView imageView, Bitmap bitmap) {
        final TransitionDrawable transitionDrawable =
                new TransitionDrawable(new Drawable[]{new ColorDrawable(android.R.color.transparent),
                        new BitmapDrawable(imageView.getResources(), bitmap)});
        imageView.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(500);
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;

    }

    /**
     * 旋转图片一定角度 rotaingImageView
     *
     * @param angle
     * @param bitmap
     * @return
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 将图片转化为圆形头像
     *
     * @param bitmap
     * @return
     */
    private static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿
        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        // 以下有两种方法画圆,drawRounRect和drawCircle
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    /**
     * 以最省内存的方式读取本地资源的图片 hdpi文件夹下
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private static Bitmap readBitMap(Context context, int resId) {
        Bitmap bmp = null;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        try {
            opt.inPreferredConfig = Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            opt.inJustDecodeBounds = true;// 只返回宽高,不返回bitmap的Byte
            // 获取资源图片
            InputStream is = context.getResources().openRawResource(resId);
            bmp = BitmapFactory.decodeStream(is, null, opt);
            int hi = (opt.outHeight / ScreenUtils.getScreenHeight(context));// 以屏幕高度作为显示依据
            int wi = (opt.outWidth / ScreenUtils.getScreenWidth(context));// 以屏幕高度作为显示依据
            int be = 0;
            if (wi > hi)
                be = hi;
            else
                be = wi;
            if (be > 0)
                opt.inSampleSize = be; // 重新读入图片，注意此时已经把
            // options.inJustDecodeBounds 设回 false 了
            opt.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeStream(is, null, opt);
        } catch (OutOfMemoryError error) {
        } catch (Exception ex) {
            Log.d("readBitMap", "" + ex.getMessage());
        }
        return bmp;
    }
}
