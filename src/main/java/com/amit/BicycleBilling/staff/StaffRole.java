package com.amit.BicycleBilling.staff;

import com.amit.BicycleBilling.Presistance.model.Category;
import com.amit.BicycleBilling.Presistance.model.Product;
import com.amit.BicycleBilling.Presistance.model.ProductPrice;
import com.amit.BicycleBilling.Presistance.model.Users;
import com.amit.BicycleBilling.Presistance.repo.CategoryRepo;
import com.amit.BicycleBilling.Presistance.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
public class StaffRole {
    public static class CategoryProduct {
        String bicycle;
        List<String> category = new ArrayList<>();
        List<String> Product = new ArrayList<>();

        public String getBicycle() {
            return bicycle;
        }

        public void setBicycle(String bicycle) {
            this.bicycle = bicycle;
        }

        public List<String> getCategory() {
            return category;
        }

        public void setCategory(List<String> category) {
            this.category = category;
        }

        public List<String> getProduct() {
            return Product;
        }

        public void setProduct(List<String> product) {
            Product = product;
        }
    }

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    private static Scanner scanner = new Scanner(System.in);

    public void loginStaff() throws ParseException, IOException, ExecutionException, InterruptedException {
        System.out.println("Login with your Admin User name and password :");
        int tries = 3;
        while (tries != 0) {
            System.out.println("staff Username :");
            String adminUsername = scanner.nextLine();
            System.out.println("staff password :");
            String adminPassword = scanner.nextLine();
            if (checkCreditials(adminUsername, adminPassword)) {
                tries = 0;
                proceedWithStaff();

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

    private void proceedWithStaff() throws ExecutionException, InterruptedException, IOException {
        System.out.println("Choose from following option");
        System.out.println("a) Buy new Product b) upload the file");
        String staffchoice = scanner.nextLine();
        if ("a".equalsIgnoreCase(staffchoice)) {
            purchaseProduct();
        } else if ("b".equalsIgnoreCase(staffchoice)) {
            processFile();

        } else {
            System.out.println("Invalid Choice Login again");
            return;
        }

    }

    private void processFile() throws ExecutionException, InterruptedException, IOException {
        ExecutorService exec = Executors.newFixedThreadPool(10);
        List<CategoryProduct> items = getItems().values().stream().collect(Collectors.toList());
        int minItemsPerThread = items.size() / 10;
        //int maxItemsPerThread = minItemsPerThread + 1;
        //int threadsWithMaxItems = items.size() - 10 * minItemsPerThread;
        int start = 0;
        for (int i = 0; i < 10; i++) {

            int itemsCount = minItemsPerThread;
            int end = (start + itemsCount) > items.size() ? (start + itemsCount) : items.size();
            if(end - start == 0){
                end=start+1;
            }
            Runnable r = new Processor(items.subList(start, end),categoryRepo);
            Future future=  exec.submit(r);
            future.get();

            start = end;
            if (start >= items.size()) {
                break;
            }
        }

    }

    private LinkedHashMap<String, CategoryProduct> getItems() throws IOException {
        System.out.println("enter the file path");
        String path = scanner.nextLine();
        FileInputStream fis = new FileInputStream(path);
        Scanner sc = new Scanner(fis);
        LinkedHashMap<String, CategoryProduct> productList = new LinkedHashMap<>();
        String cycle = null;
        while (sc.hasNextLine()) {

            String[] strings = sc.nextLine().split(" ");
            if (strings.length == 1) {
                cycle = strings[0];
                continue;
            }
            if (!productList.containsKey(strings)) {
                productList.put(cycle, new CategoryProduct());
            }
            productList.get(cycle).category.add(strings[0]);
            productList.get(cycle).Product.add(strings[1]);
            productList.get(cycle).setBicycle(cycle);
        }
        sc.close();
        return productList;

    }

    private void purchaseProduct() {
        double sumPrice = 0;
        List<Category> categoryList = categoryRepo.findDistinctNameAndMandatory();
        while (!categoryList.isEmpty()) {

            System.out.println("Choose Category from following option");
            for (int i = 0; i < categoryList.size(); i++) {
                System.out.println(1 + i + ") " + categoryList.get(i).getName() + "  mandatory " + categoryList.get(i).isMandatory());
            }
            int catInt = Integer.parseInt(scanner.nextLine());
            System.out.println("Choose product from following option");
            List<Product> products = categoryList.get(catInt - 1).getProducts();

            for (int i = 0; i < products.size(); i++) {
                System.out.println(1 + i + ") " + products.get(i).getBrand());
            }
            int productInt = Integer.parseInt(scanner.nextLine());
            ProductPrice price = getPrice(products.get(productInt - 1).getPrices(), new Date(System.currentTimeMillis()));
            sumPrice = sumPrice + price.getPrice();
            categoryList.remove(catInt - 1);

        }

        System.out.println("total price : " + sumPrice);

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

    private boolean checkCreditials(String adminUsername, String adminPassword) {
        Optional<Users> users = usersRepo.findUsersByUserNameAndAndRole(adminUsername, "staff");
        if (users.isPresent() && users.get().getPassword().equals(adminPassword)) {
            return true;
        }
        return false;
    }
}
