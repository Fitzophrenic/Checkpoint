package edu.csc3380.checkpoint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.csc3380.checkpoint.model.User;

public interface UserRepository extends JpaRepository<User, Long> { }
