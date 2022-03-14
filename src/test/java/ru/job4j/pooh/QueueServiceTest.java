package ru.job4j.pooh;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class QueueServiceTest {

    @Test
    public void whenQueueServiceTesting() {
        QueueService queueService = new QueueService();
        String weatherSourceName = "weather";
        String trafficSourceName = "traffic";
        String paramForWeatherSourceName = "temperature=18";
        String paramForTrafficSourceName = "cars=20188";

        queueService.process(
                new Req("POST", "queue", weatherSourceName, paramForWeatherSourceName)
        );
        queueService.process(
                new Req("POST", "queue", trafficSourceName, paramForTrafficSourceName)
        );
        Resp resultFromWeatherSource1 = queueService.process(
                new Req("GET", "queue", weatherSourceName, null)
        );
        Resp resultFromWeatherSource2 = queueService.process(
                new Req("GET", "queue", weatherSourceName, null)
        );
        Resp resultFromTrafficSource1 = queueService.process(
                new Req("GET", "queue", trafficSourceName, null)
        );
        Resp resultFromTrafficSource2 = queueService.process(
                new Req("GET", "queue", trafficSourceName, null)
        );
        assertThat(resultFromWeatherSource1.text(), is("temperature=18"));
        assertThat(resultFromWeatherSource2.text(), is(""));
        assertThat(resultFromTrafficSource1.text(), is("cars=20188"));
        assertThat(resultFromTrafficSource2.text(), is(""));
    }

}