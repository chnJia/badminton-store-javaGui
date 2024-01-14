package models;

public class User {
	private String userId;
	private String email;
	private String password;
	private Integer age;
	private String gender;
	private String nationality;
	private String userRole;

	public User(String userId, String email, String password, Integer age, String gender, String nationality,
			String userRole) {
		super();
		this.userId = userId;
		this.email = email;
		this.password = password;
		this.age = age;
		this.gender = gender;
		this.nationality = nationality;
		this.userRole = userRole;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

}
