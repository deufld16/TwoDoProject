package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.activities.TransferActivity;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.io.AttachmentIO;

public class BluetoothTransferOld extends Thread
{
    private BluetoothSocket socket;
    private TransferActivity srcActivity;
    private boolean listen;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public BluetoothTransferOld(BluetoothSocket socket, TransferActivity srcActivity, boolean listen)
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
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if(listen)
        {
            while(!Thread.interrupted())
            {
                /*try {
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
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    srcActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            srcActivity.informUser("Error while transferring data");
                        }
                    });
                    e.printStackTrace();
                }*/
            }
            //terminate();
        }
        else
        {
            /*try {
                write();
            } catch (IOException e) {
                srcActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        srcActivity.informUser("Error while transferring data");
                    }
                });
                e.printStackTrace();
            }*/
            //terminate();
        }
    }

    /***
     * Method to read the data sent via Bluetooth

    private void read() throws IOException, ClassNotFoundException {
        Object o = ois.readObject();
        List<Object> uncategorizedItems = new LinkedList<>();
        if (o instanceof List)
        {
            uncategorizedItems = (List<Object>) o;
            if(uncategorizedItems.get(0) instanceof File)
            {
                AttachmentIO.convertFilesToAudios(new LinkedList(uncategorizedItems));
            }
        }
    }

    **
     * Method to send the data via Bluetooth

    private void write() throws IOException {
        List<File> files = AttachmentIO.convertAudiosToFiles();
        oos.writeObject(files);
        oos.writeObject(Proxy.getToDoAdapter().getEntries());
        oos.writeObject(Proxy.getClm().getAllCategories());
    }*/

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
            e.printStackTrace();
        }
    }
}
