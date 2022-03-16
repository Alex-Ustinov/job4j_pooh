package ru.job4j.pooh;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RespTest {

    @Test
    public void checkRespClass() {
        Resp successResp = new Resp("It works", 200);
        Resp badResp = new Resp("It does not work", 501);
        assertThat(successResp.status(), is(200));
        assertThat(badResp.status(), is(501));
        assertThat(successResp.text(), is("It works"));
        assertThat(badResp.text(), is("It does not work"));
    }

}