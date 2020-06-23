package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.io.AttachmentIO;

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
    private BluetoothServerSocket bss;
    private BluetoothSocket socket;
    private Thread at;
    private Thread lt;

    public BluetoothServer(BluetoothManager bm, UUID THE_UUID)
    {
        this.bm = bm;
        this.THE_UUID = THE_UUID;
    }

    /***
     * Method to initialize all relevant steps for the device acting as a Bluetooth Server

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
    }*/

    /***
     * Method to initialize all relevant steps for the device acting as a Bluetooth Server
     */
    public void runServer() {
        try
        {
            bss = bm.getBluetoothAdapter().listenUsingRfcommWithServiceRecord(NAME, THE_UUID);
            printToUI("Device waits for connection attempt");
            at = new Thread(new AcceptThread());
            at.start();
        }
        catch (IOException e)
        {
            printToUI("Error while establishing connection");
            bm.getSrcActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm.getSrcActivity().processFailed();
                }
            });
        }
    }

    /***
     * Method to handle a successful connection and start wait for the data
     */
    private void receiveData()
    {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            lt = new Thread(new ListenerThread());
            lt.start();
        } catch (IOException e) {
            printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_error_5));
            e.printStackTrace();
            bm.getSrcActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bm.getSrcActivity().processFailed();
                }
            });
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
        if(lt != null && lt.isAlive())
        {
            lt.interrupt();
        }

        if(at != null && at.isAlive())
        {
            at.interrupt();
            try {
                if(socket != null){
                    socket.close();
                }
                bss.close();
            } catch (IOException e) {}
        }
    }

    /***
     * Class to wait for an connection attempt
     *
     * @author Maximilian Strohmaier
     */
    class AcceptThread implements Runnable {

        @Override
        public void run() {
            try {
                Log.d("FIXINGBT", "run: " + "waiting for other device");
                socket = bss.accept();
                if(socket != null)
                {
                    printToUI("Connected");
                    receiveData();
                }
            } catch (IOException e) {
                printToUI("No connection was requested");
            }
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
            AttachmentIO.deleteAudiosForTransfer();
            try {
                do {
                    Object o = ois.readObject();
                    if (o instanceof List) {
                        List<Object> uncategorizedItems = new LinkedList<>((List<Object>) o);
                        if(uncategorizedItems.get(0) instanceof Entry) {
                            //Entry sent
                            bm.getSrcActivity().runOnUiThread(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      Proxy.getToDoAdapter().setEntries(new LinkedList(uncategorizedItems));
                                                                  }
                                                              });
                            printToUI(bm.getSrcActivity().getString(R.string.bt_entries_received));
                        }
                        else if(uncategorizedItems.get(0) instanceof Category) {
                            //Category sent
                            bm.getSrcActivity().runOnUiThread(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      Proxy.getClm().setAllCategories(new LinkedList(uncategorizedItems));
                                                                  }
                                                              });
                            printToUI(bm.getSrcActivity().getString(R.string.bt_categories_received));
                        }
                    }
                    else if(o instanceof Map)
                    {
                        //Attachment sent
                        bm.getSrcActivity().runOnUiThread(new Runnable() {
                                                              @Override
                                                              public void run() {
                                                                  AttachmentIO.saveAttachments((Map<String, List<File>>) o);
                                                              }
                                                          });
                        printToUI(bm.getSrcActivity().getString(R.string.bt_attachments_received));
                    }else if(o instanceof Boolean){
                        if(!(boolean)o){
                            break;
                        }
                    }
                }
                while (!Thread.interrupted());
                ois.close();
                socket.close();
                bss.close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
