package main;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import models.Cart;

public class CartPage {
	Scene userCartScene;
	BorderPane bp;
	GridPane gp;
	FlowPane fp;

	Label cartListLabel, productNameLabel, productBrandLabel, productPriceLabel, cartTotalPriceLabel;
	Label nameResultLabel, brandResultLabel, priceResultLabel, cartTotalPriceResultLabel;

	Button checkoutButton, deleteProductButton;

	TableView<Cart> cartListTable;
	TableColumn<Cart, String> productNameCol, productBrandCol;
	TableColumn<Cart, Integer> productPriceCol, quantityCol, totalPriceCol;

	MenuBar menuBar;
	Menu menu;

	private String userId;
	private List<String> productNames;
	private List<Integer> productPrices;
	private Connect connect = Connect.getInstance();

	public CartPage(Stage stage, String userId) {
		this.userId = userId;
		this.productNames = new ArrayList<>();
		this.productPrices = new ArrayList<>();
		this.cartListTable = new TableView<>();

		initComponent();
		initUserCartPage();
		setStyle();
		displayData();
		removeCart();

		stage.setTitle("Cart");
		stage.setScene(userCartScene);

		// direct user to transaction PopUp when checkout successful
		checkoutButton.setOnMouseClicked(e -> {
			if (validateCheckoutAction()) {
				new TransactionPopUp(stage, productNames, productPrices, userId, cartListTable, this);
			}
		});

		// direct user to user home page when click "Home" menu item
		menu.getItems().get(0).setOnAction(e -> {
			new UserHome(stage, userId);
		});

		menu.getItems().get(2).setOnAction(e -> {
			new UserHistory(stage, userId);
		});
		
		menu.getItems().get(3).setOnAction(e -> {
			new LoginPage(stage);
		});
	}

	private void initComponent() {
		bp = new BorderPane();
		gp = new GridPane();
		fp = new FlowPane();

		cartListLabel = new Label("Your Cart List");
		productNameLabel = new Label("Name		:");
		productBrandLabel = new Label("Brand		:");
		productPriceLabel = new Label("Price			:");
		cartTotalPriceLabel = new Label("Total Price	:");

		nameResultLabel = new Label();
		brandResultLabel = new Label();
		priceResultLabel = new Label();
		cartTotalPriceResultLabel = new Label();

		checkoutButton = new Button("Checkout");
		deleteProductButton = new Button("Delete Product");

//		cartListTable = new TableView<>();
		productNameCol = new TableColumn<>("Name");
		productBrandCol = new TableColumn<>("Brand");
		productPriceCol = new TableColumn<>("Price");
		quantityCol = new TableColumn<>("Quantity");
		totalPriceCol = new TableColumn<>("Total");

		cartListTable.getColumns().addAll(productNameCol, productBrandCol, productPriceCol, quantityCol, totalPriceCol);
		productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productBrandCol.setCellValueFactory(new PropertyValueFactory<>("productBrand"));
		productPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
		quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("productTotalPrice"));

		menu = new Menu("Page");
		menuBar = new MenuBar();

		menu.getItems().addAll(new MenuItem("Home"), new MenuItem("Cart"), new MenuItem("History"),
				new MenuItem("Logout"));

		bp.setTop(menuBar);

		userCartScene = new Scene(bp, 950, 600);
	}

	private void initUserCartPage() {
		menuBar.getMenus().add(menu);

		gp.add(cartListLabel, 0, 0);
		gp.add(cartListTable, 0, 1);

		gp.add(productNameLabel, 6, 0);
		gp.add(productBrandLabel, 6, 0);
		gp.add(productPriceLabel, 6, 0);
		gp.add(cartTotalPriceLabel, 6, 0);

		gp.add(nameResultLabel, 7, 0);
		gp.add(brandResultLabel, 7, 0);
		gp.add(priceResultLabel, 7, 0);
		gp.add(cartTotalPriceResultLabel, 7, 0);

		gp.add(checkoutButton, 0, 15);
		gp.add(deleteProductButton, 0, 16);

		bp.setCenter(gp);
	}

	private void setStyle() {
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setAlignment(Pos.CENTER);

		checkoutButton.setMaxWidth(600);
		deleteProductButton.setMaxWidth(600);

		checkoutButton.setTranslateX(50);
		deleteProductButton.setTranslateX(50);

		checkoutButton.setTranslateY(-130);
		deleteProductButton.setTranslateY(-130);

		productNameLabel.setTranslateY(40);
		productBrandLabel.setTranslateY(70);
		productPriceLabel.setTranslateY(100);
		cartTotalPriceLabel.setTranslateY(130);

		nameResultLabel.setTranslateY(40);
		brandResultLabel.setTranslateY(70);
		priceResultLabel.setTranslateY(100);
		cartTotalPriceResultLabel.setTranslateY(130);

		productNameLabel.setTranslateX(-40);
		productBrandLabel.setTranslateX(-40);
		productPriceLabel.setTranslateX(-40);
		cartTotalPriceLabel.setTranslateX(-40);

		cartListLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.LIGHT, 16));
		cartListTable.setPrefHeight(300);
	}

	private void displayData() {
		cartListTable.getItems().clear();

		String query = "SELECT cartTable.*, msproduct.ProductName, msproduct.ProductMerk, msproduct.ProductPrice, msproduct.ProductStock\n"
				+ "FROM cartTable\n" + "JOIN msproduct ON cartTable.ProductID = msproduct.ProductID\n"
				+ "WHERE cartTable.UserID = '" + userId + "'";

		connect.execQuery(query);

		int totalCartPrice = 0;

		try {
			while (connect.rs.next()) {
				String productId = connect.rs.getString("ProductID");
				String productName = connect.rs.getString("ProductName");
				String productBrand = connect.rs.getString("ProductMerk");
				Integer productPrice = connect.rs.getInt("productPrice");
				Integer productStock = connect.rs.getInt("ProductStock");
				Integer quantity = connect.rs.getInt("Quantity");

				Cart cartItem = new Cart(userId, productId, quantity);

				Integer totalPrice = productPrice * quantity;

				totalCartPrice += totalPrice;

				cartItem.setProductName(productName);
				cartItem.setProductBrand(productBrand);
				cartItem.setProductPrice(productPrice);
				cartItem.setProductStock(productStock);
				cartItem.setProductTotalPrice(totalPrice);

				cartListTable.getItems().add(cartItem);

				this.productNames.add(productName);
				this.productPrices.add(productPrice);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		cartTotalPriceResultLabel.setText(String.valueOf(totalCartPrice));

		cartListTable.setOnMouseClicked(e -> {
			TableSelectionModel<Cart> tableSelectionModel = cartListTable.getSelectionModel();

			if (tableSelectionModel.isEmpty()) {
				return;
			}

			tableSelectionModel.setSelectionMode(SelectionMode.SINGLE);

			Cart selectedCart = tableSelectionModel.getSelectedItem();

			nameResultLabel.setText(selectedCart.getProductName());
			brandResultLabel.setText(selectedCart.getProductBrand());
			priceResultLabel.setText(String.valueOf(selectedCart.getProductPrice()));
		});
	}

	private void removeCart() {
		TableSelectionModel<Cart> tableSelectionModel = cartListTable.getSelectionModel();
		tableSelectionModel.setSelectionMode(SelectionMode.SINGLE);

		deleteProductButton.setOnMouseClicked(e -> {
			if (validateDeleteAction()) {
				Cart selectedCart = tableSelectionModel.getSelectedItem();

				if (selectedCart != null) {
					String productId = selectedCart.getProductId();
					int quantity = selectedCart.getQuantity();

					// delete product from cart
					String query = String.format("DELETE FROM carttable WHERE ProductID = '%s'", productId);
					connect.execUpdate(query);

					cartListTable.getItems().remove(selectedCart);

					// update product stock to current stock
					String updateStockQuery = String.format(
							"UPDATE msproduct SET ProductStock = ProductStock + %d WHERE ProductID = '%s'", quantity,
							productId);
					connect.execUpdate(updateStockQuery);

					cartListTable.refresh();
				}
			}
		});
	}

	private boolean validateDeleteAction() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText("Warning");

		TableSelectionModel<Cart> tableSelectionModel = cartListTable.getSelectionModel();

		if (tableSelectionModel.isEmpty()) {
			alert.setContentText("Please Select product to Delete");
			alert.show();
			return false;
		}

		return true;
	}

	private boolean validateCheckoutAction() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText("Warning");

		if (cartListTable.getItems().isEmpty()) {
			alert.setContentText("Please insert item to your cart");
			alert.show();
			return false;
		}

		return true;
	}

	public void clearCart() {
		String clearCartQuery = String.format("DELETE FROM carttable WHERE UserID = '%s'", userId);
        connect.execUpdate(clearCartQuery);

        cartListTable.getItems().clear();
        cartListTable.refresh();
        
        nameResultLabel.setText("");
		brandResultLabel.setText("");
		priceResultLabel.setText("");
		cartTotalPriceResultLabel.setText("");
	}

}