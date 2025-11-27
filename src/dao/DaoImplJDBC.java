package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplJDBC implements Dao {
	Connection connection;

	@Override
	public void connect() {
		// Define connection parameters
		String url = "jdbc:mysql://localhost:3306/shop";
		String user = "root";
		String pass = "";
		try {
			this.connection = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public ArrayList<Product> getInventory() {
		this.connect();
		String query = "SELECT id, name, wholesalerPrice, available, stock FROM inventory";
		String queryMaxID = "SELECT MAX(id) AS max_id FROM inventory";
		ArrayList<Product> products = new ArrayList<>();
		try (Statement stmt = connection.createStatement()) {
			try (ResultSet maxRs = stmt.executeQuery(queryMaxID)) {
				if (maxRs.next()) {
					Product.setTotalProducts(maxRs.getInt("max_id"));
				}
			}
			try (ResultSet rs = stmt.executeQuery(query)) {
				while (rs.next()) {
					Amount wsPrice = new Amount(rs.getDouble("wholesalerPrice"));
					products.add(new Product(rs.getInt("id"), rs.getString("name"), wsPrice, rs.getBoolean("available"),
							rs.getInt("stock")));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.disconnect();
		}
		return products;
	};

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDate = date.format(myFormatObj);

		String query = "INSERT INTO historical_inventory (id_product, name, wholesalerPrice, available, stock, created_at) VALUES (?, ?, ?, ?, ?, ?)";
		this.connect();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			Statement stmt = connection.createStatement();
			for (Product product : inventory) {
				pstmt.setInt(1, product.getId());
				pstmt.setString(2, product.getName());
				pstmt.setDouble(3, product.getWholesalerPrice().getValue());
				pstmt.setBoolean(4, product.isAvailable());
				pstmt.setInt(5, product.getStock());
				pstmt.setString(6, formattedDate);
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			this.disconnect();
		}
		return true;
	};

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		String query = "select * from employee where employeeId= ? and password = ? ";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, employeeId);
			pstmt.setString(2, password);
			// System.out.println(ps.toString());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					employee = new Employee(rs.getInt(1), rs.getString(2), rs.getString(3));
				}
			}
		} catch (SQLException e) {
			// in case error in SQL
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	public void addProduct(Product product) {
		this.connect();
		String query = "INSERT INTO inventory (name, wholesalerPrice, available, stock) VALUES" + "(?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, product.getName());
			pstmt.setDouble(2, product.getWholesalerPrice().getValue());
			pstmt.setBoolean(3, product.isAvailable());
			pstmt.setInt(4, product.getStock());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.disconnect();
		}
	}

	@Override
	public void updateProduct(Product product) {
		this.connect();
		String query = "UPDATE inventory SET stock = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, product.getStock());
			pstmt.setInt(2, product.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.disconnect();
		}
	}

	@Override
	public void deleteProduct(int productId) {
		this.connect();
		String query = "DELETE FROM inventory WHERE id = ?;";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, productId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.disconnect();
		}
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
