package com.example.nemus.bluetoothreceiver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nemus on 2016-08-16.
 */
public class BeaconController extends AppCompatActivity {

	public static String data1 = "DEVICE_NAME", data2 = "DEVICE_ADDRESS", data3;
	TextView t1, t2, t3, t4, t5, t6;


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beaconcontroller);

		final Intent intent = getIntent();
		String mDeviceName = intent.getStringExtra(data1);
		String mDeviceAddress = intent.getStringExtra(data2);

		Log.d("data1", data1 + " " + mDeviceName);
		Log.d("data2", data2 + " " + mDeviceAddress);

		t1 = (TextView)findViewById(R.id.device_name);
		t2 = (TextView)findViewById(R.id.device_bssid);
		t3 = (TextView)findViewById(R.id.editName);
		t4 = (TextView)findViewById(R.id.editTxPower);
		t5 = (TextView)findViewById(R.id.editMajor);
		t6 = (TextView)findViewById(R.id.editMinor);

		//t3.setOnclickListener(this);

		t1.setText(mDeviceName);
		t2.setText(mDeviceAddress);
	}


	public void onClick(View v){
		switch(v.getId()){
			case R.id.editName:
				Log.d("clickme", "hi");
		}
	}

}
