package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplFile implements Dao {
	
	@Override
	public void connect() {
	}

	@Override
	public ArrayList<Product> getInventory() {
		// locate file, path and name
		File f = new File(System.getProperty("user.dir") + File.separator + "files/inputInventory.txt");
		// ArrayList of products to return to the inventory
		ArrayList<Product> products = new ArrayList<>();

		try {
			// wrap in proper classes
			FileReader fr;
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			// read first line
			String line = br.readLine();

			// process and read next line until end of file
			while (line != null) {
				// split in sections
				String[] sections = line.split(";");

				String name = "";
				double wholesalerPrice = 0.0;
				int stock = 0;

				// read each sections
				for (int i = 0; i < sections.length; i++) {
					// split data in key(0) and value(1)
					String[] data = sections[i].split(":");

					switch (i) {
					case 0:
						// format product name
						name = data[1];
						break;

					case 1:
						// format price
						wholesalerPrice = Double.parseDouble(data[1]);
						break;

					case 2:
						// format stock
						stock = Integer.parseInt(data[1]);
						break;

					default:
						break;
					}
				}
				// add product to products
				products.add(new Product(name, new Amount(wholesalerPrice), true, stock));

				// read next line
				line = br.readLine();
			}
			fr.close();
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return products;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {

		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = date.format(myFormatObj);

		File f = new File(System.getProperty("user.dir") + File.separator + "file/inventory_" + formattedDate + ".txt");

		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			// wrap in proper classes
			FileWriter fw;
			fw = new FileWriter(f);
			PrintWriter pw = new PrintWriter(fw);

			// write line by line
			int counter = 0;
			for (Product product : inventory) {
				counter++;
				// format first line TO BE -> Product:Manzana;Wholesaler Price:10.00;Stock:10;
				StringBuilder firstLine = new StringBuilder(
						counter + ";Product:" + product.getName() + ";Stock:" + product.getStock() + ";");
				pw.write(firstLine.toString());
				fw.write("\n");
			}
			fw.write("Total number of products:" + inventory.size() + ";");
			// close files
			pw.close();
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	@Override
	public Employee getEmployee(int employeeId, String password) {
		return null;
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
	}
}