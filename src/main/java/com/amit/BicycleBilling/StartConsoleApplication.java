package com.amit.BicycleBilling;

import com.amit.BicycleBilling.Presistance.model.Users;
import com.amit.BicycleBilling.Presistance.repo.UsersRepo;
import com.amit.BicycleBilling.admin.AdminRole;
import com.amit.BicycleBilling.common.Common;
import com.amit.BicycleBilling.staff.StaffRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class StartConsoleApplication implements CommandLineRunner {

    @Autowired
    private AdminRole adminRole;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private StaffRole staffRole;

    @Override
    public void run(String... args) throws Exception {
        Common.printLOG();
        usersRepo.save(new Users("admin", "admin", "admin"));
        usersRepo.save(new Users("staff", "staff", "staff"));

        Scanner scanner = new Scanner(System.in);
        String exit;
        do {
//            try {
                System.out.println("Who are you Admin or staff? Choose from following \n a) Admin\n b) Staff ");

                String checkRole = scanner.nextLine();
                if ("a".equalsIgnoreCase(checkRole)) {
                    adminRole.loginAdmin();
                } else if ("b".equalsIgnoreCase(checkRole)) {
                    staffRole.loginStaff();
                }
//            } catch (Exception exception) {
//                System.out.println(exception.getMessage());
//            }
            System.out.println("Do You want to exit Y and N");
            exit = scanner.nextLine();
        } while ("N".equalsIgnoreCase(exit));
    }
}