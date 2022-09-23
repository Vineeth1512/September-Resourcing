/**
  *	
  *@author:Praveen Gudimalla
  *
  *
  **/

package com.resourcing.service;

import java.util.List;

import com.resourcing.beans.Candidate;
import com.resourcing.beans.Employee;

public interface CandidateService {

	void addCandidate(Candidate candidate);

	List<Candidate> getAllCandidates();

	void updateCandidate(Candidate candidate);

	void deleteCandidateById(int candidateId);

	Candidate getCandidate(String email, String password);

	Candidate findByEmail(String email);

	Candidate getCandidateById(int candidateId);

	List<Candidate> newCandidatesList();

	List<Candidate> getassignedCandidateList();

	//mail sending at the time of registration
	public String sendSimpleEmail(String toEmail, String body, String subject);

	// this is for list of candidates assigned to an employee
	List<Candidate> getCandidateListOfRecruiter(Employee employee);


}
