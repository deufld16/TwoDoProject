package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.activities.TransferActivity;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.io.IO_Methods;

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
                try {
                    read();
                }
                catch (SocketTimeoutException ex)
                {

                } catch (IOException e) {
                    srcActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            srcActivity.informUser("Error while transferring data");
                        }
                    });
                } catch (ClassNotFoundException e) {
                    srcActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            srcActivity.informUser("Error while transferring data");
                        }
                    });
                }
            }
        }
        else
        {
            try {
                write();
            } catch (IOException e) {
                srcActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srcActivity.informUser("Error while transferring data");
                    }
                });
            }
        }
    }

    /***
     * Method to read the data sent via Bluetooth
     */
    private void read() throws IOException, ClassNotFoundException {
        Object o = ois.readObject();
    }

    /***
     * Method to send the data via Bluetooth
     */
    private void write() throws IOException {
        List<File> files = IO_Methods.convertAudiosToFiles();
        oos.writeObject(files);
        oos.writeObject(Proxy.getToDoAdapter().getEntries());
        oos.writeObject(Proxy.getClm().getAllCategories());
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
