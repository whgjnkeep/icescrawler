package ices.crawler.utils;

import java.io.File;

/**
 * Created by Neuclil on 17-6-18.
 */
public class FilePersistentBase {

    protected  String path;

    public static String PATH_SEPERATOR = "/";

    static{
        String property = System.getProperties().getProperty("file.separator");
        if(property != null){
            PATH_SEPERATOR = property;
        }
    }

    public void setPath(String path){
        if(!path.endsWith(PATH_SEPERATOR)){
            path += PATH_SEPERATOR;
        }
        this.path = path;
    }

    public File getFile(String fullName){
        checkAndMakeParentDirectory(fullName);
        return new File(fullName);
    }

    private void checkAndMakeParentDirectory(String fullName) {
        int index = fullName.lastIndexOf(PATH_SEPERATOR);
        if(index > 0){
            String path = fullName.substring(0, index);
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
        }
    }

    public String getPath(){
        return path;
    }
}
