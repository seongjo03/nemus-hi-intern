package com.example.nemus.bluetoothreceiver;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by nemus on 2016-08-16.
 */
public class BeaconController extends AppCompatActivity {

	public static String data1 = "DEVICE_NAME",
			data2 = "DEVICE_ADDRESS",
			data3 = "DEVICE_TXPOWER",
			data4 = "DEVICE_MAJOR",
			data5 = "DEVICE_MINOR",
			data6 = "SENSOR";
	String mDeviceName,
		mDeviceAddress,
		mDeviceTxPower,
		mDeviceMajor,
		mDeviceMinor,
		mSensor;

	TextView t1, t2, t3, t4, t5, t6;
	Switch s1;
	RelativeLayout rl1, rl2, rl3, rl4;

	int IS_CHANGED = 0;
	int NAME_CHANGED = 0;
	int TXPOWER_CHANGED = 0;
	int MAJOR_CHANGED = 0;
	int MINOR_CHANGED = 0;


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beaconcontroller);

		getSupportActionBar().setTitle(R.string.controller);
		getSupportActionBar().setHomeAsUpIndicator(R.mipmap.inow);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(data1);
		mDeviceAddress = intent.getStringExtra(data2);
		mDeviceTxPower = intent.getStringExtra(data3);
		mDeviceMajor = intent.getStringExtra(data4);
		mDeviceMinor = intent.getStringExtra(data5);
		mSensor = intent.getStringExtra(data6);

		//Log.d("data1", data1 + " " + mDeviceName);
		//Log.d("data2", data2 + " " + mDeviceAddress);
		//Log.d("data6", data6 + " " + mSensor);

		t1 = (TextView)findViewById(R.id.device_name);
		t2 = (TextView)findViewById(R.id.device_bssid);
		t3 = (TextView)findViewById(R.id.editName);
		t4 = (TextView)findViewById(R.id.editTxPower);
		t5 = (TextView)findViewById(R.id.editMajor);
		t6 = (TextView)findViewById(R.id.editMinor);
		s1 = (Switch)findViewById(R.id.switch1);

		//t3.setOnclickListener(this);

		t1.setText(mDeviceName);
		t2.setText(mDeviceAddress);
		t3.setText(mDeviceName);
		t4.setText(mDeviceTxPower);
		t5.setText(mDeviceMajor);
		t6.setText(mDeviceMinor);
		if(mSensor.equals("ON")) s1.setChecked(true);
		else s1.setChecked(false);

		rl1 = (RelativeLayout)findViewById(R.id.namespace);
		rl2 = (RelativeLayout)findViewById(R.id.txpowerspace);
		rl3 = (RelativeLayout)findViewById(R.id.majorspace);
		rl4 = (RelativeLayout)findViewById(R.id.minorspace);

		rl1.setOnClickListener(modify);
		rl2.setOnClickListener(modify);
		rl3.setOnClickListener(modify);
		rl4.setOnClickListener(modify);

		//Log.d("s1", s1.isChecked()+"");
	}

	public View.OnClickListener modify = new View.OnClickListener() {
		public void onClick(View v) {

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BeaconController.this);
			LayoutInflater inflater = BeaconController.this.getLayoutInflater();
			final View dialogView = inflater.inflate(R.layout.editordialog, null);
			dialogBuilder.setView(dialogView);
			//Log.d("stat", "update");

			final EditText value = (EditText) dialogView.findViewById(R.id.editText);
			dialogBuilder.setNegativeButton("Cancel", null);

			//Log.d("getID",v.getId()+ "");
			switch (v.getId()) {
				case R.id.namespace:
					dialogBuilder.setTitle("Beacon Name");
					value.setInputType(InputType.TYPE_CLASS_TEXT);
					value.setHint(t3.getText());
					dialogBuilder.setPositiveButton("OK", new PositiveBtn(0, value));
					break;
				case R.id.txpowerspace:
					dialogBuilder.setTitle("Tx Power");
					value.setHint(t4.getText());
					dialogBuilder.setPositiveButton("OK", new PositiveBtn(1, value));
					break;
				case R.id.majorspace:
					dialogBuilder.setTitle("Major");
					value.setHint(t5.getText());
					dialogBuilder.setPositiveButton("OK", new PositiveBtn(2, value));
					break;
				case R.id.minorspace:
					dialogBuilder.setTitle("Minor");
					value.setHint(t6.getText());
					dialogBuilder.setPositiveButton("OK", new PositiveBtn(3, value));
					break;
			}
			dialogBuilder.show();
		}

		final class PositiveBtn implements DialogInterface.OnClickListener {
			int index = -1;
			EditText value;

			public PositiveBtn(int index, EditText value){
				this.index = index;
				this.value = value;
			}

			public void onClick(DialogInterface dialog, int which){
				String value = this.value.getText().toString();
				if(!value.equals("") && !this.value.getHint().equals(value)){

					if(index == 0){
						t3.setText(value);
						if(t3.getText().equals(mDeviceName)){
							t3.setTextColor(Color.parseColor("#aaaaaa"));
							NAME_CHANGED = 0;
						}
						else {
							t3.setTextColor(Color.parseColor("#3a8abe"));
							NAME_CHANGED = 1;
						}
					}else if(index == 1){
						t4.setText(value);
						if(t4.getText().equals(mDeviceTxPower)){
							t4.setTextColor(Color.parseColor("#aaaaaa"));
							TXPOWER_CHANGED = 0;
						}
						else {
							t4.setTextColor(Color.parseColor("#3a8abe"));
							TXPOWER_CHANGED = 1;
						}
					}else if(index == 2){
						t5.setText(value);
						if(t5.getText().equals(mDeviceMajor)){
							t5.setTextColor(Color.parseColor("#aaaaaa"));
							MAJOR_CHANGED = 0;
						}
						else {
							t5.setTextColor(Color.parseColor("#3a8abe"));
							MAJOR_CHANGED = 1;
						}
					}else{
						t6.setText(value);
						if(t6.getText().equals(mDeviceMinor)){
							t6.setTextColor(Color.parseColor("#aaaaaa"));
							MINOR_CHANGED = 0;
						}
						else {
							t6.setTextColor(Color.parseColor("#3a8abe"));
							MINOR_CHANGED = 1;
						}
					}
					IS_CHANGED = NAME_CHANGED + TXPOWER_CHANGED + MAJOR_CHANGED + MINOR_CHANGED;
				}
			}
		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					if(IS_CHANGED == 0){
						super.onBackPressed();
					}else{
						AlertDialog.Builder BackBtn = new AlertDialog.Builder(BeaconController.this);
						BackBtn.setTitle(R.string.controller);
						BackBtn.setMessage("Update setting?");
						BackBtn.setNegativeButton("No", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
						BackBtn.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						});
						BackBtn.show();
					}
					return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}
}
