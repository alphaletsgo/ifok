package cn.isif.ifok;

import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;

/**
 * Created by zh on 16/5/14.
 */
public class RequestManager {
    private ConcurrentHashMap<Object, Call> callMap;
    private static RequestManager manager;

    private RequestManager() {
        callMap = new ConcurrentHashMap<>();
    }

    public static RequestManager getInstance() {
        if (manager == null) {
            synchronized (RequestManager.class) {
                if (manager == null) {
                    manager = new RequestManager();
                }
            }
        }
        return manager;
    }

    public void addCall(Object tag, Call call) {
        if (!callMap.contains(tag)){
            callMap.put(tag, call);
        }
    }

    public Call getCall(Object tag) {
        return callMap.get(tag);
    }

    public void removeCall(Object tag) {
        callMap.remove(tag);
    }

}
