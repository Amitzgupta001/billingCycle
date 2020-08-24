package com.amit.BicycleBilling.Presistance.repo;

import com.amit.BicycleBilling.Presistance.model.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends CrudRepository<Users, String> {
    Optional<Users> findUsersByUserNameAndAndRole(String userName, String role);
}
