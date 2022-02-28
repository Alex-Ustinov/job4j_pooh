package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    public void putIfAbsent(Req req) {
        ConcurrentLinkedQueue<String> data = queue.get(req.getSourceName());
        if (data == null) {
            ConcurrentLinkedQueue<String> result = new ConcurrentLinkedQueue<>();
            result.add(req.getParam());
            queue.put(req.getSourceName(), result);
        } else {
            data.add(req.getParam());
        }
    };

    public ConcurrentLinkedQueue<String> get(Req req) {
        ConcurrentLinkedQueue<String> result = queue.get(req.getSourceName());
        if (result == null) {
            ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
            concurrentLinkedQueue.add(req.getParam());
            queue.put(req.getSourceName(), concurrentLinkedQueue);
            return concurrentLinkedQueue;
        } else {
            return result;
        }
    }


    @Override
    public Resp process(Req req) {
        if ("GET".equals(req.getHttpRequestType())) {
            ConcurrentLinkedQueue<String> data = get(req);
            if (data != null) {
                return new Resp(data.poll(), "200");
            } else {
                return new Resp(data.poll(), "204");
            }
        } else {
            putIfAbsent(req);
            return new Resp(req.getParam(), "200");
        }
    }
}