package cn.razesoldier.csgo.query.ui.action;

import cn.razesoldier.csgo.query.GunSkinListQuery;
import cn.razesoldier.csgo.query.Main;
import cn.razesoldier.csgo.query.SkinNameMap;
import cn.razesoldier.csgo.query.SkinWearQuery;
import cn.razesoldier.csgo.query.model.SkinModel;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.razesoldier.csgo.query.GunSkinListQuery.wearLevelList;
import static cn.razesoldier.csgo.query.GunSkinListQuery.wearLevelMap;

public class QueryAction implements IAction {
    private Main app;

    QueryAction(Main app) {
        this.app = app;
    }

    public void exec() throws RuntimeException {
        if (this.app.skinName.getText().equals("")) {
            throw new RuntimeException("皮肤名称未指定");
        }
        if (this.app.wearLevel.getSelectionModel().isEmpty()) {
            throw new RuntimeException("磨损等级未指定");
        }
        String realName = SkinNameMap.getInstance().getEnName(this.app.skinName.getText());
        if (realName == null) {
            throw new RuntimeException("未在skinnamemap.txt里定义的名称");
        }
        String proxy = this.app.proxy.getText();
        if (!proxy.equals("")) {
            if (!proxy.matches("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*:[0-9]*")) {
                throw new RuntimeException("代理地址是非法的");
            }
        } else {
            proxy = null;
        }

        // 开始查询
        HashMap<String, SkinModel> map = new HashMap<>();
        Map queryResult;
        if (proxy != null) {
            queryResult = new GunSkinListQuery(realName, this.app.wearLevel.getSelectionModel().getSelectedItem().toString(),
                    proxy).query();
        } else  {
            queryResult = new GunSkinListQuery(realName, this.app.wearLevel.getSelectionModel().getSelectedItem().toString()).query();
        }
        @SuppressWarnings("unchecked") Map<String, JSONObject> assets = (Map<String, JSONObject>)queryResult.get("assets");
        // 遍历查询结果中的"assets"
        int pageNo = 1;
        int loop = 0;
        for (Map.Entry<String, JSONObject> entry : assets.entrySet()) {
            loop++;
            JSONObject jsonValue = entry.getValue();
            String runGameLink = jsonValue.getJSONArray("actions").getJSONObject(0).getString("link");
            String listingID = getListingID(runGameLink);
            runGameLink = runGameLink.replaceAll("%assetid%", entry.getKey());
            JSONObject wearQueryReport = new SkinWearQuery(runGameLink).query();

            SkinModel model = new SkinModel();
            model.setEnName(realName);
            model.setZhName(this.app.skinName.getText());
            model.setId(entry.getKey());
            model.setListingID(listingID);
            model.setWearValue(wearQueryReport.getJSONObject("iteminfo").getFloatValue("floatvalue"));
            model.setPageNo(pageNo);
            for (int i = 0; i < wearLevelList.length; i++) {
                if (wearLevelList[i].equals(this.app.wearLevel.getSelectionModel().getSelectedItem().toString())) {
                    model.setWearLevel(wearLevelMap[i]);
                    break;
                }
            }
            map.put(entry.getKey(), model);
            if (loop % 10 == 0) {
                pageNo++;
            }
        }

        // 填充皮肤的价格属性
        for (Map.Entry<String, SkinModel> entry : map.entrySet()) {
            entry.getValue().setPrice(parseToSkinPrice(queryResult.get("html").toString(), entry.getValue().getListingID()));
        }

        org.w3c.dom.Document resDoc;
        try {
            resDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        org.w3c.dom.Element skinsElement = resDoc.createElement("Skins");
        for (Map.Entry<String, SkinModel> entry : map.entrySet()) {
            SkinModel skinModel = entry.getValue();
            org.w3c.dom.Element skinElement = resDoc.createElement("Skin");
            skinElement.setAttribute("enName", skinModel.getEnName());
            skinElement.setAttribute("zhName", skinModel.getZhName());
            skinElement.setAttribute("assetID", skinModel.getId());
            skinElement.setAttribute("wearLevel", skinModel.getWearLevel());

            org.w3c.dom.Element listingIDElement = resDoc.createElement("ListingID");
            listingIDElement.setTextContent(skinModel.getListingID());
            skinElement.appendChild(listingIDElement);

            org.w3c.dom.Element wearElement = resDoc.createElement("Wear");
            wearElement.setTextContent(String.valueOf(skinModel.getWearValue()));
            skinElement.appendChild(wearElement);

            org.w3c.dom.Element priceElement = resDoc.createElement("Price");
            priceElement.setTextContent(skinModel.getPrice());
            skinElement.appendChild(priceElement);

            org.w3c.dom.Element pageElement = resDoc.createElement("PageNo");
            pageElement.setTextContent(String.valueOf(skinModel.getPageNo()));
            skinElement.appendChild(pageElement);

            skinsElement.appendChild(skinElement);
        }
        resDoc.appendChild(skinsElement);

        // 保存DOM
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        try {
            Date date = new Date();
            String dateStr = new SimpleDateFormat("MM-dd HH:mm").format(date);
            String path = this.app.skinName.getText() + dateStr + ".xml";
            Pattern pattern = Pattern.compile("[\\s\\\\/:*?\"<>|]");
            Matcher matcher = pattern.matcher(path);
            path = matcher.replaceAll("_"); // 将匹配到的非法字符以空格替换
            path = Main.reportPath + "/" + path;
            transformer.transform(new DOMSource(resDoc), new StreamResult(path));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从runGameLink中获取listingID
     * @param runGameLink String
     * @return String
     * @throws RuntimeException 当找不到ListingID时，抛出此异常
     */
    private String getListingID(String runGameLink) throws RuntimeException {
        Pattern pattern = Pattern.compile("%20M([0-9]*)A%assetid%");
        Matcher matcher = pattern.matcher(runGameLink);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new RuntimeException("Failed to find ListingID from " + runGameLink);
        }
    }

    /**
     * 从HTML里解析出皮肤的价格
     * @param html StringBuilder
     * @param listingID String
     * @return String
     */
    private String parseToSkinPrice(String html, String listingID) throws RuntimeException {
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementById("listing_" + listingID);
        if (element == null) {
            throw new RuntimeException("在爬取的HTML里找不到ListingID: " + listingID);
        }
        return element.getElementsByClass("market_listing_price market_listing_price_with_fee").text();
    }
}
