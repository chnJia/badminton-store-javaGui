package main;

import java.sql.SQLException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.TransactionDetail;
import models.TransactionHeader;

public class UserHistory {
	Scene userHistoryScene;
	BorderPane bp;
	GridPane gp;

	Label myTransactionLabel, TransactionDetailLabel, totalPriceLabel, totalPriceResultLabel;

	TableView<TransactionHeader> myTransactionTable;
	TableColumn<TransactionHeader, String> transactionIdCol, emailCol, dateCol;

	TableView<TransactionDetail> transactionDetaiTable;
	TableColumn<TransactionDetail, String> detailTrIdCol, productNameCol;
	TableColumn<TransactionDetail, Integer> priceCol, quantityCol, totalPriceCol;

	MenuBar menuBar;
	Menu menu;

	private Connect connect = Connect.getInstance();
	private String userId;

	public UserHistory(Stage stage, String userId) {
		this.userId = userId;

		initComponent();
		initHistoryPage();
		setStyle();
		getData();

		menu.getItems().get(0).setOnAction(e -> {
			new UserHome(stage, userId);
		});

		menu.getItems().get(1).setOnAction(e -> {
			new CartPage(stage, userId);
		});

		menu.getItems().get(3).setOnAction(e -> {
			new LoginPage(stage);
		});

		stage.setTitle("My History");
		stage.setScene(userHistoryScene);
		stage.show();
	}

	private void initComponent() {
		bp = new BorderPane();
		gp = new GridPane();

		myTransactionLabel = new Label("My Transaction");
		TransactionDetailLabel = new Label("Transaction Detail");
		totalPriceLabel = new Label("Total Price	:");
		totalPriceResultLabel = new Label();

		myTransactionTable = new TableView<>();
		transactionIdCol = new TableColumn<>("ID");
		emailCol = new TableColumn<>("Email");
		dateCol = new TableColumn<>("Date");

		myTransactionTable.getColumns().addAll(transactionIdCol, emailCol, dateCol);
		transactionIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
		emailCol.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
		dateCol.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

		transactionDetaiTable = new TableView<>();
		detailTrIdCol = new TableColumn<>("ID");
		productNameCol = new TableColumn<>("Product");
		priceCol = new TableColumn<>("Price");
		quantityCol = new TableColumn<>("Quantity");
		totalPriceCol = new TableColumn<>("Total Price");

		transactionDetaiTable.getColumns().addAll(detailTrIdCol, productNameCol, priceCol, quantityCol, totalPriceCol);
		detailTrIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
		productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
		priceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("productTotalPrice"));

		menu = new Menu("Page");
		menuBar = new MenuBar();

		menu.getItems().addAll(new MenuItem("Home"), new MenuItem("Cart"), new MenuItem("History"),
				new MenuItem("Logout"));

		bp.setTop(menuBar);

		userHistoryScene = new Scene(bp, 950, 600);
	}

	private void initHistoryPage() {
		gp.add(myTransactionLabel, 0, 0);
		gp.add(myTransactionTable, 0, 1);

		gp.add(TransactionDetailLabel, 1, 0);
		gp.add(transactionDetaiTable, 1, 1);

		gp.add(totalPriceLabel, 1, 4);
		gp.add(totalPriceResultLabel, 1, 4);

		bp.setCenter(gp);
		menuBar.getMenus().add(menu);
	}

	private void setStyle() {
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setAlignment(Pos.CENTER);

		totalPriceResultLabel.setTranslateX(90);

		transactionIdCol.setPrefWidth(bp.getWidth() / 7);
		emailCol.setPrefWidth(bp.getWidth() / 7);
		dateCol.setPrefWidth(bp.getWidth() / 7);

		detailTrIdCol.setPrefWidth(bp.getWidth() / 9.5);
		productNameCol.setPrefWidth(bp.getWidth() / 9.5);
		priceCol.setPrefWidth(bp.getWidth() / 9.5);
		quantityCol.setPrefWidth(bp.getWidth() / 9.5);
		totalPriceCol.setPrefWidth(bp.getWidth() / 9.5);

		myTransactionLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.LIGHT, 16));
		TransactionDetailLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.LIGHT, 16));
	}

	private void getData() {
		try {
			String query = "SELECT th.TransactionID, th.UserID, th.TransactionDate, th.DeliveryInsurance, th.CourierType, u.UserEmail "
	                + "FROM transactionheader th " + "JOIN msuser u ON th.userID = u.UserID "
	                + "WHERE th.UserID = '" + userId + "'";
			connect.execQuery(query);

			while (connect.rs.next()) {
				String transactionId = connect.rs.getString("TransactionID");
				String userId = connect.rs.getString("UserID");
				String transactionDate = connect.rs.getString("TransactionDate");
				Integer deliveryInsurance = connect.rs.getInt("DeliveryInsurance");
				String courierType = connect.rs.getString("CourierType");
				String userEmail = connect.rs.getString("UserEmail");

				TransactionHeader transactionHeader = new TransactionHeader(transactionId, userId, transactionDate,
						deliveryInsurance, courierType);
				transactionHeader.setUserEmail(userEmail);

				myTransactionTable.getItems().add(transactionHeader);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// transaction detail data
		myTransactionTable.setOnMouseClicked(e -> {
			transactionDetaiTable.getItems().clear();

			int totalTransactionPrice = 0;
			
			TransactionHeader selectedTransaction = myTransactionTable.getSelectionModel().getSelectedItem();

			if (selectedTransaction != null) {
				try {
					String detailQuery = "SELECT td.ProductID, td.TransactionID, td.Quantity, p.ProductName, p.ProductPrice, p.ProductStock "
	                        + "FROM transactiondetail td " + "JOIN msproduct p ON td.ProductID = p.ProductID "
	                        + "WHERE td.TransactionID = '" + selectedTransaction.getTransactionId() + "'";
					connect.execQuery(detailQuery);

					while (connect.rs.next()) {
						String productId = connect.rs.getString("ProductID");
						Integer quantity = connect.rs.getInt("Quantity");
						String transactionId = connect.rs.getString("TransactionID");
						String productName = connect.rs.getString("ProductName");
						Integer productPrice = connect.rs.getInt("ProductPrice");
						Integer totalPrice = productPrice * quantity;

						TransactionDetail transactionDetail = new TransactionDetail(productId, transactionId, quantity);
						transactionDetail.setProductName(productName);
						transactionDetail.setProductPrice(productPrice);
						transactionDetail.setProductTotalPrice(totalPrice);
						
						transactionDetaiTable.getItems().add(transactionDetail);
						
						totalTransactionPrice += totalPrice;
						totalPriceResultLabel.setText(String.format("%s", totalTransactionPrice));
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

}
