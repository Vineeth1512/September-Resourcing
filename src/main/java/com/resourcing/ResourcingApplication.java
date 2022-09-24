package com.resourcing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.resourcing.beans.Branch;
import com.resourcing.beans.Candidate;
import com.resourcing.beans.Client;
import com.resourcing.beans.InterviewPanel;
import com.resourcing.beans.JobDescription;
import com.resourcing.service.BranchService;
import com.resourcing.service.CandidateService;
import com.resourcing.service.ClientService;
import com.resourcing.service.JobDescriptionService;

@SpringBootApplication
@RequestMapping("/")
public class ResourcingApplication extends SpringBootServletInitializer {

	@Autowired
	private JobDescriptionService jobDescriptionService;
	
	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private BranchService branchService;
	
	//this is to use external tomcat
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ResourcingApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ResourcingApplication.class, args);
	}
	
	@GetMapping("/test")
	public String test() {
		return "a";
	}
	
	
	//this is for hitting default page i.e //localhost:8080
	@GetMapping("")
	public String homePage(InterviewPanel interviewer, Model model, JobDescription jobDescription) {
		model.addAttribute("newInterviewerDetails", interviewer);
		List<JobDescription> jdList = jobDescriptionService.getAllJobDescriptions();
		List<Candidate> candidateList = candidateService.getAllCandidates();
		List<Client> clientList = clientService.getAllClients();
		List<Branch> objBranch = branchService.getAllBranchs();
		model.addAttribute("branchList", objBranch.size());
		model.addAttribute("jdList", jdList.size());
		model.addAttribute("candidateList", candidateList.size());
		model.addAttribute("clientList", clientList.size());
		model.addAttribute("objJd", jobDescription);
		return "homepage";
	}

	
}
