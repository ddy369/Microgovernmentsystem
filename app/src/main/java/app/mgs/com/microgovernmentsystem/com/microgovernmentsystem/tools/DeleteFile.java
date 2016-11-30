package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import java.io.File;

/**
 * Created by Administrator on 2016-09-24.
 */
public class DeleteFile {

    private String filePath;

    public DeleteFile(String filePaths){
        filePath = filePaths;
    }
    public boolean deleteFile() {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
}
