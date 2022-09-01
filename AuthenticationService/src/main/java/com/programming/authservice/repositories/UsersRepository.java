package com.programming.authservice.repositories;

import com.programming.authservice.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    Users findUsersByEmail(String mail);
}
