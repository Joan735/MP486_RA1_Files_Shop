package dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import model.Employee;
import model.Product;

public class DaoImplObjectDB implements Dao {

	// Open a database connection
	// (create a new database if it doesn't exist yet):
	private EntityManagerFactory emf;
	private EntityManager em;

	@Override
	public void connect() {
		emf = Persistence.createEntityManagerFactory("objectdb:objects/employee.odb");
		em = emf.createEntityManager();
	}

	@Override
	public ArrayList<Product> getInventory() {
		return null;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		return false;
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		TypedQuery<Employee> query = em.createQuery(
				"SELECT e FROM Employee e WHERE e.employeeId = :employeeId AND e.password = :password", Employee.class);
		query.setParameter("employeeId", employeeId);
		query.setParameter("password", password);
		List<Employee> employees = query.getResultList();
		if (!employees.isEmpty()) {
			employee = employees.get(0);
		}
		return employee;
	}

	@Override
	public void addProduct(Product product) {
	}

	@Override
	public void updateProduct(Product product) {
	}

	@Override
	public void deleteProduct(int productId) {
	}

	@Override
	public void disconnect() {
		em.close();
		emf.close();
	}
}