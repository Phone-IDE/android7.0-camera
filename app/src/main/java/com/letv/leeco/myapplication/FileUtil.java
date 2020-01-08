package com.letv.leeco.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    public  File mOriPicFile;
    public  File mCropPicFile;
    public  Uri mOriPicUri;

    public FileUtil(Context context){
        mOriPicFile = creatOriFile(context);
        mCropPicFile = creatCropFile(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mOriPicUri = FileProvider.getUriForFile(context.getApplicationContext(),"com.letv.leeco.myapplication" +
                    ".provider",mOriPicFile);
        }else {
            mOriPicUri = Uri.fromFile(mOriPicFile);
        }

    }

    private File creatOriFile(Context context){
        String imageFileName = "ori_head";

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }catch (Exception e){
            Log.e("creat ori head pic error : " ,e.getMessage());
        }
        return image;
    }

    private File creatCropFile(Context context){
        String imageFileName = "crop_head";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }catch (Exception e){
            Log.e("creat crop head pic error : ",e.getMessage());
        }
        return image;
    }

    public String savePic(Bitmap mBitmap, File file) {
        // 图像保存到文件中
        FileOutputStream foutput = null;
        try {
            foutput = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, foutput);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != foutput) {
                try {
                    foutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }


}
