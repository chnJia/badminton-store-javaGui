package main;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.labs.scene.control.window.Window;
import models.Cart;
import models.TransactionHeader;

public class TransactionPopUp {
	Window window = new Window("Transaction Card");
	Scene popUpScene;
	Stage popUpStage;

	BorderPane bp;
	GridPane gp;
	VBox productListContainer, productPriceContainer;

	Label listLabel, courierLabel, totalPriceLabel, totalPriceResultLabel, transactionTitleLabel;

	ComboBox<String> courierBox;
	CheckBox insuranceCheckBox;

	Button checkoutButton;

	int totalPriceSum = 0;
	int insurance = 0;

	private List<String> productNames;
	private List<Integer> productPrices;
	private String userId;
	private TableView<Cart> cartListTable;
	private CartPage cartPage;
	private Connect connect = Connect.getInstance();

	public TransactionPopUp(Stage stage, List<String> productNames, List<Integer> productPrices, String userId,
			TableView<Cart> cartListTable, CartPage cartPage) {
		this.productNames = productNames;
		this.productPrices = productPrices;
		this.userId = userId;
		this.cartListTable = cartListTable;
		this.cartPage = cartPage;

		initComponent();
		initPopUpPage();
		setStyle();
		getData();
		displayData();
		setAction();

		popUpStage.initOwner(stage);
		popUpStage.initModality(Modality.WINDOW_MODAL);

		popUpStage.setScene(popUpScene);

		popUpStage.show();
	}

	private void initComponent() {
		gp = new GridPane();
		bp = new BorderPane();
		productListContainer = new VBox();
		productPriceContainer = new VBox();

		transactionTitleLabel = new Label("Transaction Card");
		listLabel = new Label("List");
		courierLabel = new Label("Courier");
		totalPriceLabel = new Label("Total Price");
		totalPriceResultLabel = new Label();

		courierBox = new ComboBox<>();
		insuranceCheckBox = new CheckBox("Use Insurance");

		checkoutButton = new Button("Checkout");

		popUpStage = new Stage(StageStyle.UTILITY);
		popUpScene = new Scene(bp, 800, 450);
	}

	private void initPopUpPage() {
		gp.add(listLabel, 0, 0);
		gp.add(productListContainer, 0, 1);
		gp.add(productPriceContainer, 0, 1);
		gp.add(courierLabel, 0, 3);
		gp.add(courierBox, 0, 4);
		gp.add(insuranceCheckBox, 0, 5);
		gp.add(totalPriceLabel, 0, 6);
		gp.add(totalPriceResultLabel, 0, 6);
		gp.add(checkoutButton, 0, 7);

		bp.setCenter(window);
		window.getContentPane().getChildren().add(gp);
	}

	private void setStyle() {
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(30);
		gp.setVgap(10);
		gp.setMaxWidth(950);

		listLabel.setTranslateX(100);
		courierLabel.setTranslateX(90);
		courierBox.setTranslateX(50);
		insuranceCheckBox.setTranslateX(50);
		checkoutButton.setPrefWidth(300);
		checkoutButton.setPrefHeight(30);
		productPriceContainer.setTranslateX(150);
		totalPriceResultLabel.setTranslateX(150);
	}

	private void getData() {
		try {
			String courierQuery = "SELECT DISTINCT CourierType FROM transactionheader";
			connect.execQuery(courierQuery);

			while (connect.rs.next()) {
				String courierType = connect.rs.getString("CourierType");

				courierBox.getItems().add(courierType);
				courierBox.getSelectionModel().selectFirst();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void displayData() {
		for (String productName : productNames) {
			Label productNameResultLabel = new Label(productName + "	: ");
			productListContainer.getChildren().add(productNameResultLabel);
		}

		for (Integer price : productPrices) {
			Label productPriceResultLabel = new Label(String.valueOf(price));
			productPriceContainer.getChildren().add(productPriceResultLabel);
			totalPriceSum += price;
		}

		totalPriceResultLabel.setText(String.valueOf(": " + totalPriceSum));

		insuranceCheckBox.setOnAction(event -> {
			if (insuranceCheckBox.isSelected()) {
				int additionalInsurancePrice = 90000;
				totalPriceResultLabel.setText(": " + (totalPriceSum + additionalInsurancePrice));
				insurance += 1;
			} else {
				totalPriceResultLabel.setText(String.valueOf(": " + totalPriceSum));
			}
		});
	}

	private void setAction() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Are you sure you want to Checkout all the items?");
		alert.setContentText("Need Confirmation");

		// Create buttons for OK and Cancel
		ButtonType buttonTypeOK = new ButtonType("OK", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

		checkoutButton.setOnMouseClicked(e -> {
			Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get() == buttonTypeOK) {
				int transactionIndex = getLastStoredIndexFromDatabase();
				transactionIndex++;
				String transactionId = String.format("TH%03d", transactionIndex);

				String userID = userId;

				LocalDate today = LocalDate.now();
				String transactionDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

				Integer deliveryInsurance = insurance;
				String courierType = courierBox.getSelectionModel().getSelectedItem();

				// transaction header
				String insertTh = String.format(
						"INSERT INTO transactionheader (TransactionID, UserID, TransactionDate, DeliveryInsurance, CourierType) "
								+ "VALUES ('%s', '%s', '%s', %d, '%s')",
						transactionId, userId, transactionDate, deliveryInsurance, courierType);
				connect.execUpdate(insertTh);

				// transaction detail
				for (Cart cartItem : cartListTable.getItems()) {
					String productId = cartItem.getProductId();
					int quantity = cartItem.getQuantity();

					String insertTd = String
							.format("INSERT INTO transactiondetail (ProductID, TransactionID, Quantity) "
									+ "VALUES ('%s', '%s', %d)", productId, transactionId, quantity);
					connect.execUpdate(insertTd);
				}

				cartPage.clearCart();
				popUpStage.close();

			} else {
				popUpStage.close();
			}
		});
	}

	private int getLastStoredIndexFromDatabase() {
		try {
			String query = "SELECT MAX(TransactionID) FROM transactionheader";
			connect.execQuery(query);

			if (connect.rs.next()) {
				String maxTransId = connect.rs.getString(1);
				if (maxTransId != null) {
					return Integer.parseInt(maxTransId.replaceAll("[^0-9]", ""));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

}
