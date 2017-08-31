package com.nova.wifiap;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Francis Ilechukwu 31/08/2017.
 */

public class WirelessAPManager {

    private WifiManager wifiManager;
    private WifiConfiguration apConfig;
    private WifiConfiguration tempConfig; // To Use later

    public WirelessAPManager(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public boolean setUpAccessPoint(String ssid, String password) {
        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("getWifiApConfiguration")) {
                try {
                    tempConfig = (WifiConfiguration) method.invoke(wifiManager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        for (Method method : methods) {
            Log.e("Aegis", method.getName());
            if (method.getName().equals("setWifiApEnabled")) {
                apConfig = new WifiConfiguration();
                if (password.equals("")) {
                    apConfig.SSID = ssid;
                    apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                } else {
                    apConfig.SSID = ssid;
                    apConfig.preSharedKey = password;
                    apConfig.hiddenSSID = true;
                    apConfig.status = WifiConfiguration.Status.ENABLED;
                    apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                }
                try {
                    method.invoke(wifiManager, apConfig, true);
                    wifiManager.saveConfiguration();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return false;
    }

    public boolean stopAccessPoint() {
        if (apConfig != null) {
            wifiManager.setWifiEnabled(false);
            try {
                Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                return (Boolean) method.invoke(wifiManager, apConfig, false);
                //method.invoke(wifiManager, tempConfig, true);
                // (Boolean) method.invoke(wifiManager, tempConfig, false);
                /*
                Code commented above causes android hotspot to crash till device reboot...
                to fix later.
                 */
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean startAccessPoint() {
        wifiManager.setWifiEnabled(false);
        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(wifiManager, null, true);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return false;
    }
}
