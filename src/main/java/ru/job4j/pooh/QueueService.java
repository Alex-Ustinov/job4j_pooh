package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class QueueService implements Service {
    ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();

    public void putIfAbsent(Req req) {
        ConcurrentLinkedQueue<String> data = queue.computeIfPresent(req.getSourceName(), (key, val) -> {
            val.add(req.getParam());
                    return val;
        });
        if (data == null) {
            ConcurrentLinkedQueue<String> result = new ConcurrentLinkedQueue<>();
            result.add(req.getParam());
            queue.putIfAbsent(req.getSourceName(), result);
        }
    };

    public ConcurrentLinkedQueue<String> get(Req req) {
        AtomicReference<ConcurrentLinkedQueue<String>> result = null;
        result.set(queue.computeIfPresent(req.getSourceName(), (key, val) -> {
            result.set(val);
            return val;
        }));
        if (result == null) {
            ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
            concurrentLinkedQueue.add(req.getParam());
            result.set(concurrentLinkedQueue);
            queue.putIfAbsent(req.getSourceName(), concurrentLinkedQueue);
        }
        return result.get();
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