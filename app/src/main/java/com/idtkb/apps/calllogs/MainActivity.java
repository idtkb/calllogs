package com.idtkb.apps.calllogs;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_READ_CALLLOG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // Android 6.0以降は実行時にパーミッションチェックが必要
        //https://developer.android.com/reference/android/os/Build.VERSION_CODES.html
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS); API Level 15以下
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                    // パーミッションが必要であることを説明する画面の表示

                } else {
                    // 説明画面は表示せずにパーミンションの許可を求める画面を表示する
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                            REQUEST_CODE_READ_CALLLOG);
                }
            }
        //}

        showCallLogs();
    }

    //@SuppressWarnings({"MissingPermission"})
    public boolean showCallLogs() {
        ContentResolver contentResolver = getContentResolver();
        String order = CallLog.Calls.DEFAULT_SORT_ORDER;

        Cursor cursor;

        try {
            cursor = contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    null,
                    null,
                    order
            );
        } catch (SecurityException e) {
            return false;
        }

        TextView textView = (TextView)findViewById(R.id.callLogText);
        textView.setText("");

        if (cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String cachedName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                Date date = new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));

                Log.d(TAG, "DATE : " + date);
                Log.d(TAG, "NUMBER : " + number);
                Log.d(TAG, "CACHED_NAME : " + cachedName);
                Log.d(TAG, "TYPE : " + type);
                textView.append(date+" / " + number + " / " + cachedName + " / " + type + "\n");
                textView.append("----------\n");
            } while (cursor.moveToNext());
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_CODE_READ_CALLLOG == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 許可された
                showCallLogs();
            } else {
                // 拒否された
                Toast.makeText(this, "アプリの使用には通話履歴へのアクセスが必要です。", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
