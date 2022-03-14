package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    public Resp putIfAbsent(Req req) {
        queue.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<String>());
        queue.get(req.getSourceName()).add(req.getParam());
        return new Resp(req.getParam(), 200);
    };

    public String get(Req req) {
        ConcurrentLinkedQueue<String> result = queue.get(req.getSourceName());
        if (result != null) {
            return queue.get(req.getSourceName()).poll();
        }
        return null;
    }

    public Resp prepareGetResponse(Req req) {
        String result = get(req);
        Number code = result != null ? 200 : 204;
        String text = result != null ? result : "";
        return new Resp(text, code);
    }


    @Override
    public Resp process(Req req) {
        String typeHttp = req.getHttpRequestType();
        return switch (typeHttp) {
            case "GET" -> prepareGetResponse(req);
            case "POST" -> putIfAbsent(req);
            default -> new Resp("", 501);
        };
    }
}