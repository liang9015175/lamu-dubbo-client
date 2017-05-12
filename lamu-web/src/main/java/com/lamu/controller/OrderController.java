package com.lamu.controller;

import com.lamu.util.ConfigUtil;
import com.lamu.util.MD5;
import com.lamu.util.SignUtils;
import com.lamu.util.XmlUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by Administrator on 2016/1/21.
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    public static Map<String, String> orderResult; //用来存储订单的交易状态(key:订单号，value:状态(0:未支付，1：已支付))  ---- 这里可以根据需要存储在数据库中

    @RequestMapping("/pre")
    public Map<String, String> submitOrder(HttpServletRequest request, HttpServletResponse resp) {
        Map<String, String> toMap = new HashMap<String, String>();
        try {
            request.setCharacterEncoding("utf-8");
            resp.setCharacterEncoding("utf-8");
            SortedMap<String, String> paramMap = XmlUtil.getParamFromReq(request);
            paramMap.put("mch_id", ConfigUtil.getMchId());
            paramMap.put("nonce_str", String.valueOf(new Date().getTime()));
            paramMap.put("notify_url", ConfigUtil.getNoticeUrl());
            Map<String, String> params = SignUtils.paraFilter(paramMap);
            StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
            SignUtils.buildPayParams(buf, params, false);
            String preStr = buf.toString();
            String sign = MD5.sign(preStr, "&key=" + ConfigUtil.getKey(), "utf-8");
            paramMap.put("sign", sign);

            String reqUrl = ConfigUtil.getReqUrl();
            System.out.println("reqParams:" + XmlUtil.parseXML(paramMap));

            CloseableHttpResponse response = null;
            CloseableHttpClient client = null;
            String res = null;
            try {
                HttpPost httpPost = new HttpPost(reqUrl);
                StringEntity entityParams = new StringEntity(XmlUtil.parseXML(paramMap), "utf-8");
                httpPost.setEntity(entityParams);
                httpPost.setHeader("Content-Type", "text/xml;charset=utf-8");
                client = HttpClients.createDefault();
                response = client.execute(httpPost);
                if (response != null && response.getEntity() != null) {
                    res = new String(EntityUtils.toByteArray(response.getEntity()), "utf-8");
                    System.out.println("请求结果：" + res);
                }
            } catch (Exception e) {
                e.printStackTrace();
                res = "系统异常";
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
            toMap = XmlUtil.toMap(res.getBytes(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toMap;
    }

    @RequestMapping("/notifyOrder")
    public String notifyOrder(HttpServletRequest request, HttpServletResponse response) {
        String defaultReply = "fail";
        try {
            request.setCharacterEncoding("utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            String reqStr = XmlUtil.parseRequst(request);
            if (reqStr != null && !reqStr.equals("")) {
                Map<String, String> map = XmlUtil.toMap(reqStr.getBytes(), "utf-8");
                if (map.containsKey("sign")) {
                    if (!SignUtils.checkParam(map, ConfigUtil.getKey())) {
                        defaultReply = "fail";
                    } else {
                        String status = map.get("status");
                        if (status != null && "0".equals(status)) {
                            String result_code = map.get("result_code");
                            if (result_code != null && "0".equals(result_code)) {
                                if (orderResult == null) {
                                    orderResult = new HashMap<String, String>();
                                }
                                String out_trade_no = map.get("out_trade_no");
                                orderResult.put(out_trade_no, "1");
                            }
                        }
                        defaultReply = "success";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultReply;
    }
}
