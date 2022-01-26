package com.wzw.ziweishopcity.ziweishopcitythirdparty;

import com.wzw.ziweishopcity.ziweishopcitythirdparty.component.SmsComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ZiweishopcityThirdPartyApplicationTests {
    @Autowired
    SmsComponent smsComponent;
    @Test
    void contextLoads() {
        String jsonReturn = smsComponent.sendCode("18571358936", "5201314");
        System.out.println(jsonReturn);
    }
}
