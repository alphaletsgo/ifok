package cn.isif.ifok.utils;

/**
 * Created by zh on 16/3/21.
 */
public class FileUtils {
    /**
     * 获取文件的名
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }
}
