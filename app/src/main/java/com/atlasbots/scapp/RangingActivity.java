package com.atlasbots.scapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.provider.Settings.Secure;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.atlasbots.scapp.references.Constants;

/**
 * Created by sank on 4/10/16.
 */
public class RangingActivity extends Activity implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;
    Utils utils = new Utils();
    Map<String, String> availableBeacons = new HashMap<>();
    private String deviceId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ranging);
        deviceId = Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID);
    /*    mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        mBeaconManager.bind(this); */

    }

    @Override
    public void onResume() {
        super.onResume();
        mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        mBeaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(this);


    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
//        for (Beacon beacon : beacons) {
//            if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
//                // This is a Eddystone-UID frame
//                Identifier namespaceId = beacon.getId1();
//                Identifier instanceId = beacon.getId2();
//                System.out.println("I see a beacon transmitting namespace id: " + namespaceId +
//                        " and instance id: " + instanceId +
//                        " approximately " + beacon.getDistance() + " meters away.");
//            }
//        }
        for (Beacon beacon : beacons) {
            Identifier nameSpaceId = beacon.getId1();
            Identifier instanceId = beacon.getId2();
            if (!availableBeacons.keySet().contains(nameSpaceId)) {
                availableBeacons.put(nameSpaceId.toString(), instanceId.toString());
            }
            System.out.println(beacon.getId1() + " " + beacon.getId2());
        }

        onPause();
//       runOnUiThread(new Runnable() {
//           public void run() {
//               WebView myWebView = (WebView) findViewById(R.id.webview);
//               utils.renderPage("http://10.136.109.45:9000/", myWebView);
//           }
//       });
    }

    @Override
    public void onPause() {
        super.onPause();
        runOnUiThread(new Runnable() {
            public void run() {
                WebView myWebView = (WebView) findViewById(R.id.webview);
                System.out.println("Here is the url : " + Constants.urls.getEventsUrl + generateQueryParams(deviceId, availableBeacons));
                utils.renderPage(Constants.urls.getEventsUrl + generateQueryParams(deviceId, availableBeacons), myWebView);
            }
        });
        mBeaconManager.unbind(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ranging, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String generateQueryParams(String deviceId, Map<String, String> availableBeacons) {
        String tmp = "?";
        Iterator<String> iter = availableBeacons.keySet().iterator();
        while (iter.hasNext()) {
            tmp = tmp + Constants.urls.BEACON_ID + iter.next() + "&";
        }
        tmp = tmp + Constants.urls.DEVICE_ID + deviceId;
        return tmp;
    }
}