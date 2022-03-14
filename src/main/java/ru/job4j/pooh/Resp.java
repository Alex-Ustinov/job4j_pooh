package ru.job4j.pooh;

public class Resp {
    private final String text;
    private final Number status;

    public Resp(String text, Number status) {
        this.text = text;
        this.status = status;
    }

    public String text() {
        return text;
    }

    public Number status() {
        return status;
    }
}