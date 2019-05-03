package cn.razesoldier.csgo.query;

import cn.razesoldier.csgo.query.util.HttpClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.exception.HttpProcessException;

/**
 * 查询皮肤磨损值
 */
public class SkinWearQuery {
    private String runGameLink;

    private HttpClient httpClient;

    public SkinWearQuery(String runGameLink) {
        this.runGameLink = runGameLink;
        httpClient = new HttpClient();
    }

    public JSONObject query() throws RuntimeException {
        try {
            return JSON.parseObject(httpClient.GET("https://api.csgofloat.com/?url=" + runGameLink));
        } catch (HttpProcessException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }
}
