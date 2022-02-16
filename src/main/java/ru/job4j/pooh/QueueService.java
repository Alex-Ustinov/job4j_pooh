package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    public void putIfAbsent(String sourceName, String request) {
        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
        concurrentLinkedQueue.add(request);
        queue.putIfAbsent(sourceName, concurrentLinkedQueue);
    };

    public ConcurrentLinkedQueue<String> get(String sourceName) {
        ConcurrentLinkedQueue<String> result = null;
        while (queue.getOrDefault(sourceName, null) != null) {
            result = queue.getOrDefault(sourceName, null);
            queue.remove(sourceName);
        }
        return result;
    }

    
    @Override
    public Resp process(Req req) {
        if ("GET".equals(req.getHttpRequestType())) {
            ConcurrentLinkedQueue<String> data = get(req.getSourceName());
            if (data != null) {
                return new Resp(data.poll(), "200");
            } else {
                return new Resp(data.poll(), "204");
            }
        } else {
            putIfAbsent(req.getSourceName(), req.getParam());
            return new Resp(req.getParam(), "200");
        }
    }
}