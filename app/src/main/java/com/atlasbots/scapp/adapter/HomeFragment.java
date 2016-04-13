package com.atlasbots.scapp.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.atlasbots.scapp.R;
import com.atlasbots.scapp.Utils;
import com.atlasbots.scapp.references.Constants;

/**
 * Created by smitha on 4/11/16.
 */
public class HomeFragment extends Fragment {

    Utils utils = new Utils();

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

           View v=inflater.inflate(R.layout.fragment_home, container, false);
            WebView mWebView = (WebView) v.findViewById(R.id.webview);
            utils.renderPage(Constants.urls.getEventsUrl, mWebView);

           return v;
       }


}

