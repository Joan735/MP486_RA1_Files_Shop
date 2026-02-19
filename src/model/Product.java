package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "inventory")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = true)
	private int id;
	@Column
	private String name;
	@Transient
	private Amount publicPrice;
	@Transient
	private Amount wholesalerPrice;
	@Column
	private double price;
	@Column
	private boolean available;
	@Column
	private int stock;
	@Transient
	private static int totalProducts;
	@Transient
	public final static double EXPIRATION_RATE = 0.60;

	public Product() {
	}

	public Product(String name, Amount wholesalerPrice, boolean available, int stock) {
		super();
		totalProducts++;
		this.id = totalProducts;
		this.name = name;
		this.wholesalerPrice = wholesalerPrice;
		this.publicPrice = new Amount(wholesalerPrice.getValue() * 2);
		this.price = wholesalerPrice.getValue();
		this.available = available;
		this.stock = stock;
	}
	
	public Product(int id, String name, Amount wholesalerPrice, boolean available, int stock) {
		super();
		this.id = id;
		this.name = name;
		this.wholesalerPrice = wholesalerPrice;
		this.publicPrice = new Amount(wholesalerPrice.getValue() * 2);
		this.price = wholesalerPrice.getValue();
		this.available = available;
		this.stock = stock;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Amount getPublicPrice() {
		return publicPrice;
	}

	public void setPublicPrice(Amount publicPrice) {
		this.publicPrice = publicPrice;
	}

	public Amount getWholesalerPrice() {
		return wholesalerPrice;
	}

	public void setWholesalerPrice(Amount wholesalerPrice) {
		this.wholesalerPrice = wholesalerPrice;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public static int getTotalProducts() {
		return totalProducts;
	}

	public static void setTotalProducts(int totalProducts) {
		Product.totalProducts = totalProducts;
	}

	public void expire() {
		this.publicPrice.setValue(this.getPublicPrice().getValue() * EXPIRATION_RATE);
		;
	}
	
	public static void updatePrices(Product product) {
		product.setWholesalerPrice(new Amount(product.getPrice()));
		product.setPublicPrice(new Amount(product.getPrice() * 2));
	}

	@Override
	public String toString() {
		return "Product [id= " + id + " name=" + name + ", publicPrice=" + publicPrice + ", wholesalerPrice="
				+ wholesalerPrice + ", price" + price + ", available=" + available + ", stock=" + stock + "]";
	}
}
