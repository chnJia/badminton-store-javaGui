package main;

import java.sql.SQLException;

import org.w3c.dom.NameList;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.Product;

public class AdminHome {
	Scene adminManageProductScene;
	BorderPane bp;
	GridPane gp;
	FlowPane fp;

	Label productListLabel, productNameLabel, productBrandLabel, productPriceLabel, nameLabel, addstockLabel,
			deleteProductLabel, nameResultLabel;
	TextField productNameTf, productPriceTf;

	ComboBox<String> productBrandBox;
	Spinner<Integer> quantitySpinner;

	Button addProductButton, addStockButton, deleteButton;

	TableView<Product> productListTable;
	TableColumn<Product, String> productNameCol, productBrandCol;
	TableColumn<Product, Integer> productStockCol, productPriceCol;

	MenuBar menuBar;
	Menu menu;

	private Connect connect = Connect.getInstance();

	public AdminHome(Stage stage) {
		initComponent();
		initUserHomePage();
		setStyle();
		getData();
		displayData();
		updateData();

		// direct admin to view history page when click view history menu item
		menu.getItems().get(1).setOnAction(e -> {
			new AdminHistory(stage);
		});
		
		menu.getItems().get(2).setOnAction(e -> {
			new LoginPage(stage);
		});

		stage.setTitle("Manage Product");
		stage.setScene(adminManageProductScene);
	}

	private void initComponent() {
		bp = new BorderPane();
		gp = new GridPane();

		productListLabel = new Label("Product List");
		productNameLabel = new Label("Product Name");
		productBrandLabel = new Label("Product Brand");
		productPriceLabel = new Label("Product Price");
		nameLabel = new Label("Name			:");
		addstockLabel = new Label("Add Stock");
		deleteProductLabel = new Label("Delete Product");
		nameResultLabel = new Label();

		productNameTf = new TextField();
		productPriceTf = new TextField();

		productBrandBox = new ComboBox<>();

		quantitySpinner = new Spinner<>();

		addProductButton = new Button("Add Product");
		addStockButton = new Button("Add Stock");
		deleteButton = new Button("Delete");

		productListTable = new TableView<>();
		productNameCol = new TableColumn<>("Name");
		productBrandCol = new TableColumn<>("Brand");
		productStockCol = new TableColumn<>("Stock");
		productPriceCol = new TableColumn<>("Price");

		menu = new Menu("Admin");
		menuBar = new MenuBar();
		
		SpinnerValueFactory<Integer> quantitySpinnerVal = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
		quantitySpinner.setValueFactory(quantitySpinnerVal);

		productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productBrandCol.setCellValueFactory(new PropertyValueFactory<>("productBrand"));
		productStockCol.setCellValueFactory(new PropertyValueFactory<>("productStock"));
		productPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
		productListTable.getColumns().addAll(productNameCol, productBrandCol, productStockCol, productPriceCol);

		menu.getItems().addAll(new MenuItem("Manage Product"), new MenuItem("View History"), new MenuItem("Logout"));

		bp.setTop(menuBar);

		adminManageProductScene = new Scene(bp, 950, 600);
	}

	private void initUserHomePage() {
		gp.add(productListLabel, 0, 0);
		gp.add(productListTable, 0, 1);

		gp.add(productNameLabel, 4, 0);
		gp.add(productNameTf, 4, 0);

		gp.add(productBrandLabel, 4, 0);
		gp.add(productBrandBox, 4, 0);

		gp.add(productPriceLabel, 4, 0);
		gp.add(productPriceTf, 4, 0);
		gp.add(addProductButton, 4, 0);

		gp.add(nameLabel, 0, 4);
		gp.add(nameResultLabel, 1, 4);
		gp.add(addstockLabel, 0, 5);
		gp.add(deleteProductLabel, 1, 5);
		gp.add(deleteButton, 1, 6);
		gp.add(quantitySpinner, 0, 5);
		gp.add(addStockButton, 0, 6);

		menuBar.getMenus().add(menu);

		bp.setCenter(gp);
	}

	private void setStyle() {
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setAlignment(Pos.CENTER);

		productListLabel.setTranslateX(170);
		productNameLabel.setTranslateX(-100);
		productNameTf.setTranslateX(-100);
		productBrandLabel.setTranslateX(-100);
		productBrandBox.setTranslateX(-100);
		productPriceLabel.setTranslateX(-100);
		productPriceTf.setTranslateX(-100);
		addProductButton.setTranslateX(-100);
		nameLabel.setTranslateX(250);
		nameResultLabel.setTranslateX(-50);
		addstockLabel.setTranslateX(220);
		deleteProductLabel.setTranslateX(-50);
		deleteButton.setTranslateX(-50);
		quantitySpinner.setTranslateX(180);
		addStockButton.setTranslateX(190);

		productNameLabel.setTranslateY(30);
		productNameTf.setTranslateY(60);
		productBrandLabel.setTranslateY(90);
		productBrandBox.setTranslateY(120);
		productPriceLabel.setTranslateY(150);
		productPriceTf.setTranslateY(180);
		addProductButton.setTranslateY(215);
		addstockLabel.setTranslateY(10);
		deleteProductLabel.setTranslateY(10);
		quantitySpinner.setTranslateY(40);
		addStockButton.setTranslateY(40);
		deleteButton.setTranslateY(40);

		productListLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.LIGHT, 24));
		nameLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.LIGHT, 16));
		nameResultLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.LIGHT, 16));
		productListTable.setMaxHeight(300);

		productNameCol.setPrefWidth(bp.getWidth() / 8);
		productBrandCol.setPrefWidth(bp.getWidth() / 8);
		productStockCol.setPrefWidth(bp.getWidth() / 7);
		productPriceCol.setPrefWidth(bp.getWidth() / 7);
		addStockButton.setPrefWidth(120);
		deleteButton.setPrefWidth(80);
	}

	private void getData() {
		String query = "SELECT * FROM msproduct";
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

		// Fetch all product brand from db
		String brandQuery = "SELECT DISTINCT ProductMerk FROM msproduct";
		connect.execQuery(brandQuery);

		try {
			while (connect.rs.next()) {
				String brand = connect.rs.getString("ProductMerk");
				productBrandBox.getItems().add(brand);
				productBrandBox.getSelectionModel().selectFirst();
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
		});
	}

	private void updateData() {
		TableSelectionModel<Product> tableSelectionModel = productListTable.getSelectionModel();
		tableSelectionModel.setSelectionMode(SelectionMode.SINGLE);

		// update product stock
		addStockButton.setOnMouseClicked(e -> {
			Product selectedProduct = tableSelectionModel.getSelectedItem();

			if (selectedProduct != null) {
				Integer newStock = selectedProduct.getProductStock() + quantitySpinner.getValue();
				String productId = selectedProduct.getProductId();

				String query = String.format("UPDATE msproduct SET ProductStock = %d WHERE ProductID = '%s'", newStock,
						productId);
				connect.execUpdate(query);

				selectedProduct.setProductStock(newStock);
				productListTable.refresh();
			}
		});

		// delete product
		deleteButton.setOnMouseClicked(event -> {
			Product selectedProduct = tableSelectionModel.getSelectedItem();

			if (selectedProduct != null) {
				String productId = selectedProduct.getProductId();

				String query = String.format("DELETE FROM msproduct WHERE ProductID = '%s'", productId);
				connect.execUpdate(query);

				productListTable.getItems().remove(selectedProduct);
				productListTable.refresh();
			}
		});

		// add new product
		addProductButton.setOnMouseClicked(ev -> {
			int productIndex = getLastStoredIndexFromDatabase();
			productIndex++;
			String productId = String.format("PD%03d", productIndex);

			String productName = productNameTf.getText();
			String productBrand = productBrandBox.getSelectionModel().getSelectedItem(); // Use getSelectedItem() to get
																							// the selected item from
																							// ComboBox
			Integer productPrice = Integer.parseInt(productPriceTf.getText());
			Integer productStock = 100;

			String query = String.format(
					"INSERT INTO msproduct (ProductID, ProductName, ProductMerk, ProductPrice, ProductStock) VALUES ('%s', '%s', '%s', %d, %d)",
					productId, productName, productBrand, productPrice, productStock);

			connect.execUpdate(query);

			productListTable.getItems()
					.add(new Product(productId, productName, productBrand, productStock, productPrice));
			productListTable.refresh();

			productNameTf.clear();
			productBrandBox.getSelectionModel().clearSelection();
			productPriceTf.clear();
		});

	}

	private int getLastStoredIndexFromDatabase() {
		try {
			String query = "SELECT MAX(ProductID) FROM msproduct";
			connect.execQuery(query);

			if (connect.rs.next()) {
				String maxProductId = connect.rs.getString(1);
				if (maxProductId != null) {
					return Integer.parseInt(maxProductId.replaceAll("[^0-9]", ""));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

}
