package cn.isif.ifok;

import okhttp3.MediaType;

/**
 * Created by dell on 2016/8/18-11:22.
 */
public class FileBytes {
    public byte fileBytes[];
    public String fileName;
    public MediaType mediaType;
    private long fileSize;

    public FileBytes(String name, byte fileBytes[], MediaType mediaType) {
        this.fileBytes = fileBytes;
        this.fileName = name;
        this.mediaType = mediaType;
        this.fileSize = fileBytes.length;
    }

    public String getFileName() {
        if (fileName != null) {
            return fileName;
        } else {
            return "nofilename";
        }
    }

    public byte[] getBytes() {
        return fileBytes;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public long getFileSize() {
        return fileSize;
    }
}
