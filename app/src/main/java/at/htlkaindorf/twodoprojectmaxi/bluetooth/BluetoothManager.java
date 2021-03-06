package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.TransferActivity;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

/***
 * Class that handles all relevant steps for the bluetooth communication
 *
 * @author Maximilian Strohmaier
 */

public class BluetoothManager
{
    private String role = "";
    private BluetoothServer server = null;
    private BluetoothClient client = null;

    private BluetoothAdapter bluetoothAdapter;
    private TransferActivity srcActivity;
    private boolean processDone = true;

    private int discoverDur = 120;
    private final UUID THE_UUID = UUID.fromString("9fb97589-7ad8-4e08-8a28-db02cb7625d9");

    public final int BLUETOOTH_ENABLE_REQUEST_CODE = 1;
    public final int DISCOVERABILITY_ENABLE_REQUEST_CODE = 2;

    //Listener for changes concerning Bluetooth state, device detection etc.
    public final BroadcastReceiver bluetoothChangeReceiver = new BroadcastReceiver() {
        /***
         * Listener for changes concerning Bluetooth on the device
         *
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            //Processing when Bluetooth was manually disabled
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);

                if(state == BluetoothAdapter.STATE_OFF) {
                    srcActivity.informUser(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_turned_off));
                    if(!processDone) {
                        srcActivity.processFailed();
                    }
                }
            }

            //Processing when a device was found during the discovering process
            if(client != null && BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice bDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                List<BluetoothDevice> deviceList = client.getDeviceList();
                if(bDevice != null
                        && deviceList != null
                        && bDevice.getName() != null
                        && !bDevice.getName().equals("")
                        && !deviceList.contains(bDevice)) {
                    deviceList.add(bDevice);
                }
                client.setDeviceList(deviceList);
                srcActivity.updateDevices(deviceList);
            }
        }
    };

    public BluetoothManager(AppCompatActivity srcActivity, String role) throws Exception {
        processDone = false;
        this.srcActivity = (TransferActivity) srcActivity;
        this.role = role;

        //check Bluetooth availability
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            throw new Exception(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_error_3));
        }

        enableBluetoothDiscoverability();
    }

    /***
     * Method to enable Bluetooth as well as the discoverability of the device to certain amount of time
     */
    private void enableBluetoothDiscoverability()
    {
        if(!bluetoothAdapter.isEnabled()
                || bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverDur);
            srcActivity.startActivityForResult(discoverableIntent, DISCOVERABILITY_ENABLE_REQUEST_CODE);
        }
        else
        {
            if(Proxy.getLanguageContext().getString(R.string.language_is_stupid_2).equalsIgnoreCase("-")){
                srcActivity.informUser(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_enabled)+discoverDur+"s");
            }else{
                srcActivity.informUser(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_enabled)+discoverDur+"s " + Proxy.getLanguageContext().getString(R.string.language_is_stupid_2));
            }
            initDeviceRole();
        }
    }

    /***
     * Method to define further actions due to the selected role
     */
    public void initDeviceRole()
    {
        if(role.equals(Proxy.getLanguageContext().getString(R.string.bluetooth_sender)))
        {
            //device will act as a client
            client = new BluetoothClient(this, bluetoothAdapter, srcActivity, THE_UUID);
            client.queryPairedDevices();
        }
        else if(role.equals(Proxy.getLanguageContext().getString(R.string.bluetooth_receiver)))
        {
            //device will act as a server
            server = new BluetoothServer(this, THE_UUID);
            server.runServer();
        }
    }

    /***
     * Method that manages a discovering request
     */
    public void discoverDevices()
    {
        client.discoverDevices();
    }

    /***
     * Method that manages a request to define a partner device and connect to it
     *
     * @param device
     */
    public void connectWith(BluetoothDevice device)
    {
        client.connectWith(device);
    }

    /***
     * Method to execute all relevant steps for an early termination of the Bluetooth Transfer process
     */
    public void terminate()
    {
        if(server != null) {
            server.cancelListening();
        }
        if (client != null)
        {
            client.cancelConnection();
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public TransferActivity getSrcActivity() {
        return srcActivity;
    }
}
