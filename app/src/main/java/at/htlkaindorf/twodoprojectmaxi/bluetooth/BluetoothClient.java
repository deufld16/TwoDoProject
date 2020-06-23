package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.activities.TransferActivity;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.io.AttachmentIO;

public class BluetoothClient
{
    private BluetoothManager bm;
    private BluetoothAdapter bluetoothAdapter;
    private TransferActivity srcActivity;
    private final UUID THE_UUID;

    private List<BluetoothDevice> deviceList;
    private BluetoothDevice partnerDevice;
    private BluetoothSocket socket;
    private Thread ct;

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
        srcActivity.informUser(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_query));
    }

    /***
     * Method to discover non-paired devices
     */
    public void discoverDevices()
    {
        if(partnerDevice == null)
        {
            bluetoothAdapter.startDiscovery();
            srcActivity.informUser(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_start));
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
            srcActivity.informUser(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_stop));
        }

        if(selectedDevice == null)
        {
            srcActivity.informUser(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_no_device));
            srcActivity.processFailed();
            return;
        }

        partnerDevice = selectedDevice;

        try {
            bluetoothAdapter.cancelDiscovery();
            socket = partnerDevice.createRfcommSocketToServiceRecord(THE_UUID);
            ct = new Thread(new ConnectThread());
            ct.start();
        } catch (IOException e) {
            printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_error_1));
            srcActivity.processFailed();
        }
    }

    /***
     * Method to send Data after a successful connection process
     */
    private void sendData()
    {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            //OutputStream os = socket.getOutputStream();

            //oos.writeObject(AttachmentIO.getAllAttachments());
            AttachmentIO.initStorageDirectory();
            oos.writeObject(AttachmentIO.getStorageDirStr());
            List<byte[]> bytes = AttachmentIO.getAllAttachments();
            for (byte[] byteArr : bytes)
            {
                oos.writeObject(byteArr);
            }
            oos.writeObject(AttachmentIO.getImageFilenameMapping());
            /*for (byte[] byteArr : bytes)
            {
                os.write(byteArr);
            }*/
            //os.write("end of file".getBytes());
            printToUI(srcActivity.getString(R.string.bt_attachments_sent));
            oos.writeObject(Proxy.getClm().getAllCategories());
            printToUI(srcActivity.getString(R.string.bt_categories_sent));
            oos.writeObject(Proxy.getToDoAdapter().getEntries());
            printToUI(srcActivity.getString(R.string.bt_entries_sent));
            oos.writeObject(false);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            oos.close();
            //os.close();
        } catch (IOException e) {
            printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_error_2));
            bm.getSrcActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    srcActivity.processFailed();
                }
            });
        }
    }

    /***
     * Method to close the socket
     */
    private void disconnect() throws IOException {
        socket.close();
        printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_disconnected));
    }

    /***
     * Method to take necessary steps to cancel a connection process
     */
    public void cancelConnection()
    {
        if(ct != null && ct.isAlive())
        {
            ct.interrupt();
            try {
                disconnect();
            } catch (IOException e) {}
        }
    }

    /***
     * Method to print a message to the user
     * @param msg
     */
    private void printToUI(String msg)
    {
        srcActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                srcActivity.informUser(msg);
            }
        });
    }

    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    /***
     * Class that handles a connection to a server
     */
    class ConnectThread implements Runnable {

        @Override
        public void run() {
            try {
                socket.connect();
                printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_connect) + partnerDevice.getName());
                sendData();
                disconnect();
            } catch (IOException e) {
                printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_error_1));
                srcActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srcActivity.processFailed();
                    }
                });
            }
        }
    }
}
