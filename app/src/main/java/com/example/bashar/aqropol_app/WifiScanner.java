package com.example.bashar.aqropol_app;


/**
 * Created by admin on 21/03/2018.
 */

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Pierre on 28/02/2018.
 */

public class WifiScanner extends AsyncTask {

    private WifiManager wifiManager;
    private String ipAddress = "";

    public WifiScanner(WifiManager w){
        this.wifiManager=w;
    }

    protected Object doInBackground(Object[] objects) {
        //CF : http://www.includehelp.com/code-snippets/android-application-to-display-available-wifi-network-and-connect-with-specific-network.aspx
        wifiManager.setWifiEnabled(true);
        String ssid = "my-ap";//changer le nom en fonction du capteur
        String pass = "ZuKsfX23";//changer le mdp en fonction du capteur
        //WifiConfiguration wifiConfig = myApManager.setUpWifiConfiguration(ssid, pass);

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", pass);

        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + ssid + "\"\"";
        conf.preSharedKey = "\"" + pass + "\"";
        wifiManager.addNetwork(conf);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        while(wifiInfo.getNetworkId()==-1){
            wifiInfo = wifiManager.getConnectionInfo();
        }

        ipAddress = wifiInfo.getIpAddress()+"";

        Log.i("ça", "fonctionne");

        return null;
    }

    public String getIpAddress(){
        return this.ipAddress;
    }

}


