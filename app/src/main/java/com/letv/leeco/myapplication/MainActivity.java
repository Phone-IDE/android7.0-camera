package com.letv.leeco.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_PERMISSIONS = 0x011;
    private static final int REQUEST_CODE_CAPTURE_CAMERA = 0x012;
    private static final int REQUEST_CODE_CROP = 0x013;
    private static final int REQUEST_CODE_PICK_IMAGE = 0x014;

    private Button mTakePhotoBtn;
    private Button mGoGalleryBtn;
    private ImageView mPic;
    private FileUtil mFileUtil;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPerssion();

        mFileUtil = new FileUtil(getApplicationContext());

        mTakePhotoBtn = findViewById(R.id.take_photo);
        mTakePhotoBtn.setOnClickListener(this);
        mGoGalleryBtn = findViewById(R.id.go_gallery);
        mGoGalleryBtn.setOnClickListener(this);
        mPic = findViewById(R.id.pic);
    }


    /**
     * 动态申请权限
     */
   private void requestPerssion(){
       String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
       if (ContextCompat.checkSelfPermission(this,
               Manifest.permission.CAMERA)
               != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this,
                       permissions,
                       REQUEST_PERMISSIONS);
       }
   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //成功
                    Toast.makeText(this, "用户授权打开相机权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "用户拒绝打开相机权限", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.take_photo:
                takePhoto();
                break;
            case R.id.go_gallery:
                goGallery();
                break;
             default:
                 break;
        }
    }

    private void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUtil.mOriPicUri);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMERA);
    }

    private void goGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");// 相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void zoomPic(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);// 输出图片大小
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFileUtil.mCropPicFile));
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_CAPTURE_CAMERA:
                zoomPic(mFileUtil.mOriPicUri);
                break;
            case REQUEST_CODE_CROP:
                if (data == null) {
                    return;
                }
                mBitmap = data.getParcelableExtra("data");
                if (mBitmap!=null && mBitmap.getByteCount()>0){
                    // 有些机型需要从返回值中拿到数据进行保存，否则无法直接从申请剪裁的uri中读取到数据
                    mFileUtil.savePic(mBitmap,mFileUtil.mCropPicFile);
                    mPic.setImageBitmap(mBitmap);
                }else {
                    try{
                        Uri uri = Uri.fromFile(mFileUtil.mCropPicFile);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        mPic.setImageBitmap(bitmap);
                    }catch (Exception e){
                        Log.i(TAG, "onActivityResult: e = "+e.getMessage());
                    }

                }
                break;
            case REQUEST_CODE_PICK_IMAGE:
                if (data == null) {
                    return;
                }
                Uri uri = data.getData();
                zoomPic(uri);
                break;
            default:
                break;
        }
    }
}
