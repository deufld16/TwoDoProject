package at.htlkaindorf.twodoprojectmaxi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.bluetooth.BluetoothManager;
import at.htlkaindorf.twodoprojectmaxi.dialogs.BluetoothDevicesFragment;

/**
 * This activity is used to allow the user to transfer his/her own To Do List
 * to another device via Bluetooth. Furthermore, it informs the user about
 * all steps during this process
 *
 * @author Maximilan Strohmaier
 */

public class TransferActivity extends AppCompatActivity {

    private Spinner spRole;
    private TextView tvInfoArea;
    private LottieAnimationView lavBluetooth;
    private ImageView ivCancel;
    private TextView tvCancel;
    private BluetoothDevicesFragment bluetoothDevicesDlg = null;

    private List<String> roles = Arrays.asList("sender", "receiver");
    private String activeRole = roles.get(0);
    private ArrayAdapter<String> roleAdapter;

    private BluetoothManager bm;
    private String infoStr = "";

    /***
     * Method to inflate the GUI and initialize vital variables
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        spRole = findViewById(R.id.spTransferRole);
        tvInfoArea = findViewById(R.id.tvTransferInfoArea);
        lavBluetooth = findViewById(R.id.lav_bluetooth);
        ivCancel = findViewById(R.id.ivTransferCancel);
        tvCancel = findViewById(R.id.tvTransferCancel);

        tvInfoArea.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvInfoArea.setVerticalScrollBarEnabled(true);
        tvInfoArea.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);

        initRoleSpinner();

        lavBluetooth.setOnClickListener(view ->
                lavBluetooth.animate()
                            .alpha(0.0f)
                            .setDuration(1000)
                            .withEndAction(() -> {
                                lavBluetooth.setVisibility(View.INVISIBLE);
                                tvInfoArea.setText("");
                                startBluetoothTransfer();
                            })
                            .start());

        ivCancel.setOnClickListener(view -> onCancel(view));
        tvCancel.setOnClickListener(view -> onCancel(view));
    }

    /***
     * Method that initializes the bluetooth transfer process using adequate classes
     */
    private void startBluetoothTransfer()
    {
        try
        {
            activeRole = (String) spRole.getSelectedItem();
            spRole.setEnabled(false);

            bm = new BluetoothManager(this, activeRole);

            //register listener for Bluetooth state changes
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            registerReceiver(bm.bluetoothChangeReceiver, filter);
        }
        catch (Exception e)
        {
            informUser(e.getMessage());
            processFailed();
        }
    }

    /***
     * Method to terminate the current transfer process and return to previous activity
     *
     * @param view
     */
    private void onCancel(View view)
    {
        if(bm != null) {
            bm.terminate();
        }
        finish();
        overridePendingTransition(0, R.anim.from_right);
    }


    /***
     * Method to initialize the role spinner with the predefined roles
     */
    private void initRoleSpinner()
    {
        roleAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_white, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);
    }

    /***
     * Method to inform the user about the progress of the transfer using a specific message
     *
     * @param msg
     */
    public synchronized void informUser(String msg)
    {
        tvInfoArea.append(msg+"\n");
    }

    /***
     * Method to inform the user about a failed process in a standardized way
     * and allow further actions
     */
    public void processFailed()
    {
        informUser("Process execution failed");
        //informUser("For another attempt press Bluetooth icon");
        if(bluetoothDevicesDlg != null)
        {
            bluetoothDevicesDlg.dismiss();
        }
        restartAll();
    }

    /***
     * Method to entirely restart the Bluetooth transfer process
     */
    public void restartAll()
    {
        if(bm != null && bm.bluetoothChangeReceiver != null) {
            unregisterReceiver(bm.bluetoothChangeReceiver);
        }
        lavBluetooth.setAlpha(1f);
        lavBluetooth.setVisibility(View.VISIBLE);

        spRole.setEnabled(true);
    }

    /***
     * Method to display specific devices and let the user decide whether he/she
     * wants to take a displayed one or do something else to get further devices
     *
     * @param srcManager
     * @param devices
     * @param title
     * @param description
     * @param furtherAction
     */
    public void displayDevices(BluetoothManager srcManager, List<BluetoothDevice> devices,
                               String title, String description, String furtherAction)
    {
        bluetoothDevicesDlg = new BluetoothDevicesFragment(srcManager, devices,
                                                title, description, furtherAction);
        bluetoothDevicesDlg.show(getSupportFragmentManager(), "bluetoothDevicesFragment");
    }

    /***
     * Method to update the set of available Bluetooth devices
     *
     * @param devices
     * @return success
     */
    public boolean updateDevices(List<BluetoothDevice> devices)
    {
        if(bluetoothDevicesDlg == null)
        {
            return false;
        }
        bluetoothDevicesDlg.setDevices(devices);
        return true;
    }

    /***
     * Method that receives results of user inputs during the transfer process
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(bm != null) {
            //Result-Handler for enabling Bluetooth
            /*if (requestCode == bm.BLUETOOTH_ENABLE_REQUEST_CODE) {
                if (resultCode == RESULT_CANCELED) {
                    processFailed();
                    return;
                } else if (resultCode == RESULT_OK) {
                    infoStr += "Bluetooth turned on - ";
                    bm.enableDiscoverability();
                }
            }*/

            //Result-Handler for enabling discoverability
            if(requestCode == bm.DISCOVERABILITY_ENABLE_REQUEST_CODE)
            {
                if(resultCode == RESULT_CANCELED)
                {
                    informUser("Error while enabling bluetooth");
                    processFailed();
                    return;
                }
                else
                {
                    informUser("Bluetooth enabled, discoverable for "+resultCode+"s");
                    bm.initDeviceRole();
                }
            }
        }
    }

    /***
     * Destructor for TransferActivity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unregister listener for Bluetooth state changes
        try {
            unregisterReceiver(bm.bluetoothChangeReceiver);
        } catch (RuntimeException rex)
        {

        }
    }
}
