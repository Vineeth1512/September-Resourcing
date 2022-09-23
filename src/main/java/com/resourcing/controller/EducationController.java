/**
  *	
  *@author:Praveen Gudimalla
  *
  *
  **/

package com.resourcing.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.resourcing.beans.Candidate;
import com.resourcing.beans.Education;
import com.resourcing.service.CandidateService;
import com.resourcing.service.EducationService;

@Controller
@RequestMapping("/education")
public class EducationController {

	static Logger LOGGER = Logger.getLogger(EducationController.class);

	@Autowired
	private EducationService educationService;

	@Autowired
	private CandidateService candidateService;

	// get the list of all educations of one candidate
	@GetMapping("/educationList/{candidateId}")
	public String getAllEducationsOfOneCandidate(Model model,@PathVariable int candidateId, @RequestParam(required = false) String message) {
		Candidate candidate = candidateService.getCandidateById(candidateId);
		List<Education> educationList = educationService.getAllEducations(candidate);
		List<Education> sortedList = educationList.stream().sorted(Comparator.comparing(Education::getCourse)).collect(Collectors.toList());
		model.addAttribute("educationList", sortedList);
		model.addAttribute("candidate", candidate);
		model.addAttribute("TableName", "Education details");
		 if(!StringUtils.isEmpty(message)) {
	            model.addAttribute("message", message);
	        }
		return "education_list";
	}

	// Insert new education record
	@RequestMapping(value = "/saveEducation", method = RequestMethod.POST)
	public String saveEducation(@ModelAttribute("education") Education education, RedirectAttributes redirectAttributes,
			Model model, @ModelAttribute("candidate") Candidate candidate) {
		LOGGER.debug("educationform" + education.getCourse());
		education.setIsActive('Y');
		education.setCreatedDate(LocalDateTime.now());
		education.setCourse(education.getCourse().toUpperCase());
		educationService.addEducation(education);
		redirectAttributes.addFlashAttribute("message", "   One education record is added!!");
		return "redirect:/education/educationList/" + education.getCandidate().getCandidateId();
	}

	// add candidates to the list
	@GetMapping("/addEducation/{candidateId}")
	public String newEducation(Model model, @PathVariable("candidateId") int candidateId, Education education) {
		LOGGER.debug("add Education details method::::" + candidateId);
		model.addAttribute("education", education);
		Candidate candidate = candidateService.getCandidateById(candidateId);
		model.addAttribute("candidate", candidate);
		return "education";
	}

	@GetMapping("/updateEducation/{candidateId}/{educationId}")
	public String updateEducation(Model model, @PathVariable int candidateId, @PathVariable int educationId,
			@ModelAttribute("education") Education education, HttpSession session) {
		Education educationExist = educationService.getEducationById(education.getEducationId());
		LOGGER.debug("createddate:::::" + educationExist.getCreatedDate());
		model.addAttribute("educationExist", educationExist);
		Candidate candiidte = candidateService.getCandidateById(candidateId);
		model.addAttribute("candidate", candiidte);
		return "update_education";
	}

	// updating the education details
	@RequestMapping(value = "/updateEducationDetails", method = RequestMethod.POST)
	public String updateEducationDetails(@ModelAttribute("educationExist") Education education, Model model,RedirectAttributes redirectAttributes,
			@ModelAttribute("candidate") Candidate candidate) {
		Education existEducation = educationService.getEducationById(education.getEducationId());
		LOGGER.debug("created date new::::" + existEducation.getCreatedDate());
		education.setCreatedDate(existEducation.getCreatedDate());
		education.setUpdatedDate(LocalDateTime.now());
		education.setIsActive('Y');
		education.setCourse(education.getCourse().toUpperCase());
		educationService.updateEducation(education);
		redirectAttributes.addFlashAttribute("message", "   One education record is updated!!");
		return "redirect:/education/educationList/" + education.getCandidate().getCandidateId();
	}

	// Delete user record http://localhost:8080/emp/deleteEmp/2
	@GetMapping("/deleteEducation/{candidateId}/{educationId}")
	public String deleteEducation(Model model,RedirectAttributes redirectAttributes,
			@PathVariable(value = "candidateId") int candidateId,
			@PathVariable(value = "educationId") int educationId) {
		educationService.deleteEducationById(educationId);
		redirectAttributes.addFlashAttribute("message", "   One education record is deleted!!");
		return "redirect:/education/educationList/" + candidateId;
	}

}
