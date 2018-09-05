package cn.isif.ifok;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import cn.isif.ifok.utils.FileUtils;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by zh on 16/2/22.
 */
public class IfOk implements StatusListener {
    private OkHttpClient client;
    private volatile static IfOk ifOk;
    private Handler mDelivery;
    private OkConfig configuration = new OkConfig.Builder().build();

    public synchronized void init(OkConfig configuration) {
        this.configuration = configuration;
        long timeout = configuration.getTimeout();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS);
        if (configuration.getHostnameVerifier() != null) {
            builder.hostnameVerifier(configuration.getHostnameVerifier());
        }

        CookieJar cookieJar = configuration.getCookieJar();
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }

        if (configuration.getCache() != null) {
            builder.cache(configuration.getCache());
        }

        if (configuration.getAuthenticator() != null) {
            builder.authenticator(configuration.getAuthenticator());
        }
        if (configuration.getCertificatePinner() != null) {
            builder.certificatePinner(configuration.getCertificatePinner());
        }
        builder.followRedirects(configuration.isFollowRedirects());
        builder.followSslRedirects(configuration.isFollowSslRedirects());
        if (configuration.getSslSocketFactory() != null) {
            builder.sslSocketFactory(configuration.getSslSocketFactory());
        }
        if (configuration.getDispatcher() != null) {
            builder.dispatcher(configuration.getDispatcher());
        }
        builder.retryOnConnectionFailure(configuration.isRetryOnConnectionFailure());
        if (configuration.getNetworkInterceptorList() != null) {
            builder.networkInterceptors().addAll(configuration.getNetworkInterceptorList());
        }

        if (configuration.getInterceptorList() != null) {
            builder.interceptors().addAll(configuration.getInterceptorList());
        }
        //代理
        if (configuration.getProxy() != null) {
            builder.proxy(configuration.getProxy());
        }
        client = builder.build();
    }

    private IfOk() {
        mDelivery = new Handler(Looper.getMainLooper());
        if (client == null) {
            init(new OkConfig.Builder().build());
        }
    }

    public static IfOk getInstance() {
        if (ifOk == null) {
            synchronized (IfOk.class) {
                if (ifOk == null) {
                    ifOk = new IfOk();
                }
            }
        }
        return ifOk;
    }

    //################################## general network method
    public Call head(String url, final CallBack callBack) {
        return methodExecuteTrunk(Method.HEAD, url, null, callBack, true);
    }

    public Call head(String url, final CallBack callBack, boolean urlEncoder) {
        return methodExecuteTrunk(Method.HEAD, url, null, callBack, urlEncoder);
    }

    public Call get(String url, final CallBack callBack) {
        return methodExecuteTrunk(Method.GET, url, null, callBack, true);
    }

    public Call get(String url, final CallBack callBack, boolean urlEncoder) {
        return methodExecuteTrunk(Method.GET, url, null, callBack, urlEncoder);
    }

    public Call post(String url, Params params, final CallBack callBack) {
        return methodExecuteTrunk(Method.POST, url, params, callBack, true);
    }

    public Call post(String url, Params params, final CallBack callBack, boolean urlEncoder) {
        return methodExecuteTrunk(Method.POST, url, params, callBack, urlEncoder);
    }

    public Call put(String url, Params params, final CallBack callBack) {
        return methodExecuteTrunk(Method.PUT, url, params, callBack, true);
    }

    public Call put(String url, Params params, final CallBack callBack, boolean urlEncoder) {
        return methodExecuteTrunk(Method.PUT, url, params, callBack, urlEncoder);
    }

    public Call delete(String url, Params params, final CallBack callBack) {
        return methodExecuteTrunk(Method.DELETE, url, params, callBack, true);
    }

    public Call delete(String url, Params params, final CallBack callBack, boolean urlEncoder) {
        return methodExecuteTrunk(Method.DELETE, url, params, callBack, urlEncoder);
    }

    //############################## file download

    /**
     * 异步下载文件
     *
     * @param url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback
     */
    public Call download(final String url, final String destFileDir, final CallBack callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendOnFailedCallBack(url, callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    ResponseBody responseBody = new ProgressResponseBody(response.body(), IfOk.this, callback);//包装进度
                    is = responseBody.byteStream();
                    File file = new File(destFileDir, FileUtils.getFileName(url));
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    sendOnSuccessCallBack(url, callback, file.getAbsolutePath());
                } catch (IOException e) {
                    sendOnFailedCallBack(url, callback, e);
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
        return call;
    }


    /**
     * network main execute method
     *
     * @param method
     * @param url
     * @param params
     * @param callBack
     * @param urlEncoder
     */
    public Call methodExecuteTrunk(Method method, final String url, Params params, final CallBack callBack, boolean urlEncoder) {
        Request.Builder builder = new Request.Builder();
        Headers headers = null;
        RequestBody body = null;
        if (params != null) {
            headers = params.getHeader();
        }
        switch (method) {
            case HEAD:
                builder.head();
                break;
            case GET:
                builder.get();
                break;
            case POST:
                body = params.getRequestBody();
                if (body != null) {
                    builder.post(params.hasAttach() ? new ProgressRequestBody(body, this, callBack) : body);
                }
                break;
            case PUT:
                body = params.getRequestBody();
                if (body != null) {
                    builder.put(params.hasAttach() ? new ProgressRequestBody(body, this, callBack) : body);
                }
                break;
            case DELETE:
                body = params.getRequestBody();
                if (body != null) {
                    builder.delete(params.hasAttach() ? new ProgressRequestBody(body, this, callBack) : body);
                }
                break;
            default:
                break;
        }
        builder.url(url);
        if (headers != null) {
            builder.headers(headers);
        }
        Request request = builder.build();
        sendOnStartCallBack(callBack, request);
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sendOnFailedCallBack(url, callBack, e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Exception e = new Exception("Unexpected code " + response);
                    sendOnFailedCallBack(url, callBack, e);
                } else {
                    try {
                        sendOnSuccessCallBack(url, callBack, response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return call;
    }

    //#########################callback method

    /**
     * callBack--- onStart
     *
     * @param callBack
     * @param request
     */
    public void sendOnStartCallBack(final CallBack callBack, final Request request) {
        if (callBack == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callBack.onStart(request);
            }
        });
    }

    /**
     * callBack--- onFailed
     *
     * @param callBack
     * @param e
     */
    public void sendOnFailedCallBack(final String url, final CallBack callBack, final Exception e) {
        if (callBack == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callBack.onFail(e);
            }
        });
    }

    /**
     * callBack--- onSuccess
     *
     * @param callBack
     * @param object
     */
    public void sendOnSuccessCallBack(final String url, final CallBack callBack, final Object object) {
        if (callBack == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(object);
            }
        });

    }

    /**
     * progress upgrade
     *
     * @param callBack
     * @param progress
     * @param networkSpeed
     * @param done
     */
    public void onProgressUpgrade(final CallBack callBack, final int progress, final long networkSpeed, final boolean done) {
        if (callBack == null) return;
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callBack.updateProgress(progress, networkSpeed, done);
            }
        });
    }

    /**
     * 修改公共请求参数信息
     *
     * @param key
     * @param value
     */
    public void updateCommonParams(String key, String value) {
        boolean add = false;
        List<Part> commonParams = configuration.getCommonParams();
        if (commonParams != null) {
            for (Part param : commonParams) {
                if (param != null && TextUtils.equals(param._key, key)) {
                    param._value = value;
                    add = true;
                    break;
                }
            }
        }
        if (!add) {
            commonParams.add(new Part(key, value));
        }
    }

    /**
     * 修改公共header信息
     *
     * @param key
     * @param value
     */
    public void updateCommonHeader(String key, String value) {
        Headers headers = configuration.getCommonHeaders();
        if (headers == null) {
            headers = new Headers.Builder().build();
        }
        configuration.commonHeaders = headers.newBuilder().set(key, value).build();
    }

    public OkHttpClient getOkHttpClient() {
        return client;
    }

    public List<Part> getCommonParams() {
        return configuration.getCommonParams();
    }

    public HostnameVerifier getHostnameVerifier() {
        return configuration.getHostnameVerifier();
    }

    public long getTimeout() {
        return configuration.getTimeout();
    }

    public Headers getCommonHeaders() {
        return configuration.getCommonHeaders();
    }

}
