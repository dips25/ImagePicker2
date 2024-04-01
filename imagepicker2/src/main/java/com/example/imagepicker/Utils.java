package com.example.imagepicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static int dPtoPixel(Context context, int dp) {

        return (dp * (context.getResources().getDisplayMetrics().densityDpi))/DisplayMetrics.DENSITY_DEFAULT;


    }

    public static int PixeltoDp(Context context, int px) {

        return (px / (context.getResources().getDisplayMetrics().densityDpi));
    }

    public static Display getScreenDisplay(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display;


    }

    public static File saveImage(Context context , String image, Uri uri) throws IOException {

        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File file1 = new File(file , "Gallery");

        if (!file1.exists()) {

            file1.mkdir();
        }

        String imagename = new SimpleDateFormat("dd_mm_yyyy").format(new Date()).toString() +"_"+UUID.randomUUID()+".jpg";

        File imageFile = new File(file1 , imagename);

        if (!imageFile.exists()) {

            imageFile.createNewFile();
        }

        //FileInputStream fileInputStream = new FileInputStream("file://"+image);

        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;
        byte[] bytes = new byte[1024];

        while ((i=inputStream.read(bytes))!=-1) {

            baos.write(bytes , 0 , i);
        }

        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(baos.toByteArray());

        return imageFile;
    }


    public static File createNewFile(Context context) throws IOException {

        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File file1 = new File(file , "Camera");

        if (!file1.exists()) {

            file1.mkdir();
        }

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        String imageName = new SimpleDateFormat("dd-MM-yyyy").format(date) +"_"+UUID.randomUUID().toString();

        File cameraFile = File.createTempFile(imageName , ".jpg" , file1);

        return cameraFile;

    }

    public static boolean isBlank(String s) {

        if (s.equals("")) {

            return true;
        }

        Pattern pattern = Pattern.compile("\\s+");
        Matcher m = pattern.matcher(s);
        if (m.matches()) {

            return true;
        }

        return false;


    }

    public static int getScreenWidth(Context context) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        return point.x;

    }

    public static Bitmap getBitmapFromPath(Context context, String path , int width , int height) {

        File file = new File(path);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath() , o);

        return reduceBitmapSize(width , height , o , path);


    }

    public static Bitmap reduceBitmapSize(int width , int height , BitmapFactory.Options o , String path) {

        int halfWidth = 0;
        int halfHeight = 0;


        //Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size(), o);

        int insampleSize = 1;

        if (o.outWidth > width && o.outHeight > height) {

            halfWidth = o.outWidth / 2;
            halfHeight = o.outHeight / 2;

            while (halfWidth >= width && halfHeight >= height) {

                insampleSize *= 2;
                halfWidth = halfWidth / insampleSize;
                halfHeight = halfHeight / insampleSize;

            }
        }

        o.inSampleSize = insampleSize;
        o.inJustDecodeBounds = false;


        Bitmap bitmap = BitmapFactory.decodeFile(path , o);
        return bitmap;








    }
}
