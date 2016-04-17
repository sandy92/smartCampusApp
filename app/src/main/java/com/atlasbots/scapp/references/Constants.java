package com.atlasbots.scapp.references;

/**
 * Created by sank on 4/11/16.
 */
public class Constants {

    static public class urls {
        public static final String playService = "http://192.168.43.177:9000/";
        public static final String getEventsUrl = playService + "beacons";
        public static final String noBeaconsUrl = playService + "noBeacons";
        public static final String BEACON_ID = "beaconId=";
        public static final String DEVICE_ID = "deviceId=";
    }
}
