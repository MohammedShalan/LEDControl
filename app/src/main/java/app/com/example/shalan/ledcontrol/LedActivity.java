package app.com.example.shalan.ledcontrol;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by noura on 16/11/2016.
 */
public class LedActivity extends AppCompatActivity {

        private ProgressDialog progress;
        String address = null;

        BluetoothAdapter myBluetooth ;
        BluetoothSocket btSocket ;
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        Boolean isON  ;
        TextView ledSTATE ;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_led);

            ImageView ON = (ImageView) findViewById(R.id.on_led);
            ImageView OFF = (ImageView) findViewById(R.id.off_led);

            ledSTATE = (TextView) findViewById(R.id.LEDstate);
            ledSTATE.setText("");

            Intent intent = this.getIntent();

            //address = MainActivity.Address ;
            address = intent.getStringExtra("AddresstoSEND");
            new ConnectBT().execute();

            ON.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ONled();
                }
            });
            OFF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OFFled();
                }
            });
        }

        public void ONled(){
            try {
                btSocket.getOutputStream().write("1".toString().getBytes());
                isON = true ;
                checkLED();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void OFFled(){
            try {
                btSocket.getOutputStream().write("0".toString().getBytes());
                isON = false ;
                checkLED();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public class ConnectBT extends AsyncTask<Void,Void,Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(LedActivity.this, "Coonecting...!", "Please wait!");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progress.dismiss();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (btSocket == null) {
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice device = myBluetooth.getRemoteDevice(address);

                        btSocket = device.createRfcommSocketToServiceRecord(uuid);

                        btSocket.connect();
                    } else {
                        Log.v("TAG", "BT is USE!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return null;

            }
        }
        public void checkLED(){
            if (isON) {
                ledSTATE.setText("ON");
            }else {
                ledSTATE.setText("OFF");

            }


        }

}
