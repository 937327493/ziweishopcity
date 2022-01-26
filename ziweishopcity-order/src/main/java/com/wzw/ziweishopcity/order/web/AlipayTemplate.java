package com.wzw.ziweishopcity.order.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.wzw.ziweishopcity.order.vo.PayVo;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000118696983";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCftPINbro40xApwwpa6qY1TnCxnTSmKs+gTlCXPifkn7XPFWDqYp5AEO71eJU3ypg1SaBqqorfDYm0IVCV9bvKXEC+7pSYt5VlCngdLlm/4HJeuShTNDtGTnWRwMUVQmGVmTn4etie5QTN0TwpwC8M0CoRhopygVTpu9Z2YbxLoGx8gf0M4B9hOgJHGSlaGeAE1+rWJKxY5A2C70uwNPH5td4JTRp911wV7jorjB80CJo1gj7KMSBbxuLvyc78dd7sVaXWCTU6CbsFTMm77weIS5L7uh/7CjsooUnNPoKFgI3+UXQlrTb/0Bupb/0basHXfjZeRlaC1/dNQDLnLttJAgMBAAECggEAQ19zCUWf2572X5e/A5GfojdABT2bW0oOIsNG7jazX5gce4q6QsZtmftKw1UmJc4ancg8myLsyKIOaGXxeAGdQgXe7mz5VN+Fb4WWHEFUbUSDGwCjSU5sfwFUSbN3sLs7LX/hW7h/L7DPcQAGkFpZvt8GEDSgrSNwLdYm5/PdGk2dEhogq0IUQVdDKVSgS0kgNwF/8t5QqMmE15kXTSaAquECU7Z4CILccX2oTLC96hCGG4vvBH84Ax5Wt8L4JG2WMh/YcYtUAF8bAjX5BORzOtuic0O4DwIpm0l23ZNeCuDWss550BAf6of60SHGtC4eFH1bgL4HF4ih+wM9iU4UMQKBgQDTV7UEJ8H4FqN0gizNp4ZVxL+aOvAbsrteY2joJgqYBrTzJBMQ6PdqRSFo37nORtkAj5WEYHjtJxd75YkPsldzdPZ7SwM6J+YhPt8Mu35OTpYEQ5aC7TZK1CpHmFZ4G9oXoAoK/2Sn06adRd1JXJn+GpElCLOVpq+MCq0mX7r1lQKBgQDBdBH6E58uYVymHdE9hXgNjNmzsJVSqhVVv5r+CwYVTehzqYXR/xL0xCQqTjEN//z1NzK3WnLB8tZB3KJAa47HeOiQZR3f7Kx7U55/R0y2O1JWjTEJcwh/84tGxgoEhXU5STiUlNDXmFCq9q8aUtTt05SJa8g4RDqgum21jHc55QKBgQCxM6r9LvC+OQ0YVUWHsd068NrOUHieYMAlpiiB7dOYIJd7/lVWKl+45PncWyoJwPGWPM3azqDWB57zLUQ8uwYEMp+wRPcRvf6BBZwzqsl+kvnJ3XnHkWmiWD6TBTILRx8YDAIfsoAND7N1zux55IDhxBjK+n7JDZfHTfRcZov1+QKBgBCnd2y6B7gsdZdvdbYAKYexGoTXrONC9Tc+mY9JT6rB3EMo0vZzKSSdRifGN37nDNXnfJxAUyTTTRK6ddVp7Q7LH+peaiX+8BJl7n0ynC4MCVieKxyPJKovQ0xCfFZLTv4xiYdxEFGfMUKmngh8HQykbGMwPjpp1UpFAnDguNrpAoGBAJ5cs+c+gY2kdUCX+1L0+2qQt48e8fNf+f3gnztGqom5La4IGy5jFiME6FjluyWNtWuSX28tTwZd4BT6ue+BP6bRShqtGB/eDWCCTTdCPxbi7Wux/mASrAytyBwEXxnQ7RsMDojg/Az50bgWptYjuZTQvZ5euqYXT7b3vNXBjpHy";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArFpyksSVOfRaCIqaAbKGD9v/7W97ijjd/FlMyehAYxU5Nv0KDrYtIGPgGjsAbCU0hWG0Yt9oynGJ4DpIhuOACv/HzxqAOKpRPpJdr3/CTxykRkeomDMaUl6iVa/XlJ3a9W5j0KI5foV6V/oMHe7cERt/vK566YEO6u+7TvaVkJkPc7Nqaf9QO7bmHSd9cMWcthE8xoH1FfLCZc5Alhx/oQdM71asfXDnwByOZnlSvkHBvFgQfYvaeRQML0tloNh7dwbfpB+VUqr93K8LILh0iB68P/8Wu3lzsRdG76r2gJ1gUcQzFSso2UEPGG/6cY93/Oe6C3jsC9XklKnNS3C5DQIDAQAB";
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://dt5lc93t1f.51xd.pub/alipayNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.ziweishopcity.com/orderMember";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"1m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
