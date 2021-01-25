package com.u8.server.utils;

import com.alibaba.fastjson.JSON;
import com.u8.server.log.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.struts2.ServletActionContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ant on 2015/2/28.
 */
public class HttpUtils {


    public static void render(String content, String contentType) {
        try {
            HttpServletResponse localHttpServletResponse = ServletActionContext.getResponse();
            if (localHttpServletResponse != null) {
                localHttpServletResponse.setHeader("Pragma", "No-cache");
                localHttpServletResponse.setHeader("Cache-Control", "no-cache");
                localHttpServletResponse.setDateHeader("Expires", 0L);
                localHttpServletResponse.setContentType(contentType);
                localHttpServletResponse.getWriter().write(content);
            }
        } catch (IOException localIOException) {
            Log.e(localIOException.toString());
            localIOException.printStackTrace();
        }
    }

    public static void renderText(String content) {
        render(content, "text/plain; charset=UTF-8");
    }

    public static void renderJson(String content) {
        render(content, "text/json; charset=UTF-8");
    }

    public static void renderHtml(String content) {
        render(content, "text/html; charset=UTF-8");
    }

    public static void renderXml(String content) {
        render(content, "text/xml; charset=UTF-8");
    }

    public static Map<String, Object> parseXMLParams(String xmlStr) {
        return XmlUtils.xml2map(xmlStr);
    }

    public static Map<String, Object> parseJSONParams(String jsonStr) {
        return JSON.parseObject(jsonStr, Map.class);
    }

    public static Map<String, Object> parseWWWParams(Map params) {

        Map<String, Object> p = new HashMap<String, Object>();
        for (Object k : params.keySet()) {
            Object v = params.get(k);
            if (v instanceof String[]) {
                p.put(k.toString(), ((String[]) v)[0]);
            } else {
                p.put(k.toString(), v);
            }

        }
        return p;
    }

    //解析URL格式参数为Map格式
    public static Map<String, String> parseUrlParams(String param) {

        Map<String, String> data = new HashMap<String, String>();
        if (StringUtils.isEmpty(param)) {

            return data;
        }

        if (param.startsWith("?")) {
            param = param.substring(1);
        }

        String[] splits = param.split("&");
        for (String s : splits) {
            String[] p = s.split("=");
            if (p.length == 2) {
                data.put(p[0], p[1]);
            }
        }

        return data;

    }

    public static HttpClient wrapClient(HttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs,
                                               String string) {
                }

                public void checkServerTrusted(X509Certificate[] xcs,
                                               String string) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));

            return new DefaultHttpClient(ccm, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getIpAddr(HttpServletRequest request) {

        try {
            String ip = request.getHeader("X-Real-IP");

            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Forwarded-For");
            }

            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }

            if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个IP值，第一个为真实IP。
                int index = ip.indexOf(',');
                if (index != -1) {
                    ip = ip.substring(0, index);
                }
            }

            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("PROXY_FORWARDED_FOR");
                if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                } else {
                    int index = ip.indexOf(',');
                    if (index != -1) {
                        ip = ip.substring(0, index);
                    }
                }
            }

            return ip;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "unknown";
    }

}
