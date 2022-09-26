package com.resourcing.oath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserOathService {

	@Autowired
	private UserOathRepository repo;
	
	public void processOAuthPostLogin(String username) {
		UserOath existUser = repo.getUserByUsername(username);
		
		if (existUser == null) {
			UserOath newUser = new UserOath();
			newUser.setUsername(username);
			newUser.setProvider(Provider.GOOGLE);
			newUser.setEnabled(true);			
			repo.save(newUser);
			System.out.println("Created new user: " + username);
		}
		
	}
	
}
