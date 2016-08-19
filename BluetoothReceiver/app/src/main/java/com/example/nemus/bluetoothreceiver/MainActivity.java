package com.example.nemus.bluetoothreceiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static BluetoothAdapter mBTAdapter;
    private static final int B_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        ImageButton find = (ImageButton)findViewById(R.id.find);

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBTAdapter = bluetoothManager.getAdapter();



        find.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                if(mBTAdapter == null || !mBTAdapter.isEnabled()){
                    Intent turnON = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnON, B_ACTIVITY);
                }else{
                    Intent mintent = new Intent(MainActivity.this, BeaconList.class);
                    startActivity(mintent);
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){
            case B_ACTIVITY: // requestCode == B_ACTIVITY
                if(resultCode == RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Bluetooth ON", Toast.LENGTH_LONG).show();
                    Intent mintent = new Intent(MainActivity.this, BeaconList.class);
                    startActivity(mintent);
                }
        }
    }

    public void bleON(View view){

    }
}
