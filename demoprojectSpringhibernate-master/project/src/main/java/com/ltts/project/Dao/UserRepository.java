package com.ltts.project.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ltts.project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u FROM User u WHERE u.email = ?1")
	public User findByEmail(String email);
	
	 @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
	    public User findByVerificationCode(String code);
}
