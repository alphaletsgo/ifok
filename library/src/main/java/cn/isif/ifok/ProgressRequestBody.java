package cn.isif.ifok;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 进度处理
 */
public class ProgressRequestBody extends RequestBody {
    //实际的待包装请求体
    private final RequestBody requestBody;
    //进度回调接口
    private final IfOk ifOk;
    //包装完成的BufferedSink
    private BufferedSink bufferedSink;
    //开始时间，用户计算加载速度
    private long previousTime;
    private final CallBack callBack;

    /**
     * 构造函数，赋值
     *
     * @param requestBody 待包装的请求体
     * @param ifOk
     */
    public ProgressRequestBody(RequestBody requestBody, IfOk ifOk, CallBack callBack) {
        this.requestBody = requestBody;
        this.ifOk = ifOk;
        this.callBack = callBack;
    }

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return MediaType
     */
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return contentLength
     * @throws IOException 异常
     */
    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    /**
     * 重写进行写入
     *
     * @param sink BufferedSink
     * @throws IOException 异常
     */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sink(Sink sink) {
        previousTime = System.currentTimeMillis();
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;

                //回调
                if (ifOk != null) {
                    //计算速度
                    long totalTime = (System.currentTimeMillis() - previousTime) / 1000;
                    if (totalTime == 0) {
                        totalTime += 1;
                    }
                    long networkSpeed = bytesWritten / totalTime;
                    int progress = (int) (bytesWritten * 100 / contentLength);
                    boolean done = bytesWritten == contentLength;
                    ifOk.onProgressUpgrade(callBack, progress, networkSpeed, done ? true : false);
                }
            }
        };
    }
}
