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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginPage {
	Scene scene;
	BorderPane loginBp;
	GridPane loginGp;
	FlowPane loginFp;

	Label emailLabel;
	TextField emailTf;
	Label passwordLabel;
	PasswordField passwordTf;

	Button loginButton;

	Menu loginMenu;

	MenuBar menuBar;
	Menu menu;

	private Connect connect = Connect.getInstance();

	public LoginPage(Stage stage) {
		initialize();
		initloginBp();
		initloginGp();
		style();
		
		// direct user to user | admin page when click submit button
		loginButton.setOnMouseClicked(event -> {
			if (validateInput()) {	
        // validate userRole (admin | user)
				String query = "SELECT * FROM msuser WHERE UserEmail = '" + emailTf.getText() + "'";
				connect.execQuery(query);
				
				try {
					while (connect.rs.next()) {
						String role = connect.rs.getString("UserRole");
						String email = connect.rs.getString("UserEmail");
			            
						if (role.equals("User")) {
							System.out.println(role);
							System.out.println(email);
							String userId = connect.rs.getString("UserID");
                            new UserHome(stage, userId);
						} 
						
						if (role.equals("Admin")) {
							System.out.println(role);
							System.out.println(email);
							new AdminHome(stage);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				clearInput();
			}
		});

		// direct user to register page when click register menu item
		menu.getItems().get(1).setOnAction(event -> {
			new RegisterPage(stage);
		});

		stage.setScene(scene);

		stage.setTitle("Login");
		stage.show();
	}

	private void initialize() {
		loginBp = new BorderPane();
		loginGp = new GridPane();
		loginFp = new FlowPane();

		loginMenu = new Menu("Page");

		emailLabel = new Label("Email");
		emailTf = new TextField();
		passwordLabel = new Label("Password");
		passwordTf = new PasswordField();
		loginButton = new Button("Login");

		menu = new Menu("Page");
		menuBar = new MenuBar();

		menu.getItems().addAll(new MenuItem("Login"), new MenuItem("Register"));

		scene = new Scene(loginBp, 950, 600);
	}

	private void initloginBp() {
		loginBp.setCenter(loginGp);

		menuBar.getMenus().add(menu);

		loginBp.setTop(menuBar);

		loginBp.setCenter(loginGp);
	}

	private void initloginGp() {
		loginGp.add(emailLabel, 0, 0);
		loginGp.add(emailTf, 0, 1);
		loginGp.add(passwordLabel, 0, 2);
		loginGp.add(passwordTf, 0, 3);
		loginGp.add(loginButton, 0, 4);
	}

	private void style() {
		loginGp.setAlignment(Pos.CENTER);
		loginGp.setHgap(20);
		loginGp.setVgap(10);

		loginButton.setPrefWidth(100);
		loginButton.setPrefHeight(10);

		emailTf.setPrefWidth(200);
		passwordTf.setPrefWidth(200);
	}

	private boolean validateInput() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText("Warning");

		String query = "SELECT * FROM msuser";
		connect.execQuery(query);

		if (emailTf.getText().isEmpty() || passwordTf.getText().isEmpty()) {
			alert.setContentText("Email or Password must be Filled!");
			alert.show();
			return false;
		}

		try {
			while (connect.rs.next()) {
				String email = connect.rs.getString("UserEmail");
				String password = connect.rs.getString("UserPassword");

				if (emailTf.getText().equals(email) && passwordTf.getText().equals(password)) {
					return true;
				}
			}

			alert.setContentText("Wrong Email or Password!");
			alert.show();
			return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}

	private void clearInput() {
		emailTf.clear();
		passwordTf.clear();
	}
	

}
