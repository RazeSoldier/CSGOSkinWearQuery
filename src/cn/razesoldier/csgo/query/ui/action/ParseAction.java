package cn.razesoldier.csgo.query.ui.action;

import cn.razesoldier.csgo.query.Main;
import cn.razesoldier.csgo.query.ParseResultWindow;
import cn.razesoldier.csgo.query.ui.item.AlertBox;
import com.sun.istack.internal.NotNull;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ParseAction implements IAction {
    private Main mainRef;

    ParseAction(Main mainRef) {
        this.mainRef = mainRef;
    }

    public void exec() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择xml格式的查询报告");
        fileChooser.setInitialDirectory(new File(Main.reportPath));
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML", "*.xml"));
        File reportFile = fileChooser.showOpenDialog(mainRef.getPrimaryStage());
        if (reportFile == null) {
            return;
        }
        List<Element> skins = doParse(reportFile);
        if (skins.isEmpty()) {
            AlertBox box = new AlertBox(Alert.AlertType.INFORMATION, "没有可分析的数据", "没有可分析的数据");
            box.show();
            return;
        }

        ObservableList<ParseResultWindow.Skin> tableRows = FXCollections.observableArrayList();
        for (Element element : skins) {
            String assetID = element.getAttribute("assetID");
            String listingID = null;
            String wear = null;
            String price = null;
            Integer pageNo = null;
            for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                Node node = element.getChildNodes().item(i);
                if (node instanceof Element) {
                    String tagName = ((Element) node).getTagName();
                    if (tagName.equals("ListingID")) {
                        listingID = "#listing_" + node.getTextContent();
                        continue;
                    }
                    if (tagName.equals("Wear")) {
                        wear = node.getTextContent();
                        continue;
                    }
                    if (tagName.equals("Price")) {
                        price = node.getTextContent().substring(1).replaceAll(",", "");
                        continue;
                    }
                    if (tagName.equals("PageNo")) {
                        pageNo = Integer.valueOf(node.getTextContent());
                    }
                }
            }
            tableRows.add(new ParseResultWindow.Skin(assetID, listingID, wear, price, pageNo));
        }

        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("parse.fxml"));
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        ParseResultWindow controller = fxmlLoader.getController();
        // 绑定数据模型 @{
        controller.assetID.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getAssetID()));
        controller.wear.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getWear()));
        controller.price.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getPrice()));
        controller.listingID.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getListingID()));
        controller.listingID.setEditable(true);
        controller.listingID.setCellFactory(TextFieldTableCell.forTableColumn()); // 允许listingID列可编辑
        // 显示超链接
        controller.pageNo.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(new Hyperlink(String.valueOf(param.getValue().getPageNo()))));
        controller.pageNo.setCellFactory(new Callback<TableColumn<ParseResultWindow.Skin, Hyperlink>, TableCell<ParseResultWindow.Skin, Hyperlink>>() {
            @Override
            public TableCell<ParseResultWindow.Skin, Hyperlink> call(TableColumn<ParseResultWindow.Skin, Hyperlink> param) {
                return new TableCell<ParseResultWindow.Skin, Hyperlink>() {
                    @Override
                    protected void updateItem(Hyperlink item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(item);
                            item.setOnAction(event -> {
                                URI uri;
                                try {
                                    uri = new URI("https", "//steamcommunity.com/market/listings/730/" +
                                            skins.get(0).getAttribute("enName") + " (" + skins.get(0).getAttribute("wearLevel") + ")", null);
                                } catch (URISyntaxException e) {
                                    throw new RuntimeException(e.getLocalizedMessage());
                                }
                                try {
                                    Desktop.getDesktop().browse(uri);
                                } catch (IOException e) {
                                    throw new RuntimeException(e.getLocalizedMessage());
                                }
                            });
                        }
                    }
                };
            }
        });
        // @}
        controller.tableView.setItems(tableRows);

        // 自定义排序逻辑 @{
        controller.tableView.setSortPolicy(param -> {
            ObservableList<TableColumn<ParseResultWindow.Skin, ?>> columns = param.getSortOrder();
            if (columns.isEmpty()) {
                return true;
            }
            Comparator<ParseResultWindow.Skin> comparator = (a, b) -> {
                String id = columns.get(0).getId();
                // 判断是否是倒序
                if (columns.get(0).getSortType().equals(TableColumn.SortType.DESCENDING)) {
                    ParseResultWindow.Skin temp = b;
                    b = a;
                    a = temp;
                }
                switch (id) {
                    case "assetID":
                        return Float.compare(Float.valueOf(a.getAssetID()), Float.valueOf(b.getAssetID()));
                    case "wear":
                        return Float.compare(Float.valueOf(a.getWear()), Float.valueOf(b.getWear()));
                    case "price":
                        return Float.compare(Float.valueOf(a.getPrice()), Float.valueOf(b.getPrice()));
                    case "listingID":
                        Float f1 = Float.valueOf(a.getListingID().replaceAll("#listing_", ""));
                        Float f2 = Float.valueOf(b.getListingID().replaceAll("#listing_", ""));
                        return Float.compare(f1, f2);
                    case "pageNo":
                        return Integer.compare(a.getPageNo(), b.getPageNo());
                    default:
                        return 0;
                }
            };
            FXCollections.sort(param.getItems(), comparator);
            return true;
        });
        // @}

        Stage stage = new Stage();
        stage.setTitle("分析结果");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private List<Element> doParse(@NotNull File reportFile) {
        Document reportDOM;
        try {
            reportDOM = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(reportFile);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        NodeList skinsElement = reportDOM.getElementsByTagName("Skins").item(0).getChildNodes();
        List<Element> skins = new ArrayList<>();
        for (int i = 0; i < skinsElement.getLength(); i++) {
            if (skinsElement.item(i) instanceof Element) {
                skins.add((Element)skinsElement.item(i));
            }
        }
        return skins;
    }
}
