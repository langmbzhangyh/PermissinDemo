package com.example.permissiondemo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BaseActivity extends AppCompatActivity {
    private static PermissionListener mListtener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);

    }

    public static void requestRuntimePermissions(String[] permissions, PermissionListener listener) {
        mListtener = listener;
        Activity topActivity = ActivityCollector.getTopActivity();
        if (topActivity == null) {
            return;
        } else {
            ArrayList<String> permissionsList = new ArrayList<>();// 创建一个集合
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsList.add(permission);
                }
            }
            if (!permissionsList.isEmpty()) {
                //申请开通权限
                ActivityCompat.requestPermissions(topActivity, permissionsList.toArray(new String[permissionsList.size()]), 1);
            } else {
                //你所申请的权限都已经开启，可以运行你想要的操作了。
                mListtener.onGrande();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (!deniedPermissions.isEmpty()) {
                        mListtener.onGrande();
                    } else {
                        mListtener.onDenied(deniedPermissions);
                    }
                    //如果没有被return,说明你所申请的权限都已经被同意，可以去做想做的事情
                }
                break;
            default:
                break;
        }

    }
}
