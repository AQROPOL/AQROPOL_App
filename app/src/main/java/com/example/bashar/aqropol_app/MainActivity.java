package com.example.bashar.aqropol_app;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private AllData ad = new AllData("", "");
    private AndroidToNuc hdu = new AndroidToNuc(ad);
    private AndroidToServer atbdd = new AndroidToServer(ad);
    private WifiManager wifiManager;
    private WifiScanner wifiScanner = new WifiScanner(wifiManager);
    private String ipAdress = "";

    public static ProgressBar mProgressBar;

    /***********************************TRAITEMENT DE PERMISSION****************************************/
    private Activity activity = MainActivity.this;
    String wantPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST = 112;
    private boolean permissionIsGranted = false;

    private void checkPermission(String permission){
       /*L'appel de la boîte de dialogue de vérification des autorisations
        n'est nécessaire que si le périphérique exécute l'API 23 ou une version ultérieure.*/
        if (Build.VERSION.SDK_INT >= 23) {

            //Verifier la pérmission
            int result = ContextCompat.checkSelfPermission(activity, permission);

            if (result != PackageManager.PERMISSION_GRANTED){ //demander la permission si on l'a pas
                ActivityCompat.requestPermissions(activity,
                        new String[]{wantPermission},
                        REQUEST);
            } else { //J'ai déjà la permission
                permissionIsGranted=true;
            }
        } else { //SDK < 23 pas besoin de demander la permission
            permissionIsGranted=true;
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionIsGranted=true;
                } else {
                    Toast.makeText(activity, "The app was not allowed to write in your storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    /***********************************TRAITEMENT DE PERMISSION****************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.nucChargeBar);
        checkPermission(wantPermission);
    }

    public void connectWifi(View v){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(this.WIFI_SERVICE);
        //wifiManager.setWifiEnabled(true);
        wifiScanner = new WifiScanner(wifiManager);
        wifiScanner.execute();
        ipAdress=wifiScanner.getIpAddress();
    }

    public void deconnectWifi(View v){
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(this.WIFI_SERVICE);
        //boolean wasEnabled = wifiManager.isWifiEnabled();
        wifiManager.setWifiEnabled(false);
    }

    public void dlFile(View v){
        mProgressBar.setProgress(0);
        hdu.execute();
    }

    public void sendToBdd(View v){
        atbdd.execute();
    }

    public static void progress(int p){
        mProgressBar.setProgress(p);
    }
}



