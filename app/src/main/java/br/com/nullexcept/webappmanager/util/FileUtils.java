package br.com.nullexcept.webappmanager.util;

import java.io.File;
import java.util.Objects;

public class FileUtils {

    //@TODO Delete recursive files.
    public static void delete(File file){
        if (file.isDirectory()){
            for (File child: Objects.requireNonNull(file.listFiles())){
                delete(child);
            }
        }
        file.delete();
    }
}
