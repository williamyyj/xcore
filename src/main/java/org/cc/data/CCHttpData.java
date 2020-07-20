package org.cc.data;


import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.cc.CCMap;
import org.cc.ICCMap;

/**
 * @author William 目前支援 GET 直接使用URL POST 使用$form
 */
public class CCHttpData {

    /*
     * 支援跨越網站
     */
    static {
        TrustManager[] trustManager = new TrustManager[]{new TrustEverythingTrustManager()};
        // Let us create the factory where we can set some parameters for the connection
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManager, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyManagementException e) {
        }
    }

    public static byte[] bytes(ICCMap mq, String enc) throws Exception {
        URL url = new URL(mq.asString("$url"));
        ICCMap form = mq.map("$form");
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        uc.setReadTimeout(5000);
        uc.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
        uc.addRequestProperty("User-Agent", "Mozilla");
        //uc.addRequestProperty("Referer", "google.com");
        try {
            // uc.setInstanceFollowRedirects(true);
            // HttpURLConnection.setFollowRedirects(true);
            if (form != null && form.size() > 0) {
                uc.setRequestMethod("POST");
                uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, Object> param : form.entrySet()) {
                    String k = param.getKey();
                    Object v = param.getValue();
                    postData.append(k).append('=').append(URLEncoder.encode(String.valueOf(v), enc)).append('&');
                }
                if (postData.length() > 0) {
                    postData.setLength(postData.length() - 1);
                }
                byte[] postDataBytes = postData.toString().getBytes(enc);
                uc.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                uc.setDoOutput(true);
                uc.getOutputStream().write(postDataBytes);
            }
            int code = uc.getResponseCode();
            boolean redirect = false;
            if (code != HttpURLConnection.HTTP_OK) {
                if (code == HttpURLConnection.HTTP_MOVED_TEMP
                        || code == HttpURLConnection.HTTP_MOVED_PERM
                        || code == HttpURLConnection.HTTP_SEE_OTHER) {
                    redirect = true;
                }
            }
            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = uc.getHeaderField("Location");

                // get the cookie if need, for login
                String cookies = uc.getHeaderField("Set-Cookie");

                // open the new connnection again
                uc = (HttpURLConnection) new URL(newUrl).openConnection();
                uc.setRequestProperty("Cookie", cookies);
                uc.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                uc.addRequestProperty("User-Agent", "Mozilla");
                //uc.addRequestProperty("Referer", "google.com");
                code = uc.getResponseCode();
                System.out.println("Redirect to URL : " + newUrl);
            }

            if (code == 200) {
                return IOUtils.loadData(uc.getInputStream());
            } else {
                return IOUtils.loadData(uc.getErrorStream());
                //throw new Exception("http error : " + code);
            }
        } finally {
            uc.disconnect();
        }

    }

    public static String text(ICCMap mq, String enc) throws Exception {
        return new String(bytes(mq, enc), enc);
    }
    
    public static String text(String url,String enc) throws Exception {
        ICCMap mq = new CCMap();
        mq.put("$url", url);
        return new String(bytes(mq, enc), enc);
    }

    public static void main(String[] args) throws Exception {
        // JSONObject jq = new JSONObject();
        // jq.put("$url", "http://tw.yahoo.com");
        //jq.put("$url", "http://tw.yahoo.com");
        
        
        String url = "http://www.twse.com.tw/exchangeReport/MI_INDEX?response=csv&date=20190304&type=ALL";
       // ICCMap mq = CCJSON.loadString("{ $url: 'http://pest.baphiq.gov.tw/BAPHIQ/wSite/pos/pos.do' }");
        String content = CCHttpData.text(url, "BIG5");
        System.out.println(content);
    }

}
