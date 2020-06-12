package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import at.htlkaindorf.twodoprojectmaxi.R;
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
                bm.getSrcActivity().getString(R.string.bluetooth_discovered_devices_title_2),
                bm.getSrcActivity().getString(R.string.bluetooth_discovered_devices_body_1),
                bm.getSrcActivity().getString(R.string.bluetooth_discovered_devices_discover_devices_btn));
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
                    bm.getSrcActivity().getString(R.string.bluetooth_discovered_devices_title_1),
                    bm.getSrcActivity().getString(R.string.bluetooth_discovered_devices_body_2),
                    bm.getSrcActivity().getString(R.string.dialog_cancel));
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
        //ct.cancel();
    if(ct != null && ct.isAlive()) {
            ct.interrupt();
        }
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
        private BluetoothTransfer bt;

        public ConnectThread()
        {
            try {
                socket = partnerDevice.createRfcommSocketToServiceRecord(THE_UUID);
            } catch (IOException e) {
                srcActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srcActivity.informUser("Error while trying to connect");
                    }
                });
            }
        }

        @Override
        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                srcActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srcActivity.informUser("Connecting to: " + partnerDevice.getName());
                    }
                });
                socket.connect();
                bm.getSrcActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srcActivity.informUser("Connected to: " + partnerDevice.getName());
                    }
                });
                sendData();
                cancel();
            } catch (IOException e) {
                cancel();
                bm.getSrcActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srcActivity.informUser("Unable to connect");
                        srcActivity.processFailed();
                    }
                });
            }
        }

        /***
         * Method to process a successful connection and send the data
         */
        private void sendData()
        {
            bt = new BluetoothTransfer(socket, srcActivity, false);
            bt.start();
        }

        /***
         * Method to close the socket
         */
        private void cancel()
        {
            try {
                socket.close();
            } catch (IOException e) {
                bm.getSrcActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srcActivity.informUser("Error with Bluetooth connection");
                    }
                });
            }
        }
    }
}
