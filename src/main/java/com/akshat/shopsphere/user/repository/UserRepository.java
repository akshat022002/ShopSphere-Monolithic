package com.akshat.shopsphere.user.repository;

import com.akshat.shopsphere.user.entity.User;
import com.akshat.shopsphere.user.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByDeletedFalse(); // added in dashboard module to count no of users

    long countByRoleAndDeletedFalse(Role role); // added in dashboard module to count no of users

}
