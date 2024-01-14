package main;

import java.sql.SQLException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.Product;

public class UserHome {
	Scene userHomeScene;
	BorderPane bp;
	GridPane gp;

	Label productListLabel, productNameLabel, productBrandLabel, priceLabel, totalPriceLabel;
	Label nameResultLabel, brandResultLabel, priceResultLabel, totalPriceResultLabel;
	Spinner<Integer> quantitySpinner;

	Button addToCartButton;

	TableView<Product> productListTable;
	TableColumn<Product, String> productNameCol, productBrandCol;
	TableColumn<Product, Integer> productStockCol, productPriceCol;

	MenuBar menuBar;
	Menu menu;

	private Connect connect = Connect.getInstance();
	private String userId;

	public UserHome(Stage stage, String userId) {
		this.userId = userId;

		initComponent();
		initUserHomePage();
		setStyle();
		getData();
		displayData();

		stage.setTitle("Home");
		stage.setScene(userHomeScene);

		// direct user to cart page when click cart menu item
		menu.getItems().get(1).setOnAction(e -> {
			new CartPage(stage, userId);
		});
		
		menu.getItems().get(2).setOnAction(e -> {
			new UserHistory(stage, userId);
		});
		
		menu.getItems().get(3).setOnAction(e -> {
			new LoginPage(stage);
		});

		addToCartButton.setOnMouseClicked(e -> {
			if (validateInput()) {
				addProductToCart();
				new CartPage(stage, userId);
			}
		});
	}

	private void initComponent() {
		bp = new BorderPane();
		gp = new GridPane();

		productListLabel = new Label("Product List");
		productNameLabel = new Label("Product Name  :");
		productBrandLabel = new Label("Product Brand  :");
		priceLabel = new Label("Price                 :");
		totalPriceLabel = new Label("Total Price        :");

		nameResultLabel = new Label();
		brandResultLabel = new Label();
		priceResultLabel = new Label();
		totalPriceResultLabel = new Label();

		addToCartButton = new Button("Add to Cart");

		quantitySpinner = new Spinner<>();
		SpinnerValueFactory<Integer> quantitySpinnerVal = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
		quantitySpinner.setValueFactory(quantitySpinnerVal);

		productListTable = new TableView<>();
		productNameCol = new TableColumn<>("Name");
		productBrandCol = new TableColumn<>("Brand");
		productStockCol = new TableColumn<>("Stock");
		productPriceCol = new TableColumn<>("Price");

		productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productBrandCol.setCellValueFactory(new PropertyValueFactory<>("productBrand"));
		productStockCol.setCellValueFactory(new PropertyValueFactory<>("productStock"));
		productPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
		productListTable.getColumns().addAll(productNameCol, productBrandCol, productStockCol, productPriceCol);

		menu = new Menu("Page");
		menuBar = new MenuBar();

		menu.getItems().addAll(new MenuItem("Home"), new MenuItem("Cart"), new MenuItem("History"),
				new MenuItem("Logout"));

		bp.setTop(menuBar);

		userHomeScene = new Scene(bp, 950, 600);
	}

	private void initUserHomePage() {
		menuBar.getMenus().add(menu);

		gp.add(productListLabel, 0, 0);
		gp.add(productListTable, 0, 1);

		gp.add(productNameLabel, 5, 0);
		gp.add(productBrandLabel, 5, 0);
		gp.add(priceLabel, 5, 0);
		gp.add(quantitySpinner, 5, 0);
		gp.add(totalPriceLabel, 5, 0);
		gp.add(addToCartButton, 5, 0);

		gp.add(nameResultLabel, 6, 0);
		gp.add(brandResultLabel, 6, 0);
		gp.add(priceResultLabel, 6, 0);
		gp.add(totalPriceResultLabel, 6, 0);

		bp.setCenter(gp);
	}

	private void setStyle() {
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setAlignment(Pos.CENTER);

		productNameLabel.setTranslateX(-30);
		productBrandLabel.setTranslateX(-30);
		priceLabel.setTranslateX(-30);
		quantitySpinner.setTranslateX(-30);
		totalPriceLabel.setTranslateX(-30);
		addToCartButton.setTranslateX(-30);

		productNameLabel.setTranslateY(50);
		productBrandLabel.setTranslateY(80);
		priceLabel.setTranslateY(110);
		quantitySpinner.setTranslateY(140);
		totalPriceLabel.setTranslateY(170);
		addToCartButton.setTranslateY(205);

		nameResultLabel.setTranslateY(50);
		brandResultLabel.setTranslateY(80);
		priceResultLabel.setTranslateY(110);
		totalPriceResultLabel.setTranslateY(170);

		productListLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.LIGHT, 16));
		productListTable.setMaxHeight(300);

		productNameCol.setPrefWidth(bp.getWidth() / 7);
		productBrandCol.setPrefWidth(bp.getWidth() / 7);
		productStockCol.setPrefWidth(bp.getWidth() / 7);
		productPriceCol.setPrefWidth(bp.getWidth() / 7);
	}

	private void getData() {
		String query = "SELECT * FROM msproduct WHERE ProductStock > 0";
		connect.execQuery(query);

		try {
			while (connect.rs.next()) {
				String productId = connect.rs.getString("ProductID");
				String productName = connect.rs.getString("ProductName");
				String productBrand = connect.rs.getString("ProductMerk");
				int productStock = connect.rs.getInt("ProductStock");
				int productPrice = connect.rs.getInt("ProductPrice");

				productListTable.getItems()
						.add(new Product(productId, productName, productBrand, productStock, productPrice));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void displayData() {
		productListTable.setOnMouseClicked(e -> {
			TableSelectionModel<Product> tableSelectionModel = productListTable.getSelectionModel();

			if (tableSelectionModel.isEmpty()) {
				return;
			}

			tableSelectionModel.setSelectionMode(SelectionMode.SINGLE);

			Product selectedProduct = tableSelectionModel.getSelectedItem();

			nameResultLabel.setText(selectedProduct.getProductName());
			brandResultLabel.setText(selectedProduct.getProductBrand());
			priceResultLabel.setText(String.valueOf(selectedProduct.getProductPrice()));

			final Product finalSelectedProduct = selectedProduct;
			quantitySpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
				if (finalSelectedProduct != null) {
					totalPriceResultLabel.setText(
							String.valueOf(finalSelectedProduct.getProductPrice() * quantitySpinner.getValue()));
				}
			});
		});
	}

	private boolean validateInput() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText("Warning");

		TableSelectionModel<Product> tableSelectionModel = productListTable.getSelectionModel();

		if (tableSelectionModel.isEmpty()) {
			alert.setContentText("Please choose 1 item!");
			alert.show();
			return false;
		}

		tableSelectionModel.setSelectionMode(SelectionMode.SINGLE);

		Product selectedProduct = tableSelectionModel.getSelectedItem();

		if (quantitySpinner.getValue() < 1 || quantitySpinner.getValue() > selectedProduct.getProductStock()) {
			alert.setContentText("Quantity must be more than 1 and less than product stock!");
			alert.show();
			return false;
		}

		// Update the product stock in the database
		Integer newStock = selectedProduct.getProductStock() - quantitySpinner.getValue();
		String productId = selectedProduct.getProductId();
		String query = String.format("UPDATE msproduct SET ProductStock = %d WHERE ProductID = '%s'", newStock,
				productId);
		connect.execUpdate(query);
		getData();

		return true;
	}

	private void addProductToCart() {
		TableSelectionModel<Product> tableSelectionModel = productListTable.getSelectionModel();

		if (tableSelectionModel.isEmpty()) {
			return;
		}

		tableSelectionModel.setSelectionMode(SelectionMode.SINGLE);

		Product selectedProduct = tableSelectionModel.getSelectedItem();
		String productId = selectedProduct.getProductId();

		int quantity = quantitySpinner.getValue();

		String checkQuery = String.format("SELECT * FROM carttable WHERE UserID = '%s' AND ProductID = '%s'", userId,
				productId);
		connect.execQuery(checkQuery);

		try {
			// if data exists
			if (connect.rs.next()) {
				int existingQuantity = connect.rs.getInt("Quantity");
				int newQuantity = existingQuantity + quantity;

				String updateQuery = String.format(
						"UPDATE carttable SET Quantity = %d WHERE UserID = '%s' AND ProductID = '%s'", newQuantity,
						userId, productId);
				connect.execUpdate(updateQuery);
			} else {
				String insertQuery = String.format(
						"INSERT INTO carttable (UserID, ProductID, Quantity) VALUES ('%s', '%s', %d)", userId,
						productId, quantity);
				connect.execUpdate(insertQuery);
			}

			getData();
			displayData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
