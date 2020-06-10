package at.htlkaindorf.twodoprojectmaxi.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import at.htlkaindorf.twodoprojectmaxi.beans.Entry;
import at.htlkaindorf.twodoprojectmaxi.bl.Proxy;

public class IO_Methods {

    public static List<File> convertAudiosToFiles(){
        List<File> allAudioFiles = new LinkedList<>();

        for (Entry entry:Proxy.getToDoAdapter().getEntries()) {
            for (String path:
                 entry.getAllAudioFileLocations()) {
                try {
                    File file = new File(path);
                    allAudioFiles.add(file);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        return allAudioFiles;
    }

    public static void convertFilesToAudios(List<File> allAudioFiles){
        for (File file:
             allAudioFiles) {
            try{
                file.createNewFile();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

}
