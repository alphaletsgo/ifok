package cn.isif.ifok;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zh on 16/2/22.
 */
public abstract class CallBack {
    /**
     * when request submit is called
     *
     * @param request
     */
    public abstract void onStart(Request request);

    /**
     * request is failed
     *
     * @param e
     * @param response if response is null mean request is failed,the reason have user client error maybe
     */
    public abstract void onFail(Exception e, Response response);

    /**
     * request is success
     *
     * @param object
     */
    public abstract void onSuccess(Object object);

    public abstract void updateProgress(int progress, long networkSpeed, boolean done);

}
