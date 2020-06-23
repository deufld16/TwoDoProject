package at.htlkaindorf.twodoprojectmaxi.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import at.htlkaindorf.twodoprojectmaxi.R;
import at.htlkaindorf.twodoprojectmaxi.beans.Category;
import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;
import at.htlkaindorf.twodoprojectmaxi.io.AttachmentIO;
import at.htlkaindorf.twodoprojectmaxi.mediaRecorders.ImageRecorder;
import at.htlkaindorf.twodoprojectmaxi.mediaRecorders.SoundRecorder;

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
    private InputStream is;
    private BluetoothServerSocket bss;
    private BluetoothSocket socket;
    private Thread at;
    private Thread lt;
    private Map<byte[], String> tempImgPaths;
    private Map<byte[], String> tempAudioPaths;
    private String path = null;
    private byte[] sentArray = null;

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
            bss = bm.getBluetoothAdapter().listenUsingRfcommWithServiceRecord(NAME, THE_UUID);
            printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_waiting));
            at = new Thread(new AcceptThread());
            at.start();
        }
        catch (IOException e)
        {
            printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_error_4));
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
            is = socket.getInputStream();
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
                    printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_connected));
                    receiveData();
                }
            } catch (IOException e) {
                printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_inform_user_error_6));
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
            printToUI(Proxy.getLanguageContext().getString(R.string.bluetooth_waiting_for_transfer));
            AttachmentIO.deleteAudiosForTransfer();
            String directory = "";
            tempImgPaths = new HashMap<>();
            tempAudioPaths = new HashMap<>();
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
                        String type = ois.readObject()+"";
                        Map<byte[], String> mapping = (Map<byte[], String>)o;
                        for (byte[] byteArray : mapping.keySet())
                        {
                            File oldFile = new File(mapping.get(byteArray));
                            File tempFile = null;
                            //Log.d("BUGFIXING", "run: " + byteArray.toString() + " - " + tempImgPaths);
                            //Log.d("BUGFIXING", "run: " + mapping.keySet() + " - " + mapping.values());
                            switch (type)
                            {
                                case "photoMapping":
                                    tempFile = new File(tempImgPaths.get(byteArray));
                                    break;
                                case "audioMapping":
                                    tempFile = new File(tempAudioPaths.get(byteArray));
                                    break;
                            }
                            if(tempFile != null) {
                                tempFile.renameTo(oldFile);
                            }
                        }

                    } else if(o instanceof String) {
                        Log.d("BUGFIXING", "run: ich bin hier");
                        if(o.equals("photo") || o.equals("audio")) {
                            byte[] help = (byte[])ois.readObject();
                            if(o.equals("photo")){
                                path = directory + ImageRecorder.assemblePhotoPath();
                            }else{
                                path = directory + SoundRecorder.createFileNameNow();
                            }
                            sentArray = help;
                            bm.getSrcActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AttachmentIO.saveAttachments(path, sentArray);
                                }
                            });
                        }

                        switch(o+"")
                        {
                            case "photo":
                                //Log.d("BUGFIXING", "run: ich bin hier aber habe heute kein Foto für dich");
                                tempImgPaths.put(sentArray, path);
                                break;
                            case "audio":
                                tempAudioPaths.put(sentArray, path);
                                break;
                            default:
                                directory = o+"";
                                break;
                        }
                    } else if(o instanceof Boolean){
                        if(!(boolean)o){
                            printToUI(bm.getSrcActivity().getString(R.string.bt_attachments_received));
                            break;
                        }
                    }
                }
                while (!Thread.interrupted());
                ois.close();
                is.close();
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
