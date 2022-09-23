/* ======================
 * Author : Nikhil 
 * ======================*/

package com.resourcing.service;

import java.util.List;

import com.resourcing.beans.Client;
import com.resourcing.beans.User;

public interface ClientService {
	Client findByEmail(String email);

	List<Client> getAllClients();
	
	List<Client> clientListByUser(User user);

	void deleteClientById(int id);

	void saveClient(Client client);

	Client getClientById(int id);

	Client updateClient(Client client);

	void addClient(Client client);

	Client findByUserNameIgnoreCaseAndPassword(String email, String password);

}
