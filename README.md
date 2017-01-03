# ifok

> 基于okhttp库封装，RESTful设计，使用灵活，可快速用于各种android项目！

##使用

**导入项目**
```
compile 'cn.isif.plug.ifok:library:1.0.2'
```

**post请求**
```java
  public static final String tag = "test";//定义一个标识，用于取消任务
  String url = "https://mostring.com/";
  Params builder = new Params.Builder().json().build();
  params.put("protocolVersion", "2.0.0");
  IfOk.getInstance().post(url, params, new CallBack() {
            @Override
            public void onStart(Request request) {
                
            }

            @Override
            public void onFail(Exception e) {

            }

            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void updateProgress(int progress, long networkSpeed, boolean done) {

            }
        }, tag);
```
**取消请求**
```java
IfOk.getInstance().cancel(tag);
```
