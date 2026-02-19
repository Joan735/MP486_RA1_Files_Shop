package dao;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplMongoDB implements Dao {
	MongoCollection<Document> collection;
	ObjectId id;

	final String URI = "mongodb://localhost:27017";
	MongoClientURI mongoClientURI;
	MongoClient mongoClient;
	MongoDatabase mongoDatabase;

	final String COLLECTION_HISTORICAL_INVENTORY = "historical_inventory";
	final String COLLECTION_INVENTORY = "inventory";
	final String COLLECTION_USERS = "users";

	public DaoImplMongoDB() {
		if (mongoClient == null) {
			mongoClientURI = new MongoClientURI(URI);
			mongoClient = new MongoClient(mongoClientURI);
		}
	}

	@Override
	public void connect() {
		if (mongoDatabase == null) {
			mongoDatabase = mongoClient.getDatabase("shop");
		}
	}

	@Override
	public ArrayList<Product> getInventory() {
		this.connect();
		collection = mongoDatabase.getCollection(COLLECTION_INVENTORY);
		ArrayList<Product> products = new ArrayList<>();

		Iterable<Document> documents = collection.find();

		Product newProduct = new Product();
		for (Document doc : documents) {
			Document priceDoc = doc.get("wholesalerPrice", Document.class);
			Amount wholesalerPrice = new Amount(priceDoc.getDouble("value"), priceDoc.getString("currency"));

			newProduct = new Product(doc.getInteger("id").intValue(), doc.getString("name"), wholesalerPrice,
					doc.getBoolean("available"), doc.getInteger("stock").intValue());
			products.add(newProduct);
		}

		Document maxIdDocument = collection.find().sort(Sorts.descending("id")).limit(1).first();

		if (maxIdDocument != null) {
			int maxId = maxIdDocument.getInteger("id").intValue();
			Product.setTotalProducts(maxId);
		}
		return products;
	};

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		try {
	        this.connect();
	        collection = mongoDatabase.getCollection("");
	        for (Product product : inventory) {
	            Document wholesalerPriceDoc = new Document("value", product.getWholesalerPrice().getValue())
	                    .append("currency", product.getWholesalerPrice().getCurrency());
	            Document document = new Document("_id", new ObjectId())
	                    .append("id", product.getId())
	                    .append("name", product.getName())
	                    .append("wholesalerPrice", wholesalerPriceDoc)
	                    .append("available", product.isAvailable())
	                    .append("stock", product.getStock())
	                    .append("created_at", new Date());

	            collection.insertOne(document);
	        }
	        return true;
	    } catch (Exception e) {
	        System.err.println("Error al insertar inventario: " + e.getMessage());
	        return false;
	    }
	};

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		this.connect();
		collection = mongoDatabase.getCollection(COLLECTION_USERS);

		Document document = collection.find(combine(eq("employeeId", employeeId), eq("password", password))).first();

		if (document != null) {
			employee = new Employee(document.getInteger("employeeId").intValue(), document.getString("name"),
					document.getString("password"));
		}
		return employee;
	}

	@Override
	public void addProduct(Product product) {
		this.connect();
		collection = mongoDatabase.getCollection(COLLECTION_INVENTORY);

		Document wholesalerPriceDoc = new Document("value", product.getWholesalerPrice().getValue()).append("currency",
				product.getWholesalerPrice().getCurrency());

		Document document = new Document("_id", new ObjectId()).append("name", product.getName())
				.append("wholesalerPrice", wholesalerPriceDoc).append("available", product.isAvailable())
				.append("stock", product.getStock()).append("id", product.getId());
		collection.insertOne(document);
	}

	@Override
	public void updateProduct(Product product) {
		this.connect();
		collection = mongoDatabase.getCollection(COLLECTION_INVENTORY);
		collection.updateOne(eq("id", product.getId()), set("stock", product.getStock()));
	}

	@Override
	public void deleteProduct(int productId) {
		this.connect();
		collection = mongoDatabase.getCollection(COLLECTION_INVENTORY);
		collection.deleteMany(eq("id", productId));
	}

	@Override
	public void disconnect() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}
}
