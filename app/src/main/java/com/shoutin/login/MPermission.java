package com.shoutin.login;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shoutin.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public abstract class MPermission extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpermission);
    }

    public boolean mayRequestLocationAccess() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(SEND_SMS) == PackageManager.PERMISSION_GRANTED
                ) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA, READ_PHONE_STATE, READ_SMS, SEND_SMS}, 1);
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA, READ_PHONE_STATE, READ_SMS, SEND_SMS}, 1);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        System.out.println("ONE PERMISSION GRANTED");

        int intCount = 0;
        if (requestCode == 1) {
            for (int intIndex = 0; intIndex < grantResults.length; intIndex++) {
                if (grantResults[intIndex] == PackageManager.PERMISSION_DENIED) {
                    intCount++;
                }
            }
            System.out.println("PERMISSION GRANTED COUNT : " + intCount);
        }
        System.out.println("USER PRINT PERMISSION GRANTED COUNT : " + intCount);

        if (intCount > 0) {
            // all permission granted
            onPermissionsGrantedListener(intCount);
        }
        /*else {
            Snackbar.make(findViewById(android.R.id.content), "To use app features you need some app permissions. If you want to enable it. Please click on ENABLE.",
                    Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            startActivity(intent);
                        }
                    }).show();
        }*/
    }

    public abstract void onPermissionsGrantedListener(int requestCode);
}
