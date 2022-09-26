package com.resourcing.oath;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserOathRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) 
			throws UsernameNotFoundException {
		UserOath user = userRepository.getUserByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user");
		}
		return null;
	//	return new MyUserDetails(user);
	}

}
