package cn.razesoldier.csgo.query.util;

import cn.razesoldier.csgo.query.ui.item.AlertBox;
import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.builder.HCB;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import javafx.scene.control.Alert;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * 用于访问发出HTTP请求的客户端
 */
public class HttpClient {
    private CloseableHttpClient client;

    public HttpClient() {
        try {
            client = HCB.custom().ssl().retry(10).timeout(5000).build();
        } catch (HttpProcessException e) {
            AlertBox box = new AlertBox(Alert.AlertType.ERROR, "Error", "无法启用SSL");
            box.show();
            System.exit(1);
        }
    }

    public HttpClient(int retry) {
        try {
            client = HCB.custom().ssl().retry(retry).timeout(5000).build();
        } catch (HttpProcessException e) {
            AlertBox box = new AlertBox(Alert.AlertType.ERROR, "Error", "无法启用SSL");
            box.show();
            System.exit(1);
        }
    }

    public HttpClient(int retry, int timeout) {
        try {
            client = HCB.custom().ssl().retry(retry).timeout(timeout).build();
        } catch (HttpProcessException e) {
            AlertBox box = new AlertBox(Alert.AlertType.ERROR, "Error", "无法启用SSL");
            box.show();
            System.exit(1);
        }
    }

    public HttpClient(int retry, int timeout, String proxy) {
        int pos = proxy.indexOf(":");
        String host = proxy.substring(0, pos);
        int port = Integer.valueOf(proxy.substring(pos + 1));
        try {
            client = HCB.custom().ssl().retry(retry).timeout(timeout).proxy(host, port).build();
        } catch (HttpProcessException e) {
            AlertBox box = new AlertBox(Alert.AlertType.ERROR, "Error", "无法启用SSL");
            box.show();
            System.exit(1);
        }
    }

    /**
     * 设置代理
     * @param proxy String 代理IP地址
     */
    public void setProxy(String proxy) {
        int pos = proxy.indexOf(":");
        String host = proxy.substring(0, pos);
        int port = Integer.valueOf(proxy.substring(pos + 1));
        try {
            client = HCB.custom().ssl().retry(5).timeout(1000).proxy(host, port).build();
        } catch (HttpProcessException e) {
            AlertBox box = new AlertBox(Alert.AlertType.ERROR, "Error", "无法启用SSL");
            box.show();
            System.exit(1);
        }
    }

    /**
     * 发起GET请求
     * @return String
     */
    public String GET(String url) throws HttpProcessException {
        HttpConfig config = HttpConfig.custom().url(url).encoding("utf-8").client(client);
        return HttpClientUtil.get(config);
    }
}
