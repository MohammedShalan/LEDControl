package app.com.example.shalan.ledcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    List ListDevice ;
    ListView listView ;
    BluetoothAdapter bluetoothAdapter;
    String Device= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Define the listview that you will use
        listView = (ListView) findViewById(R.id.listView);

        /* Intent for Request to open BT if it's Disabled
        *
        * */
        Intent EnableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE) ;
        startActivityForResult(EnableBT,0);
        /**
         * The Image that appear in first UI [To show how to connect HC-05 with Arduino]
         */

        ImageView imageView  = (ImageView) findViewById(R.id.imageConnect);
        // To Scale the image to put in SCREEN's Size
        scaleImage(imageView);

        /**
         * Don't Forget to USE Bluetooth Permission in Mainfest file
         *
         */

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ;
        Set<BluetoothDevice> PairedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice i:PairedDevices){
            ListDevice.add(i.getName() +"\n" + i.getAddress());
            listView.setAdapter(new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,ListDevice));

        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = ((TextView) view).getText().toString();

                Device = name.substring(name.length()- 17 );
                Intent intent = new Intent(MainActivity.this,LedActivity.class);
                intent.putExtra("Address",Device);
                startActivity(intent);
            }
        });


    }

    /**
     * Two Fundtion to Scale an Image
     * Don't Care about it.
     */

    private void scaleImage(ImageView view) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(300);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}
