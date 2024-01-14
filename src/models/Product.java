package models;

public class Product {
	private String productId;
	private String productName;
	private String productBrand;
	private Integer productStock;
	private Integer productPrice;

	public Product(String productId, String productName, String productBrand, Integer productStock,
			Integer productPrice) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productBrand = productBrand;
		this.productStock = productStock;
		this.productPrice = productPrice;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	public Integer getProductStock() {
		return productStock;
	}

	public void setProductStock(Integer productStock) {
		this.productStock = productStock;
	}

	public Integer getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}

}
