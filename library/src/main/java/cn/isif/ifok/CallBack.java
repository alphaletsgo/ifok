package cn.isif.ifok;

import okhttp3.Request;

/**
 * Created by zh on 16/2/22.
 */
public abstract class CallBack {
    /**
     * when request submit is called
     * @param request
     */
    public abstract void onStart(Request request);

    /**
     * request is failed
     * @param e
     */
    public abstract void onFail(Exception e);

    /**
     * request is success
     * @param object
     */
    public abstract void onSuccess(Object object);

    public abstract void updateProgress(int progress, long networkSpeed,boolean done);

}
