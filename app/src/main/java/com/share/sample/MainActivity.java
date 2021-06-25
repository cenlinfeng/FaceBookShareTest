package com.share.sample;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    
    private Button mBtnShareImage;
    private static CallbackManager mCallbackManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mCallbackManager = CallbackManager.Factory.create();
        
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                android.util.Log.d("test_", "facebook login onSuccess");
                AccessToken accessToken = loginResult.getAccessToken();
            }
            
            @Override
            public void onCancel() {
            
            }
            
            @Override
            public void onError(FacebookException error) {
            
            }
        });
        
        
        mBtnShareImage = findViewById(R.id.btn_share_iamge);
        mBtnShareImage.setOnClickListener(this);
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share_iamge:
                shareImage();
                break;
        }
    }
    
    
    private void shareImage() {
        Drawable drawable = getResources().getDrawable(R.drawable.share_image);
        drawableToFile(drawable, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "share_image.jpg", Bitmap.CompressFormat.JPEG);
        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "share_image.jpg";
        Uri imageUri = null;
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(new File(path));
        } else {
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(path));
        }
        android.util.Log.d("test_", "imageUri.getPath() = " + imageUri.getPath());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        //ShareDialog.show(this, content);
        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("test_", "onSuccess = > " + result.toString());
            }
            
            @Override
            public void onCancel() {
                Log.d("test_", "cancel = > ");
                
            }
            
            @Override
            public void onError(FacebookException error) {
                Log.d("test_", "onError = > " + error.toString());
                
            }
        });
        
        
        shareDialog.show(this, content);
        
    }
    
    //Bitmap.CompressFormat.PNG
    public void drawableToFile(Drawable drawable, String filePath, Bitmap.CompressFormat format) {
        if (drawable == null)
            return;
        try {
            File file = new File(filePath);
            
            if (file.exists())
                file.delete();
            
            if (!file.exists())
                file.createNewFile();
            
            FileOutputStream out = null;
            out = new FileOutputStream(file);
            ((BitmapDrawable) drawable).getBitmap().compress(format, 100, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}