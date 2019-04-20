package cn.razesoldier.csgo.query;

import cn.razesoldier.csgo.query.util.HttpClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.exception.HttpProcessException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class GunSkinListQuery {
    final static public String[] wearLevelList = {"崭新出厂", "略有磨损", "久经沙场", "战痕累累", "破损不堪"};
    final static public String[] wearLevelMap = {"Factory New", "Minimal Wear", "Field-Tested", "Battle-Scarred", "Well-Worn"};

    private String rawSkinName;

    private HttpClient httpClient;

    public GunSkinListQuery(String enSkinName, String wearLevelText) {
        for (int i = 0; i < wearLevelList.length; i++) {
            if (wearLevelList[i].equals(wearLevelText)) {
                rawSkinName = enSkinName + " (" + wearLevelMap[i] + ")";
            }
        }
        httpClient = new HttpClient();
    }

    public GunSkinListQuery(String enSkinName, String wearLevelText, String proxy) {
        for (int i = 0; i < wearLevelList.length; i++) {
            if (wearLevelList[i].equals(wearLevelText)) {
                rawSkinName = enSkinName + " (" + wearLevelMap[i] + ")";
            }
        }
        httpClient = new HttpClient(10, 1000, proxy);
    }

    public Map<String, String> query() {
        try {
            String url;
            int startPos = 0;
            boolean end = false;
            HashMap<String, String> result = new HashMap<>();
            JSONObject assetsResult = new JSONObject();
            StringBuilder htmlResult = new StringBuilder();
            do {
                url = new URI("https", "//steamcommunity.com/market/listings/730/" + rawSkinName +
                        "/render/?count=100&language=schinese&currency=1&start=" + startPos, null).toASCIIString();
                JSONObject jsonObj = doQuery(url);
                if (jsonObj.get("assets").equals(new JSONArray())) {
                    end = true;
                } else {
                    assetsResult.putAll(jsonObj.getJSONObject("assets").getJSONObject("730").getJSONObject("2"));
                    htmlResult.append(jsonObj.getString("results_html")).append("\n");
                    startPos += 100;
                }
            } while (!end);
            result.put("assets", assetsResult.toString());
            result.put("html", "<html><body>" + htmlResult.toString() + "</body></html>");
            return result;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    /**
     * 执行查询操作
     * @param url String
     */
    private JSONObject doQuery(String url) throws RuntimeException {
        String res;
        try {
            res = httpClient.GET(url);
        } catch (HttpProcessException e) {
            throw new RuntimeException("拉取GunSkinList失败，堆栈跟踪：\n" + e.getLocalizedMessage());
        }
        return JSON.parseObject(res);
    }
}
