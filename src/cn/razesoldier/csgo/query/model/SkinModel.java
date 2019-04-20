package cn.razesoldier.csgo.query.model;

/**
 * 皮肤的模型化
 */
public class SkinModel {
    /**
     * 资产ID
     */
    private String id;

    /**
     * 列表ID
     */
    private String listingID;

    private String zhName;

    private String enName;

    private int pageNo;

    /**
     * 磨损值
     */
    private float wearValue;

    /**
     * 磨损等级，以英文表示
     */
    private String wearLevel;

    /**
     * 价钱，由前缀的美元符号和后缀的值组成
     */
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListingID() {
        return listingID;
    }

    public void setListingID(String listingID) {
        this.listingID = listingID;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public float getWearValue() {
        return wearValue;
    }

    public void setWearValue(float wearValue) {
        this.wearValue = wearValue;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getWearLevel() {
        return wearLevel;
    }

    public void setWearLevel(String wearLevel) {
        this.wearLevel = wearLevel;
    }
}
