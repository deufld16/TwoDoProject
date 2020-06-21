package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.io.IO_Methods;

/***
 * Class that handles all relevant steps for the Bluetooth connection as a server
 *
 * @author Maximilian Strohmaier
 */

public class BluetoothServer
{
    private final UUID THE_UUID;
    private BluetoothManager bm;
    private final String NAME = "TWO_DO";

    private ObjectInputStream ois;
    private Thread lt;

    public BluetoothServer(BluetoothManager bm, UUID THE_UUID)
    {
        this.bm = bm;
        this.THE_UUID = THE_UUID;
    }

    /***
     * Method to initialize all relevant steps for the device acting as a Bluetooth Server
     */
    public void runServer() {
        try
        {
            BluetoothServerSocket bss = bm.getBluetoothAdapter().listenUsingRfcommWithServiceRecord(NAME, THE_UUID);
            printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_waiting));
            BluetoothSocket socket = bss.accept();
            if(socket != null)
            {
                printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_connected));
                receiveData(socket);
                socket.close();
            }
            bss.close();
        }
        catch (IOException e)
        {
            printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_error_4));
            bm.getSrcActivity().processFailed();
        }
    }

    /***
     * Method to handle a successful connection and start wait for the data
     */
    private void receiveData(BluetoothSocket socket)
    {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            lt = new Thread(new ListenerThread());
            lt.start();
            ois.close();
        } catch (IOException e) {
            printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_error_5));
            bm.getSrcActivity().processFailed();
        }
    }

    /***
     * Method to print a message to the user
     * @param msg
     */
    public void printToUI(String msg)
    {
        bm.getSrcActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bm.getSrcActivity().informUser(msg);
            }
        });
    }

    /***
     * Method to cancel the waiting process for data
     */
    public void cancelListening()
    {
        if(lt.isAlive())
        {
            lt.interrupt();
        }
    }

    /***
     * Class to take necessary actions with received data
     *
     * @author Maximilian Strohmaier
     */
    class ListenerThread implements Runnable {

        @Override
        public void run() {
            printToUI("Waiting for transmitted data");
            try {
                do {
                    Object o = ois.readObject();
                    List<Object> uncategorizedItems = new LinkedList<>();
                    if (o instanceof List) {
                        uncategorizedItems = (List<Object>) o;
                        if (uncategorizedItems.get(0) instanceof File) {
                            //File sent
                            IO_Methods.convertFilesToAudios(new LinkedList(uncategorizedItems));
                        }
                        else if(uncategorizedItems.get(0) instanceof Entry) {
                            //Entry sent
                            Proxy.getToDoAdapter().setEntries(new LinkedList(uncategorizedItems));
                        }
                        else if(uncategorizedItems.get(0) instanceof Category) {
                            //Category sent

                        }
                    }
                    //ToDo: handle further received data
                }
                while (!Thread.interrupted());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
