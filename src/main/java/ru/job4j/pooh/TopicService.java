package ru.job4j.pooh;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {

    ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topic = new ConcurrentHashMap();
    public boolean putIfAbsent(Req req) {
        Boolean result = false;
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> existMap = topic.get(req.getSourceName());
        if (existMap != null) {
            ConcurrentLinkedQueue existQueue = existMap.get(req.getParam());
            if (existQueue != null) {
                existQueue.add(req.getParam());
                result = true;
            }
        }
        return result;
    }

    public ConcurrentLinkedQueue<String> get(Req req) {
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> data = topic.get(req.getSourceName());
        if (data == null) {
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> sources = new ConcurrentHashMap<>();
            ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
            concurrentLinkedQueue.add(req.getParam());
            sources.put(req.getParam(), concurrentLinkedQueue);
            topic.putIfAbsent(req.getSourceName(), sources);
            return concurrentLinkedQueue;
        } else {
            ConcurrentLinkedQueue<String> queue = data.get(req.getParam());
            if (queue == null) {
                ConcurrentLinkedQueue<String> paramsQueue = new ConcurrentLinkedQueue<>();
                data.putIfAbsent(req.getParam(), paramsQueue);
                return paramsQueue;
            } else {
                data.remove(queue);
                return queue;
            }
        }
    }

    @Override
    public Resp process(Req req) {
        if ("GET".equals(req.getHttpRequestType())) {
            ConcurrentLinkedQueue<String> data = get(req);
            if (data != null) {
                return new Resp(data.poll(), "200");
            } else {
                return new Resp("No topic", "204");
            }
        } else {
            putIfAbsent(req);
            return new Resp(req.getParam(), "200");
        }
    }
}