package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import at.htlkaindorf.twodoprojectmaxi.activities.TransferActivity;

/***
 * Class that handles all relevant steps for the bluetooth communication
 *
 * @author Maximilian Strohmaier
 */

public class BluetoothManager
{
    private BluetoothAdapter bluetoothAdapter;
    private TransferActivity srcActivity;
    private boolean processDone = true;
    private List<BluetoothDevice> deviceList;
    private BluetoothDevice partnerDevice;

    public final int BLUETOOTH_ENABLE_REQUEST_CODE = 1;

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
                    srcActivity.informUser("Bluetooth turned off");
                    if(!processDone) {
                        srcActivity.processFailed();
                    }
                }
            }

            //Processing when a device was found during the discovering process
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice bDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!deviceList.contains(bDevice)) {
                    deviceList.add(bDevice);
                }
                srcActivity.updateDevices(deviceList);
            }
        }
    };

    public BluetoothManager(AppCompatActivity srcActivity) throws Exception {
        processDone = false;
        this.srcActivity = (TransferActivity) srcActivity;

        //check Bluetooth availability
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
        {
            throw new Exception("Device does not support bluetooth");
        }

        enableBluetooth();
    }

    /***
     * Method to enable Bluetooth on device
     */
    private void enableBluetooth()
    {
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enaBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            srcActivity.startActivityForResult(enaBluetoothIntent, BLUETOOTH_ENABLE_REQUEST_CODE);
        }
        else
        {
            srcActivity.informUser("Bluetooth turned on");
            queryPairedDevices();
        }
    }

    /***
     * Method to get paired devices and let the user decide on one
     * resp. demand a discovering process
     */
    public void queryPairedDevices()
    {
        deviceList = bluetoothAdapter.getBondedDevices().stream().collect(Collectors.toList());
        srcActivity.displayDevices(this, deviceList,
                                                        "Paired Devices",
                                                    "Please choose a paired device",
                                                    "Discover Further Devices");
        srcActivity.informUser("Querying paired devices");
    }

    /***
     * Method to discover non-paired devices
     */
    public void discoverDevices()
    {
        if(partnerDevice == null)
        {
            bluetoothAdapter.startDiscovery();
            srcActivity.informUser("Bluetooth discovery has started");
            srcActivity.displayDevices(this, deviceList,
                    "Discovered Devices",
                    "Please wait and choose a discovered device",
                    "Cancel");
        }
    }

    /***
     * Method to process a device selection
     */
    public void definePartnerDevice(BluetoothDevice selectedDevice)
    {
        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            srcActivity.informUser("Bluetooth discovery has stopped");
        }

        if(selectedDevice == null)
        {
            srcActivity.informUser("No device has been selected");
            srcActivity.processFailed();
            return;
        }

        partnerDevice = selectedDevice;
        srcActivity.informUser("Selected device: " + partnerDevice.getName());
    }
}
