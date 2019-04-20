package cn.razesoldier.csgo.query;

import com.sun.istack.internal.NotNull;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * parse.fxml的控制器
 */
public class ParseResultWindow {
    public TableColumn<Skin, String> assetID;
    public TableColumn<Skin, String> wear;
    public TableColumn<Skin, String> price;
    public TableColumn<Skin, String> listingID;
    public TableColumn<Skin, Hyperlink> pageNo;
    public TableView<Skin> tableView;

    /**
     * 表格
     */
    public static class Skin {
        private SimpleStringProperty assetID;
        private SimpleStringProperty listingID;
        private SimpleStringProperty wear;
        private SimpleStringProperty price;
        private SimpleIntegerProperty pageNo;

        public Skin(@NotNull String assetID, @NotNull String listingID, @NotNull String wear, @NotNull String price,
                    @NotNull Integer pageNo) {
            this.assetID = new SimpleStringProperty(assetID);
            this.listingID = new SimpleStringProperty(listingID);
            this.wear = new SimpleStringProperty(wear);
            this.price = new SimpleStringProperty(price);
            this.pageNo = new SimpleIntegerProperty(pageNo);
        }

        public String getAssetID() {
            return assetID.get();
        }

        public String getListingID() {
            return listingID.get();
        }

        public String getWear() {
            return wear.get();
        }

        public String getPrice() {
            return price.get();
        }

        public int getPageNo() {
            return pageNo.get();
        }
    }
}
