package cn.isif.ifok;


/**
 * Created by zh on 16/3/7.
 */
public class Part {
    public String _key;
    public String _value;
    public FileWrapper fileWrapper;
    public FileBytes fileBytes;//文件bytes

    public Part(String _key, String _value) {
        this._key = _key;
        this._value = _value;
    }
    public Part(String key, FileWrapper fileWrapper) {
        this._key = key;
        this.fileWrapper = fileWrapper;
    }
    public Part(String key, FileBytes fileBytes) {
        this._key = key;
        this.fileBytes = fileBytes;
    }
}
