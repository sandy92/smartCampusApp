package com.atlasbots.scapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;

import com.atlasbots.scapp.adapter.CreateEventFragment;
import com.atlasbots.scapp.adapter.HomeFragment;
import com.atlasbots.scapp.adapter.NavDrawerListAdapter;
import com.atlasbots.scapp.adapter.RecommendedFragment;
import com.atlasbots.scapp.adapter.StarredFragment;
import com.atlasbots.scapp.models.NavDrawerItem;
import com.atlasbots.scapp.R;
import com.atlasbots.scapp.references.Constants;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends Activity implements BeaconConsumer, RangeNotifier {
    private BeaconManager mBeaconManager;
    Utils utils = new Utils();
    Map<String, String> availableBeacons = new HashMap<>();
    private String deviceId = "";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceId = m_BluetoothAdapter.getAddress();


//        WebView myWebView = (WebView) findViewById(R.id.homewebview);
        //System.out.println("Here is the url : " + Constants.urls.getEventsUrl + generateQueryParams(deviceId, availableBeacons));
//        utils.renderPage(Constants.urls.getEventsUrl + generateQueryParams(deviceId, availableBeacons), myWebView);
        mTitle = mDrawerTitle = getTitle();
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));

        navMenuIcons.recycle();

        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            //displayView(0);
        }
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                //Home fragment
//                fragment = new HomeFragment();
//                break;
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            case 1:
                //categories
                fragment = new HomeFragment();
                break;
            case 2:
                //recommended events
                fragment = new RecommendedFragment();
                break;
            case 3:
                //starred events
                fragment = new StarredFragment();
                break;
            case 4:
                //create event
                fragment = new CreateEventFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        verifyBluetooth();
        /*mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        mBeaconManager.bind(this);*/
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
        for (Beacon beacon : beacons) {
            Identifier nameSpaceId = beacon.getId1();
            Identifier instanceId = beacon.getId2();
            if (!availableBeacons.keySet().contains(nameSpaceId)) {
                availableBeacons.put(nameSpaceId.toString(), instanceId.toString());
            }
            System.out.println(beacon.getId1() + " " + beacon.getId2());
        }

        onPause();
    }

    @Override
    public void onPause() {
        runOnUiThread(new Runnable() {
            public void run() {
                WebView myWebView = (WebView) findViewById(R.id.homewebview);
                System.out.println("Here is the url : " + Constants.urls.getEventsUrl + generateQueryParams(deviceId, availableBeacons));
                utils.renderPage(Constants.urls.getEventsUrl + generateQueryParams(deviceId, availableBeacons), myWebView);
            }
        });
        super.onPause();

        mBeaconManager.unbind(this);
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

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();

            }
            else{

                mBeaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
                // Detect the main Eddystone-UID frame:
                mBeaconManager.getBeaconParsers().add(new BeaconParser().
                        setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
                mBeaconManager.bind(this);

            }
        }
        catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }
}
