package com.resourcing.oath;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserOathRepository extends CrudRepository<UserOath, Long> {

	@Query("SELECT u FROM UserOath u WHERE u.username = :username")
	public UserOath getUserByUsername(@Param("username") String username);
}
