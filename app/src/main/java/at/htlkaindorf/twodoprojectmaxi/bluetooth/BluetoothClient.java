package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import at.htlkaindorf.twodoprojectmaxi.activities.TransferActivity;

public class BluetoothClient
{
    private BluetoothManager bm;
    private BluetoothAdapter bluetoothAdapter;
    private TransferActivity srcActivity;
    private final UUID THE_UUID;

    private List<BluetoothDevice> deviceList;
    private BluetoothDevice partnerDevice;
    private ConnectThread ct;

    public BluetoothClient(BluetoothManager bm, BluetoothAdapter bluetoothAdapter, TransferActivity srcActivity, UUID THE_UUID) {
        this.bm = bm;
        this.bluetoothAdapter = bluetoothAdapter;
        this.srcActivity = srcActivity;
        this.THE_UUID = THE_UUID;
    }

    /***
     * Method to get paired devices and let the user decide on one
     * resp. demand a discovering process
     */
    public void queryPairedDevices()
    {
        deviceList = bluetoothAdapter.getBondedDevices().stream().collect(Collectors.toList());
        srcActivity.displayDevices(bm, deviceList,
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
            srcActivity.displayDevices(bm, deviceList,
                    "Discovered Devices",
                    "Please wait and choose a discovered device",
                    "Cancel");
        }
    }

    /***
     * Method to process a device selection and the initialization of a connection attempt
     */
    public void connectWith(BluetoothDevice selectedDevice)
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

        ct = new ConnectThread();
        ct.start();
    }

    /***
     * Method to terminate the Bluetooth-Client process
     */
    public void closeConnection()
    {
        ct.cancel();
        ct.interrupt();
    }

    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    /***
     * Class to attempt a connection to the partner device
     *
     * @author Maximilian Strohmaier
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket socket = null;

        public ConnectThread()
        {
            try {
                socket = partnerDevice.createRfcommSocketToServiceRecord(THE_UUID);
            } catch (IOException e) {
                srcActivity.informUser("Error while trying to connect");
            }
        }

        @Override
        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                srcActivity.informUser("Connecting to: " + partnerDevice.getName());
                socket.connect();
                srcActivity.informUser("Connected to: " + partnerDevice.getName());
                //TODO: client-side process with opened connection
            } catch (IOException e) {
                srcActivity.informUser("Unable to connect");
                cancel();
            }
        }

        /***
         * Method to close the socket
         */
        private void cancel()
        {
            try {
                socket.close();
            } catch (IOException e) {
                srcActivity.informUser("Error with Bluetooth connection");
            }
        }
    }
}
