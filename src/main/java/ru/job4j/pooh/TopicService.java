package ru.job4j.pooh;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {

    ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topic = new ConcurrentHashMap();

    public Resp putIfAbsent(Req req) {
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> existMap = topic.get(req.getSourceName());
        if (existMap != null) {
            for(ConcurrentLinkedQueue queue : existMap.values()) {
                queue.add(req.getParam());
            }
        }
        return new Resp(req.getParam(), 200);
    }

    public String get(Req req) {
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> data = topic.putIfAbsent(req.getSourceName(), new ConcurrentHashMap<>());
        if (data == null) {
            ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
            ConcurrentHashMap queue = topic.get(req.getSourceName());
            queue.put(req.getParam(), concurrentLinkedQueue);
            return null;
        } else {
            ConcurrentLinkedQueue<String> queue = data.get(req.getParam());
            if (queue == null) {
                ConcurrentLinkedQueue<String> paramsQueue = new ConcurrentLinkedQueue<>();
                data.putIfAbsent(req.getParam(), paramsQueue);
                return null;
            } else {
                return queue.poll();
            }
        }
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