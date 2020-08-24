package cn.isif.ifok;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 下载进度处理
 * <p>
 * Created by he on 2018/8/27.
 */

public class ProgressResponseBody extends ResponseBody {

    //实际的待包装响应体
    private final ResponseBody responseBody;
    //进度回调接口
    private final StatusListener statusListener;
    //包装完成的BufferedSource
    private BufferedSource bufferedSource;
    private long previousTime;
    private CallBack callBack;

    /**
     * 构造函数，赋值
     *
     * @param responseBody   待包装的响应体
     * @param statusListener 回调接口
     * @param callBack       UI回调
     */
    public ProgressResponseBody(ResponseBody responseBody, StatusListener statusListener, CallBack callBack) {
        this.responseBody = responseBody;
        this.statusListener = statusListener;
        this.callBack = callBack;
    }


    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    /**
     * 重写进行包装source
     *
     * @return BufferedSource
     * @throws IOException 异常
     */
    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            //包装
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private Source source(Source source) {
        previousTime = System.currentTimeMillis();
        return new ForwardingSource(source) {
            //当前读取字节数
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //如果contentLength()不知道长度，会返回-1
                long ctl = responseBody.contentLength();
                if (statusListener != null && ctl >= 0) {
                    int progress = (int) (((double)totalBytesRead / (double) ctl) * 100);
                    //计算速度
                    long totalTime = (System.currentTimeMillis() - previousTime) / 1000;
                    if (totalTime == 0) {
                        totalTime += 1;
                    }
                    long networkSpeed = totalBytesRead / totalTime;

                    statusListener.onProgressUpgrade(callBack, progress, networkSpeed, bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}
