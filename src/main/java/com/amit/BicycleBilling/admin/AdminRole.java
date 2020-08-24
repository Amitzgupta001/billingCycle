package com.amit.BicycleBilling.admin;

import com.amit.BicycleBilling.Presistance.model.Category;
import com.amit.BicycleBilling.Presistance.model.Product;
import com.amit.BicycleBilling.Presistance.model.ProductPrice;
import com.amit.BicycleBilling.Presistance.model.Users;
import com.amit.BicycleBilling.Presistance.repo.CategoryRepo;
import com.amit.BicycleBilling.Presistance.repo.ProductPriceRepo;
import com.amit.BicycleBilling.Presistance.repo.ProductRepo;
import com.amit.BicycleBilling.Presistance.repo.UsersRepo;
import com.amit.BicycleBilling.common.Common;
import com.amit.BicycleBilling.common.JSONBeutifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class AdminRole {
    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ProductPriceRepo productPriceRepo;


    private static Scanner scanner = new Scanner(System.in);


    public void loginAdmin() throws ParseException, IOException {
        System.out.println("Login with your Admin User name and password :");
        int tries = 3;
        while (tries != 0) {
            System.out.println("Admin Username :");
            String adminUsername = scanner.nextLine();
            System.out.println("Admin password :");
            String adminPassword = scanner.nextLine();
            if (checkCreditials(adminUsername, adminPassword)) {
                tries = 0;
                proceedWithAdmin();

            } else {
                tries--;
                if (tries == 0) {
                    System.out.println("No Tries Left returning to main page");
                    return;
                }
                System.out.println("Wrong username password try again " + tries + " tries left ");
            }
        }

    }


    private void proceedWithAdmin() throws ParseException, IOException {
        while (true) {
            System.out.println("choose from following option : ");
            System.out.println("\n a) Add new Parts \n b) change price of product \n c) show all existing parts \n d) logout");
            String adminChoice = scanner.nextLine();
            if ("a".equalsIgnoreCase(adminChoice)) {
                addParts();
            } else if ("b".equalsIgnoreCase(adminChoice)) {
                changePrice();
            } else if ("c".equalsIgnoreCase(adminChoice)) {
                try {
                    printCategory();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid Choice Login again");
                return;
            }

        }
    }

    @Transactional
    public void printCategory() throws IOException {
        JSONBeutifier.printJson(categoryRepo.findAll());
    }

    @Transactional
    public void changePrice() throws ParseException, IOException {
        System.out.println("choose Category from following : ");
        List<String> stringList = categoryRepo.findDistinctName();
        for (int i = 0; i < stringList.size(); i++) {
            System.out.println(1 + i + ") " + stringList.get(i));
        }
        int i = Integer.parseInt(scanner.nextLine());
        Category category = categoryRepo.findCategoryByName(stringList.get(i - 1));

        System.out.println("choose product from following : ");
        List<Product> products = category.getProducts();
        for (int j = 0; j < products.size(); j++) {
            System.out.println(1 + j + ") " + products.get(j));
        }
        int priceInt = Integer.parseInt(scanner.nextLine());

        System.out.println("All price for product brand: ");
        JSONBeutifier.printJson(products.get(priceInt - 1));

        List<ProductPrice> prices = products.get(priceInt - 1).getPrices();
        for (int j = 0; j < prices.size(); j++) {
            System.out.println(1 + j + ") " + prices.get(j));
        }

        System.out.println("Enter the start date of new price eg : 31/12/1998");
        String start = scanner.nextLine().trim();
        Date startDate = new Date(Common.getStartOfDay(new SimpleDateFormat("dd/MM/yyyy").parse(start)).getTime());

        System.out.println("Enter the end date of new price eg : 31/12/1998");
        String end = scanner.nextLine().trim();
        Date endDate = new Date(Common.getEndOfDay(new SimpleDateFormat("dd/MM/yyyy").parse(end)).getTime());

        System.out.println("Enter  new price for product");
        double price = Double.parseDouble(scanner.nextLine().trim());

        ProductPrice price1 = getPrice(products.get(priceInt - 1).getPrices(), startDate);
//                productPriceRepo
//                .findByProductProductIdAndStartOfPriceLessThanAndEndOfPriceGreaterThan(products.get(priceInt - 1).getProductId(), startDate, startDate).get();

        ProductPrice price2 = getPrice(products.get(priceInt - 1).getPrices(), endDate);//
        // productPriceRepo.findByProductProductIdAndStartOfPriceLessThanAndEndOfPriceGreaterThan(products.get(priceInt - 1).getProductId(), endDate, endDate).get();

        ProductPrice productPrice = new ProductPrice();
        productPrice.setPrice(price);
        productPrice.setStartOfPrice(startDate);
        productPrice.setEndOfPrice(endDate);
        productPrice.setProduct(products.get(priceInt - 1));
        if (price1.getPriceId() == price2.getPriceId() && price1 == null) {
        } else if (price1.getPriceId() == price2.getPriceId()) {
            price1.setEndOfPrice(new Date(startDate.getTime() - 1));
            productPriceRepo.save(price1);
        } else {
            price1.setEndOfPrice(new Date(startDate.getTime() - 1));
            price2.setEndOfPrice(new Date(endDate.getTime() + 1));
            productPriceRepo.save(price1);
            productPriceRepo.save(price2);
        }
        products.get(priceInt - 1).getPrices().add(productPrice);
        productRepo.save(products.get(priceInt - 1));


    }

    private ProductPrice getPrice(List<ProductPrice> prices, Date endDate) {

        for (ProductPrice productPrice :
                prices) {

            if ((productPrice.getStartOfPrice().before(endDate)) && productPrice.getEndOfPrice().after(endDate)) {
                return productPrice;
            }
        }
        return null;
    }

    private void addParts() throws ParseException {
        System.out.println("choose from following option : ");
        System.out.println("\n a) Add new Category of parts \n b) Add new product to Category");

        String addChoice = scanner.nextLine();
        if ("a".equalsIgnoreCase(addChoice)) {
            addCategory();
        } else if ("b".equalsIgnoreCase(addChoice)) {
            addProduct();
        } else {
            System.out.println("Invalid Choice Login again");
        }

    }

    private void addProduct() throws ParseException {
        System.out.println("Choose the category you want to add new product\n");
        List<String> stringList = categoryRepo.findDistinctName();
        for (int i = 0; i < stringList.size(); i++) {
            System.out.println(1 + i + ") " + stringList.get(i));
        }
        int i = Integer.parseInt(scanner.nextLine());
        Category category = categoryRepo.findCategoryByName(stringList.get(i - 1));

        System.out.println("Enter brand Name");
        String name = scanner.nextLine();
        System.out.println("Enter price of product ");
        double price = Double.parseDouble(scanner.nextLine());
        Date start = new Date(Common.getStartOfDay(new Date(System.currentTimeMillis())).getTime());
        String end1 = "31/12/2500";
        Date end = new Date(Common.getEndOfDay(new SimpleDateFormat("dd/MM/yyyy").parse(end1)).getTime());
        Product product = presistProduct(0, name, price, category, start, end);
        category.getProducts().add(product);

        categoryRepo.save(category);

    }

    @Transactional
    public Product presistProduct(int i, String name, double price, Category category, Date start, Date end) {
        Product product = new Product();
        if (i != 0) {
            product.setProductId(i);
        }
        product.setBrand(name);

        product.setCategory(category);
        ProductPrice productPrice = new ProductPrice();
        List<ProductPrice> prices = new ArrayList<>();
        productPrice.setPrice(price);
        productPrice.setStartOfPrice(start);
        productPrice.setEndOfPrice(end);
        prices.add(productPrice);
        product.setPrices(prices);
        return productRepo.save(product);

    }

    private void addCategory() {
        System.out.println("Enter new Category Name");
        String name = scanner.nextLine();
        System.out.println("Enter new Category Description");
        String description = scanner.nextLine();
        System.out.println("It is mandatory to build Integrated Product y/n ");
        String mandatory = scanner.nextLine();
        boolean mand = "y".equalsIgnoreCase(mandatory) ? true : false;
        Category category = new Category(name, description, mand);
        categoryRepo.save(category);
    }

    private boolean checkCreditials(String adminUsername, String adminPassword) {
        Optional<Users> users = usersRepo.findUsersByUserNameAndAndRole(adminUsername, "admin");
        if (users.isPresent() && users.get().getPassword().equals(adminPassword)) {
            return true;
        }
        return false;
    }
}
