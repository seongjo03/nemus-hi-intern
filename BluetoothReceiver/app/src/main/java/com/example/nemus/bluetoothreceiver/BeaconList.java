
package com.example.nemus.bluetoothreceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nemus on 2016-08-10.
 */
public class BeaconList extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter = MainActivity.mBTAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private final static int REQUEST_ENABLE_BT = 1;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private ListView BeaconListView;


    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beaconlist);

        BeaconListView = (ListView) findViewById(R.id.list);
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        BeaconListView.setAdapter(mLeDeviceListAdapter);

        mHandler = new Handler();
        setCustomActionbar();
    }

    void setCustomActionbar() {
        /*
        // set logo on actionbar
        getSupportActionBar().setLogo(R.mipmap.inowb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        */

        getSupportActionBar().setTitle(R.string.app_title);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.inow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blmenu, menu);

        if (!mScanning) {
            menu.findItem(R.id.menu_refresh).setActionView(null);
            menu.findItem(R.id.menu_stop).setEnabled(false);
            menu.findItem(R.id.menu_scan).setEnabled(true);
        } else {
            menu.findItem(R.id.menu_stop).setEnabled(true);
            menu.findItem(R.id.menu_scan).setEnabled(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device. If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            finish();
            return;
        }

        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        final LeScanRecord device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;

        //final Intent intent = new Intent(this, DeviceControlActivity.class);
        //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        //startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            //Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<LeScanRecord> mLeDevices;
        private ArrayList<BluetoothDevice> CheckDup;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            CheckDup = new ArrayList<BluetoothDevice>();
            mLeDevices = new ArrayList<LeScanRecord>();
            mInflator = BeaconList.this.getLayoutInflater();
        }

        public void add(LeScanRecord object){

            if(!CheckDup.contains(object.device)) {
                CheckDup.add(object.device);
                mLeDevices.add(object);
            }else {
                for (int i = 0; i < mLeDevices.size(); i++) {
                    if(mLeDevices.get(i).device.getAddress().equals(object.device.getAddress())){
                        mLeDevices.set(i, object);
                        break;
                    }
                }
            }

            rssiSort(mLeDevices);
            /*
            // Test sorting result
            for(int i = 0; i < mLeDevices.size(); i++){
                Log.d("List " + i, mLeDevices.get(i).device.getAddress() + " " + mLeDevices.get(i).rssi);
            }
            Log.d("List Fin", "--------------------------");
            */
        }

        public LeScanRecord getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            CheckDup.clear();
        }

        public int getCount() {
            return mLeDevices.size();
        }

        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        public long getItemId(int i) {
            return i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // Gerneral ListView optimization code.
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.ListBackground = (LinearLayout)view.findViewById(R.id.background);
            viewHolder.deviceIcon = (ImageView)view.findViewById(R.id.device_icon);
            viewHolder.deviceBatteryIcon = (ImageView)view.findViewById(R.id.device_batteryicon);
            viewHolder.deviceBattery = (TextView)view.findViewById(R.id.device_battery);
            viewHolder.deviceName = (TextView)view.findViewById(R.id.device_name);
            viewHolder.deviceBssid = (TextView)view.findViewById(R.id.device_bssid);
            viewHolder.deviceUuid = (TextView)view.findViewById(R.id.device_uuid);
            viewHolder.deviceRssi = (TextView)view.findViewById(R.id.device_rssi);
            viewHolder.deviceMajor = (TextView)view.findViewById(R.id.device_major);
            viewHolder.deviceMinor = (TextView)view.findViewById(R.id.device_minor);
            viewHolder.deviceTemp = (TextView)view.findViewById(R.id.device_temp);
            viewHolder.deviceHum = (TextView)view.findViewById(R.id.device_hum);
            view.setTag(viewHolder);

            LeScanRecord device = mLeDevices.get(i);
            final String deviceName = device.device.getName();
            if (deviceName != null && deviceName.length() > 0){
                viewHolder.deviceName.setText(deviceName);
            }
            else {
                viewHolder.deviceName.setText(R.string.unknown_device);
            }

            if(i%2 == 1){
                viewHolder.ListBackground.setBackgroundColor(Color.parseColor("#f9f9f9"));
            }else{
                viewHolder.ListBackground.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            //final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
                if (deviceName.equals("iNOW") || deviceName.equals("iNOW_SETTING") || deviceName.equals("iNOWB")) {
                    viewHolder.deviceIcon.setImageResource(R.mipmap.inowb);
                }else {
                    viewHolder.deviceIcon.setImageResource(R.mipmap.unknown_text);
                }
            }else {
                viewHolder.deviceName.setText(R.string.unknown_device);
            }

            viewHolder.deviceBssid.setText(device.device.getAddress());

            String rssis = "";
            rssis = rssis.valueOf(device.rssi);

            String tempview;
            if(device.temp == 128) tempview = "off";
            else tempview = String.format("%.0f", device.temp);

            String humview;
            if(device.hum == 255) humview = "off";
            else humview = String.valueOf(device.hum);

            String batteryview;
            if(device.battery == 500) batteryview = "";
            else{
                batteryview = String.valueOf(device.battery);
                if(device.battery >= 75) {
                    viewHolder.deviceBatteryIcon.setImageResource(R.mipmap.battery_3);
                }else if(device.battery >= 30){
                    viewHolder.deviceBatteryIcon.setImageResource(R.mipmap.battery_2);
                }else{
                    viewHolder.deviceBatteryIcon.setImageResource(R.mipmap.battery_1);
                }
            }

            viewHolder.deviceBattery.setText(batteryview+"%");
            viewHolder.deviceRssi.setText(rssis);
            viewHolder.deviceUuid.setText(device.uuid);
            viewHolder.deviceMajor.setText(device.major+"");
            viewHolder.deviceMinor.setText(device.minor+"");
            viewHolder.deviceTemp.setText(tempview);
            viewHolder.deviceHum.setText(humview);


            return view;
        }
    }

    // Device scan callback
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //*********************************
                            int startByte = 2;
                            boolean patternFound = false;
                            while (startByte <= 5) {
                                if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                                    patternFound = true;
                                    break;
                                }
                                startByte++;
                            }

                            if (patternFound) {

                                byte[] responseBytes = new byte[31];
                                System.arraycopy(scanRecord, 31, responseBytes, 0, 31);
                                String hexString2 = bytesToHex(responseBytes);
                                String comp1 = hexString2.substring(14,18);
                                String comp2 = hexString2.substring(24,28);

                                boolean IS_iNOW = false;
                                float temp = 500;
                                int hum = 500;
                                int battery = 500;

                                //String comp = hexString2.substring(14,26);

                                if(comp1.equals("2018")){
                                    String comp3 = hexString2.substring(40,44);
                                    if (comp3.equals("2118")){
                                        IS_iNOW = true;

                                        String toFloat = (scanRecord[40] & 0xff) + "." + (scanRecord[41] & 0xff);
                                        temp = Float.parseFloat(toFloat);

                                        temp = (scanRecord[41] & 0xff);
                                        hum = ((int)scanRecord[42] & 0xff);
                                        float tem = Float.valueOf(scanRecord[43] & 0xff);
                                        battery = (int)convertToBatteryLevel(tem);

                                        /*
                                        if(device.getName() != null){
                                            if(device.getName().equals("iNOW")){


                                        //if(device.getAddress().equals("EC:BC:CF:77:26:EE") || device.getAddress().equals("F2:6C:2B:48:84:C7")){
                                            String comp4 = hexString2.substring(14, 26);
                                            String comp5 = hexString2.substring(0);
                                            Log.d("iNOW info", device.getAddress() + " " + comp4 + " " + toFloat + "." + hum + "." + battery);
                                            Log.d("iNOW info", "  ┖" + comp5 + " " + tem);
                                            }
                                        }
                                        */

                                    }
                                }else if(comp2.equals("2018")){
                                    IS_iNOW = true;

                                    String toFloat = (scanRecord[46] & 0xff) + "." + (scanRecord[47] & 0xff);
                                    temp = Float.parseFloat(toFloat);
                                    hum = ((int)scanRecord[48] & 0xff);
                                    battery = ((int)scanRecord[49] & 0xff);
                                }

                                if(IS_iNOW){
                                    //Convert to hex String
                                    byte[] uuidBytes = new byte[16];
                                    System.arraycopy(scanRecord, startByte+4, uuidBytes, 0, 16);
                                    String hexString = bytesToHex(uuidBytes);

                                    //Here is your UUID
                                    String uuid =  hexString.substring(0,8) + "-" +
                                            hexString.substring(8,12) + "-" +
                                            hexString.substring(12,16) + "-" +
                                            hexString.substring(16,20) + "-" +
                                            hexString.substring(20,32);

                                    //Here is your Major value
                                    int major = (scanRecord[startByte+20] & 0xff) * 0x100 + (scanRecord[startByte+21] & 0xff);

                                    //Here is your Minor value
                                    int minor = (scanRecord[startByte+22] & 0xff) * 0x100 + (scanRecord[startByte+23] & 0xff);

                                    //Log.d("check", tempsend + "");
                                    mLeDeviceListAdapter.add(new LeScanRecord(device, rssi, uuid, major, minor, (int)temp, hum, battery));
                                    mLeDeviceListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            };

    public static float convertToBatteryLevel(float sensorValue) {

        if (0xAC < sensorValue) return 100;
        else if (0xAA < sensorValue) return 95;
        else if (0xA8 < sensorValue) return 90;
        else if (0xA7 < sensorValue) return 85;
        else if (0xA5 < sensorValue) return 80;
        else if (0xA3 < sensorValue) return 75;
        else if (0xA2 < sensorValue) return 70;
        else if (0xA0 < sensorValue) return 65;
        else if (0x9F < sensorValue) return 60;
        else if (0x9E < sensorValue) return 55;
        else if (0x9C < sensorValue) return 50;
        else if (0x9B < sensorValue) return 45;
        else if (0x9A < sensorValue) return 40;
        else if (0x96 < sensorValue) return 35;
        else if (0x8C < sensorValue) return 30;
        else if (0x83 < sensorValue) return 25;
        else if (0x7E < sensorValue) return 20;
        else if (0x79 < sensorValue) return 15;
        else if (0x72 < sensorValue) return 10;
        else if (0x68 < sensorValue) return 5;
        else return 0;
    }

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    static class ViewHolder {
        LinearLayout ListBackground;
        ImageView deviceIcon;
        ImageView deviceBatteryIcon;
        TextView deviceBattery;
        TextView deviceName;
        TextView deviceBssid;
        TextView deviceUuid;
        TextView deviceRssi;
        TextView deviceMajor;
        TextView deviceMinor;
        TextView deviceTemp;
        TextView deviceHum;
    }

    static class LeScanRecord {
        public final BluetoothDevice device;
        public int rssi;
        public String uuid;
        public int major;
        public int minor;
        public float temp;
        public int hum;
        public int battery;

        public LeScanRecord(BluetoothDevice device, int rssi, String uuid, int major, int minor, int temp, int hum, int battery) {
            this.device = device;
            this.rssi = rssi;
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;
            this.temp = temp;
            this.hum = hum;
            this.battery = battery;
        }
    }

    // Sort ArrayList by rssi values using insertion sort
    public void rssiSort(ArrayList<LeScanRecord> object){
        for(int i = 1; i < object.size(); i++){
            LeScanRecord temp = object.get(i);
            int aux = i - 1;

            while((aux >= 0) && (object.get(aux).rssi < temp.rssi)){
                object.set(aux+1, object.get(aux));
                aux--;
            }
            object.set(aux+1, temp);
        }
    }
}