package cn.isif.ifok;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by he on 2018/8/27.
 */

public interface StatusListener {
    /**
     * callBack--- onStart
     *
     * @param callBack
     * @param request
     */
    void sendOnStartCallBack(final CallBack callBack, final Request request);

    /**
     * callBack--- onFailed
     *
     * @param callBack
     * @param e
     */
    void sendOnFailedCallBack(final String url, final CallBack callBack, final Exception e, final Response response);

    /**
     * callBack--- onSuccess
     *
     * @param callBack
     * @param object
     */
    void sendOnSuccessCallBack(final String url, final CallBack callBack, final Object object);

    /**
     * progress upgrade
     *
     * @param callBack
     * @param progress
     * @param networkSpeed
     * @param done
     */
    void onProgressUpgrade(final CallBack callBack, final int progress, final long networkSpeed, final boolean done);
}
