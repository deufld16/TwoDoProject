package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import at.htlkaindorf.twodoprojectmaxi.activities.TransferActivity;

public class BluetoothTransfer extends Thread
{
    private BluetoothSocket socket;
    private TransferActivity srcActivity;
    private boolean listen;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public BluetoothTransfer(BluetoothSocket socket, TransferActivity srcActivity, boolean listen)
    {
        this.socket = socket;
        this.srcActivity = srcActivity;
        this.listen = listen;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            srcActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    srcActivity.informUser("Error while transferring data");
                }
            });
        }
    }

    @Override
    public void run() {
        if(listen)
        {
            while(!Thread.interrupted())
            {
                read();
            }
        }
        else
        {
            write();
        }
    }

    /***
     * Method to read the data sent via Bluetooth
     */
    private void read()
    {

    }

    /***
     * Method to send the data via Bluetooth
     */
    private void write()
    {

    }

    /***
     * Method to close the streams
     */
    public void terminate()
    {
        try {
            ois.close();
            oos.close();
        } catch (IOException e) {
            srcActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    srcActivity.informUser("Error while transfer process");
                }
            });
        }
    }
}
