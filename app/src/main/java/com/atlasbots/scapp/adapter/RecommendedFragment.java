package com.atlasbots.scapp.adapter;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.atlasbots.scapp.R;
import com.atlasbots.scapp.Utils;
import com.atlasbots.scapp.references.Constants;

/**
 * Created by smitha on 4/12/16.
 */
public class RecommendedFragment extends Fragment {
    Utils utils = new Utils();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String deviceId = bluetoothAdapter.getAddress();

        WebView mWebView = (WebView) v.findViewById(R.id.webview);
        utils.renderPage(Constants.urls.playService + "recommendedEvents/" + deviceId + "/", mWebView);
        return v;
    }
}

