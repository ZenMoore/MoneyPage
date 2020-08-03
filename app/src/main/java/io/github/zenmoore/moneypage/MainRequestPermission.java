package io.github.zenmoore.moneypage;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class MainRequestPermission implements RequestPermission {

    @Override
    public void writeExternalStorage(Activity activity) {
        //检查这个权限是否已经获取
        int checkWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkWriteExternalStoragePermission!= PackageManager.PERMISSION_GRANTED){
            //如果没有权限则获取权限 requestCode在后面回调中会用到
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
        }
    }
}
