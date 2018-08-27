package cn.isif.ifok.utils;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import cn.isif.ifok.Part;


/**
 * Created by zh on 16/3/7.
 */
public class UrlTools {

    public static String getFullUrl(String url, List<Part> params, boolean urlEncoder) {
        StringBuffer fullUrl = new StringBuffer();
        fullUrl.append(url);
        if (fullUrl.indexOf("?", 0) < 0 && params.size() > 0) {
            fullUrl.append("?");
        }
        int flag = 0;
        for (Part part : params) {
            try {
                fullUrl.append(urlEncoder ? URLEncoder.encode(part._key, "UTF-8") : part._key).append("=").append(urlEncoder ? URLEncoder.encode(part._value, "UTF-8") : part._value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (++flag != params.size()) {
                fullUrl.append("&");
            }
        }

        return fullUrl.toString();
    }


}
