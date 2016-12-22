package cn.isif.ifok;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.isif.alibs.utils.StringUtils;
import cn.isif.ifok.utils.GsonUtil;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by zh on 16/3/2.
 */
public class Params {
    private Headers.Builder headers;
    private List<Part> files;
    private List<Part> params;
    private boolean isJson = false;

    public Params(Builder builder) {
        this.headers = builder.headers;
        this.files = builder.files;
        this.params = builder.params;
        this.isJson = builder.isJson;
        init();
    }

    protected void init() {
        headers.add("charset", "UTF-8");
        //添加公共header
        Headers commonHeaders = IfOk.getInstance().getCommonHeaders();
        if (commonHeaders != null && commonHeaders.size() > 0) {
            for (int i = 0; i < commonHeaders.size(); i++) {
                String key = commonHeaders.name(i);
                String value = commonHeaders.value(i);
                headers.add(key, value);
            }
        }
    }

    public RequestBody getRequestBody() {
        RequestBody body = null;
        if (files.size() > 0) {//有附件
            boolean hasData = false;
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            //处理参数
            if (isJson) {
                String jsonBody = GsonUtil.GsonString(params);
                if (!StringUtils.isEmpty(jsonBody)) {
                    builder.addPart(RequestBody.create(Constants.JSON, jsonBody));
                    hasData = true;
                }
            } else {
                for (Part part : params) {
                    builder.addFormDataPart(part._key, part._value);
                    hasData = true;
                }
            }
            //处理附件
            for (Part part : files) {
                String key = part._key;
                FileWrapper file = part.fileWrapper;
                if (file != null) {
                    hasData = true;
                    builder.addFormDataPart(key, file.getFileName(), RequestBody.create(file.getMediaType(), file.getFile()));
                }
                //文件bytes
                FileBytes fileBytes = part.fileBytes;
                if (fileBytes != null) {
                    builder.addFormDataPart(key, fileBytes.getFileName(), RequestBody.create(fileBytes.getMediaType(), fileBytes.getBytes()));
                }
            }
            if (hasData) {
                body = builder.build();
            }

        } else {
            if (isJson) {
                String jsonBody = GsonUtil.GsonString(params);
                if (!StringUtils.isEmpty(jsonBody)) {
                    body = RequestBody.create(Constants.JSON, jsonBody);
                    ;
                }
            } else {
                FormBody.Builder builder = new FormBody.Builder();
                boolean hasData = false;
                for (Part part : params) {
                    String key = part._key;
                    String value = part._value;
                    builder.add(key, value);
                    hasData = true;
                }
                if (hasData) {
                    body = builder.build();
                }
            }

        }

        return body;
    }

    public final static class Builder {
        private Headers.Builder headers = new Headers.Builder();
        private List<Part> files = new ArrayList<Part>();
        private List<Part> params = new ArrayList<Part>();
        private boolean isJson = false;

        public Builder json() {
            this.isJson = true;
            return this;
        }

        public Params build() {
            return new Params(this);
        }
    }

    /**
     * @param key
     * @param file
     */
    public void attach(String key, File file) {
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        boolean isPng = file.getName().lastIndexOf("png") > 0 || file.getName().lastIndexOf("PNG") > 0;
        if (isPng) {
            attach(key, file, "image/png; charset=UTF-8");
            return;
        }

        boolean isJpg = file.getName().lastIndexOf("jpg") > 0 || file.getName().lastIndexOf("JPG") > 0
                || file.getName().lastIndexOf("jpeg") > 0 || file.getName().lastIndexOf("JPEG") > 0;
        if (isJpg) {
            attach(key, file, "image/jpeg; charset=UTF-8");
            return;
        }

        if (!isPng && !isJpg) {
            attach(key, new FileWrapper(file, null));
        }
    }

    public void attach(String key, File file, String contentType) {
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        MediaType mediaType = null;
        try {
            mediaType = MediaType.parse(contentType);
        } catch (Exception e) {

        }

        attach(key, new FileWrapper(file, mediaType));
    }

    public void attach(String key, File file, MediaType mediaType) {
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        attach(key, new FileWrapper(file, mediaType));
    }

    public void attach(String key, List<File> files, MediaType mediaType) {
        for (File file : files) {
            if (file == null || !file.exists() || file.length() == 0) {
                continue;
            }
            attach(key, new FileWrapper(file, mediaType));
        }
    }

    public void attach(String key, FileWrapper fileWrapper) {
        if (!StringUtils.isEmpty(key) && fileWrapper != null) {
            File file = fileWrapper.getFile();
            if (file == null || !file.exists() || file.length() == 0) {
                return;
            }
            files.add(new Part(key, fileWrapper));
        }
    }

    public void attach(String key, List<FileWrapper> fileWrappers) {
        for (FileWrapper fileWrapper : fileWrappers) {
            attach(key, fileWrapper);
        }
    }

    public void attach(String key, String fileName, byte bytes[]) {
        if (bytes == null || bytes.length == 0) {
            return;
        }

        boolean isPng = fileName.lastIndexOf("png") > 0 || fileName.lastIndexOf("PNG") > 0;
        if (isPng) {
            attach(key, fileName, bytes, "image/png; charset=UTF-8");
            return;
        }

        boolean isJpg = fileName.lastIndexOf("jpg") > 0 || fileName.lastIndexOf("JPG") > 0
                || fileName.lastIndexOf("jpeg") > 0 || fileName.lastIndexOf("JPEG") > 0;
        if (isJpg) {
            attach(key, fileName, bytes, "image/jpeg; charset=UTF-8");
            return;
        }

        if (!isPng && !isJpg) {
            attach(key, new FileBytes(fileName, bytes, null));
        }
    }

    public void attach(String key, String fileName, byte bytes[], String contentType) {
        if (bytes == null || bytes.length == 0) {
            return;
        }

        MediaType mediaType = null;
        try {
            mediaType = MediaType.parse(contentType);
        } catch (Exception e) {

        }

        attach(key, new FileBytes(fileName, bytes, mediaType));
    }

    public void attach(String key, FileBytes fileBytes) {
        if (!StringUtils.isEmpty(key) && fileBytes != null) {
            byte[] bytes = fileBytes.getBytes();
            if (bytes == null || bytes.length == 0) {
                return;
            }
            files.add(new Part(key, fileBytes));
        }
    }

    //==================================header====================================
    public void addHeader(String line) {
        headers.add(line);
    }

    public void addHeader(String key, String value) {
        if (value == null) {
            value = "";
        }

        if (!TextUtils.isEmpty(key)) {
            headers.add(key, value);
        }
    }

    public void addHeader(String key, int value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, long value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, float value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, double value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, boolean value) {
        addHeader(key, String.valueOf(value));
    }

    private void putParamsTrunk(String key, String value) {
        if (value == null) {
            value = "";
        }

        Part part = new Part(key, value);
        if (!StringUtils.isEmpty(key) && !this.params.contains(part)) {
            this.params.add(part);
        }
    }

    public void put(String key, int value) {
        this.putParamsTrunk(key, String.valueOf(value));
    }

    public void put(String key, float value) {
        this.putParamsTrunk(key, String.valueOf(value));
    }

    public void put(String key, double value) {
        this.putParamsTrunk(key, String.valueOf(value));
    }

    public void put(String key, String value) {
        this.putParamsTrunk(key, value);
    }

    public void put(String key, char value) {
        this.putParamsTrunk(key, String.valueOf(value));
    }

    public void put(List<Part> params) {
        this.params.addAll(params);
    }


    public Headers getHeader() {
        return headers.build();
    }

    public List<Part> getParams() {
        return params;
    }

    public int size() {
        return this.params.size();
    }
}
