package ru.job4j.pooh;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class TopicService implements Service {

    ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topic = new ConcurrentHashMap();
    public boolean putIfAbsent(Req req) {
        AtomicReference<Boolean> result = new AtomicReference<>(false);
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> sourceQueue = topic.computeIfPresent(req.getSourceName(),(key, val) -> {
            ConcurrentLinkedQueue<String> user = val.computeIfPresent(req.getParam(), (keyUser, userQueue) -> {
                userQueue.add(req.getParam());
                return userQueue;
            });
            if (user == null) {
                ConcurrentLinkedQueue<String> paramsQueue = new ConcurrentLinkedQueue<>();
                paramsQueue.add(req.getParam());
                val.put(req.getParam(), paramsQueue);
            }
            result.set(true);
            return val;
        });
        return result.get();
    }

    public ConcurrentLinkedQueue<String> get(Req req) {
        AtomicReference<ConcurrentLinkedQueue<String>> result = new AtomicReference<>(new ConcurrentLinkedQueue<>());
        ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> data = topic.computeIfPresent(req.getSourceName(),(key, val) -> {
            ConcurrentLinkedQueue<String> source = val.getOrDefault(req.getParam(), null);
            if (source != null) {
                for (String sou : source) {
                    result.get().add(sou);
                }
            }
            return val;
        });

        if (data == null) {
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> sources = new ConcurrentHashMap<>();
            ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();
            concurrentLinkedQueue.add(req.getParam());
            sources.put(req.getParam(), concurrentLinkedQueue);
            topic.putIfAbsent(req.getSourceName(), sources);
            return concurrentLinkedQueue;
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
                return new Resp("No topic", "204");
            }
        } else {
            putIfAbsent(req);
            return new Resp(req.getParam(), "200");
        }
    }
}