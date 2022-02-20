package ru.job4j.pooh;

public class Req {

    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        String [] arrReqParam = content.split(" ");
        String method = arrReqParam[0];
        String [] poohParams = arrReqParam[1].split("/");
        String  poohMode = poohParams[0];
        String  sourceName = poohParams[1];
        String param = arrReqParam[arrReqParam.length - 1];

        return new Req(method, poohMode, sourceName, param);
    }

    public String getHttpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }

}