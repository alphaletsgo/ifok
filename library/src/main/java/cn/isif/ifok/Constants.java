package cn.isif.ifok;

import okhttp3.MediaType;

/**
 * Created by dell on 2016/4/5.
 */
public class Constants {
    public static final long DEFAULT_CONN_TIMEOUT = 1000 * 15l;
    public static final String UTF_8 = "UTF-8";
    public static final String USER_AGENT = "User-Agent";
    public static final String CONTENT_LEN = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
}
