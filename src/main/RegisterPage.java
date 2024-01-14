package main;

import java.sql.SQLException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterPage {
	Scene regisScene;
	BorderPane bp;
	GridPane gp;
	FlowPane fp;

	Label emailLabel, passwordLabel, confirmPassLabel, ageLabel, genderLabel, nationalityLabel;
	TextField emailField;
	PasswordField passwordField, confirmPasswordField;

	RadioButton maleRadio, femaleRadio;
	ToggleGroup genderGroup;
	VBox genderVBox;

	ComboBox<String> nationalityBox;
	Spinner<Integer> ageSpinner;

	Button registerButton;

	MenuBar menuBar;
	Menu menu;

	private Connect connect = Connect.getInstance();

	public RegisterPage(Stage stage) {
		initComponent();
		initRegisterForm();
		setStyle();
		stage.setTitle("Register");
		stage.setScene(regisScene);	

		// direct user to login page when click register menu item
		menu.getItems().get(0).setOnAction(event -> {
			new LoginPage(stage);
		});

		// direct user to home page when click register button (ROLE = USER)
		registerButton.setOnMouseClicked(event -> {
			if (validateInput()) {
				addData();
				clearInput();
				new LoginPage(stage);
			}
		});
	}

	private void initComponent() {
		bp = new BorderPane();
		gp = new GridPane();

		emailLabel = new Label("Email");
		passwordLabel = new Label("Password");
		confirmPassLabel = new Label("Confirm Password");
		ageLabel = new Label("Age");
		genderLabel = new Label("Gender");
		nationalityLabel = new Label("Nationality");
		emailField = new TextField();
		passwordField = new PasswordField();
		confirmPasswordField = new PasswordField();

		maleRadio = new RadioButton("Male");
		femaleRadio = new RadioButton("Female");
		genderGroup = new ToggleGroup();
		genderVBox = new VBox();

		nationalityBox = new ComboBox<>();
		ageSpinner = new Spinner<>();

		registerButton = new Button("Register");

		menu = new Menu("Page");
		menuBar = new MenuBar();

		menu.getItems().addAll(new MenuItem("Login"), new MenuItem("Register"));

		bp.setTop(menuBar);

		regisScene = new Scene(bp, 950, 600);
	}

	private void initRegisterForm() {
		maleRadio.setToggleGroup(genderGroup);
		femaleRadio.setToggleGroup(genderGroup);
		genderVBox.getChildren().addAll(maleRadio, femaleRadio);

		nationalityBox.getItems().add("Indonesia");
		nationalityBox.getItems().add("Singapore");
		nationalityBox.getSelectionModel().selectFirst();

		gp.add(emailLabel, 0, 0);
		gp.add(emailField, 0, 1);

		gp.add(passwordLabel, 0, 2);
		gp.add(passwordField, 0, 3);

		gp.add(confirmPassLabel, 0, 4);
		gp.add(confirmPasswordField, 0, 5);

		gp.add(ageLabel, 0, 6);
		gp.add(ageSpinner, 0, 7);

		gp.add(genderLabel, 1, 0);
		gp.add(genderVBox, 1, 1);

		gp.add(nationalityLabel, 1, 2);
		gp.add(nationalityBox, 1, 3);

		gp.add(registerButton, 1, 4, 2, 1);

		menuBar.getMenus().add(menu);

		bp.setCenter(gp);

		SpinnerValueFactory<Integer> ageSpinnerVal = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
		ageSpinner.setValueFactory(ageSpinnerVal);
	}

	private void setStyle() {
		gp.setHgap(25);
		gp.setVgap(10);
		gp.setAlignment(Pos.CENTER);

		genderVBox.setSpacing(5);

		emailField.setTranslateY(-10);

		passwordLabel.setTranslateY(-20);
		passwordField.setTranslateY(-20);

		confirmPassLabel.setTranslateY(-25);
		confirmPasswordField.setTranslateY(-30);

		ageLabel.setTranslateY(-30);
		ageSpinner.setTranslateY(-30);

		registerButton.setPrefWidth(100);
	}

	private boolean validateInput() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText("Warning");

		String query = "SELECT * FROM msuser";
		connect.execQuery(query);

		if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()
				|| confirmPasswordField.getText().isEmpty()) {
			alert.setContentText("Email or Password must be Filled!");
			alert.show();
			return false;
		}

		if (!(emailField.getText().endsWith("@gmail.com"))) {
			alert.setContentText("Email must be ended with '@gmail.com'!");
			alert.show();
			return false;
		}

		if (passwordField.getText().length() < 6) {
			alert.setContentText("Password must contains more than 6 characters!");
			alert.show();
			return false;
		}
		
		if (!(confirmPasswordField.getText().equals(passwordField.getText()))) {
			alert.setContentText("Confirm Password must be the same as Password!");
			alert.show();
			return false;
		}

		if (!(ageSpinner.getValue() > 0)) {
			alert.setContentText("Age must be above 0!");
			alert.show();
			return false;
		}

		RadioButton selectedRadioButton = (RadioButton) genderGroup.getSelectedToggle();
		if (selectedRadioButton == null) {
			alert.setContentText("Gender must be selected!");
			alert.show();
			return false;
		}

		String selectedNationality = nationalityBox.getSelectionModel().getSelectedItem();
		if (selectedNationality == null || selectedNationality.isEmpty()) {
			alert.setContentText("Nationality must be selected!");
			alert.show();
			return false;
		}

		// validate user data not existed in database
		try {
			while (connect.rs.next()) {
				String email = connect.rs.getString("UserEmail");
				String password = connect.rs.getString("UserPassword");

				if (emailField.getText().equals(email)) {
					alert.setContentText("Email already been registered!");
					alert.show();
					return false;
				} else if (passwordField.getText().equals(password)) {
					alert.setContentText("Password already been registered!");
					alert.show();
					return false;
				}
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}

	private void addData() {
		int userIndex = getLastStoredIndexFromDatabase();
		userIndex++;
		String userId = String.format("UA%03d", userIndex);
        
		String email = emailField.getText();
		String password = passwordField.getText();
		
		RadioButton selectedRadioButton = (RadioButton) genderGroup.getSelectedToggle();
	    String gender = (selectedRadioButton != null) ? selectedRadioButton.getText() : "";
	    
		String nationality = nationalityBox.getSelectionModel().getSelectedItem();
		int age = ageSpinner.getValue();
		
		String role = "User";
		
		// add user to db
		String query = String.format("INSERT INTO msuser VALUES ('%s', '%s', '%s', %d, '%s', '%s', '%s')", userId, email, password, age, gender, nationality, role);
		connect.execUpdate(query);
	}
	
	private int getLastStoredIndexFromDatabase() {
		try {
	        String query = "SELECT MAX(UserID) FROM msuser";
	        connect.execQuery(query);

	        if (connect.rs.next()) {
	            String maxUserId = connect.rs.getString(1);
	            if (maxUserId != null) {
	                return Integer.parseInt(maxUserId.replaceAll("[^0-9]", ""));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return 0;
    }

	private void clearInput() {
		emailField.clear();
		passwordField.clear();
		confirmPasswordField.clear();
	}

}
