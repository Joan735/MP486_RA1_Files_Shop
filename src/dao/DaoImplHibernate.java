package dao;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import model.Employee;
import model.Product;
import model.ProductHistory;

public class DaoImplHibernate implements Dao {
	org.hibernate.SessionFactory sessionFactory;
	private Session session;

	public DaoImplHibernate() {
		if (session == null) {
			Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
			sessionFactory = configuration.buildSessionFactory();
			session = sessionFactory.openSession();
		}
	}

	@Override
	public void connect() {
		if (session == null || !session.isOpen()) {
			session = sessionFactory.openSession();
		}
	}

	@Override
	public ArrayList<Product> getInventory() {
		this.connect();
		ArrayList<Product> products = new ArrayList<>();
		try {
			products = new ArrayList<>(session.createQuery("FROM Product", Product.class).getResultList());
			for (Product product: products) {
				Product.updatePrices(product);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.disconnect();
		}
		return products;
	};

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		this.connect();
		try {
			for (Product product : inventory) {
				ProductHistory productHistory = new ProductHistory(product);
				session.save(productHistory);
			}
		} catch (Exception e) {
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
		try {
			Query<Employee> query = session
					.createQuery("from Employee e where e.employeeId = :id and e.password = :password", Employee.class);
			query.setParameter("id", employeeId);
			query.setParameter("password", password);

			employee = query.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	public void addProduct(Product product) {
		this.connect();
		try {
			session.save(product);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.disconnect();
		}
	}

	@Override
	public void updateProduct(Product product) {
		this.connect();
		try {
			session.getTransaction().begin();
			session.update(product);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.disconnect();
		}
	}

	@Override
	public void deleteProduct(int productId) {
		this.connect();
		try {
			session.getTransaction().begin();

			Query<?> query = session.createQuery("delete from Product where id = :id");
			query.setParameter("id", productId);
			query.executeUpdate();

			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
		} finally {
			this.disconnect();
		}
	}

	@Override
	public void disconnect() {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}
}
