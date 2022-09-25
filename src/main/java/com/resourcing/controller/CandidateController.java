

/**
  *	
  *@author:Praveen Gudimalla
  *
  *
  **/

package com.resourcing.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.resourcing.beans.Candidate;
import com.resourcing.beans.CandidateJdAssociation;
import com.resourcing.beans.Doc;
import com.resourcing.beans.JobDescription;
import com.resourcing.beans.Schedule;
import com.resourcing.repository.CandidateRepository;
import com.resourcing.service.CandidateJdAssociationService;
import com.resourcing.service.CandidateService;
import com.resourcing.service.DocStorageService;
import com.resourcing.service.EducationService;
import com.resourcing.service.EmailSenderService;
import com.resourcing.service.JobDescriptionService;
import com.resourcing.service.QRCodeGeneratorService;
import com.resourcing.service.ScheduleService;
import com.resourcing.utilities.Utilities;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

	static Logger LOGGER = Logger.getLogger(CandidateController.class);
	
	private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

	@Autowired
	CandidateService candidateService;

	@Autowired
	EducationService educationService;

	@Autowired
	CandidateRepository candidateRepository;

	@Autowired
	JobDescriptionService jobDescriptionService;

	@Autowired
	EmailSenderService emailSenderService;

	@Autowired
	private DocStorageService docStorageService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private CandidateJdAssociationService candidateJdAssociationService;
	
	@Autowired
	private QRCodeGeneratorService qrCodeGeneratorService;

	// Insert candidate record
	@GetMapping(value = "/saveCandidate")
	public String saveCandidate(Model model, @ModelAttribute("candidate") Candidate candidate,
			RedirectAttributes redirectAttributes) {
		Candidate candidateObj = candidateService.findByEmail(candidate.getEmail());
		if (candidateObj == null) {
			String strEncPassword = Utilities.getEncryptSecurePassword(candidate.getPassword(), "GLAM");
			candidate.setPassword(strEncPassword); // while saving the candidate set this to encrypt pwd.
			String qrCodeBase64 = qrCodeGeneratorService.generateQrCodeBase64(candidate.getEmail(), 500, 500);
			candidate.setQrCode(qrCodeBase64);
			candidateService.addCandidate(candidate);
			candidate.setCreatedDate(LocalDateTime.now());
			LOGGER.debug("created date:::" + candidate.getCreatedDate());
			LOGGER.debug("created_by:::" + candidate.getCandidateId());
			// after adding the account we get the candidateId that's why we are updating
			// the created by here..
			//candidate.setCreatedBy(candidate.getCandidateId());
			//candidate.setIsActive('Y');
			candidateService.updateCandidate(candidate);
			LOGGER.debug("name of the first:::" + candidate.getFirstName());
			redirectAttributes.addFlashAttribute("message", "registered successfully!!");
			redirectAttributes.addAttribute("candidate", candidate);
			return "redirect:/candidate/login";
		} else {
			redirectAttributes.addFlashAttribute("message", candidate.getEmail() + "  is already exists!! please try with another mail!!");
			return "redirect:/candidate";
		}
	}

	// candidate form will appear to enter his/her details
	@GetMapping("")
	public String newCandidate(Model model, HttpServletRequest request, Candidate candidate,
			@RequestParam(required = false) String message,
			final HttpServletResponse response, HttpSession session) {
		LOGGER.debug("add candidate::::");
		final StringBuffer url1 = request.getRequestURL();
		HttpSession sessionOne = request.getSession();
		sessionOne.setAttribute("url", url1);
		LOGGER.debug("url1::::::" + url1);
		model.addAttribute("candidate", candidate);
		//message for email already exist!!
		if(!StringUtils.isEmpty(message)) {
			model.addAttribute("message", message);
		}
		return "candidate_pages_register";
	}

	// verification of mail if it is valid or not
	@RequestMapping("/verification")
	public String validatemail(Model model, Candidate candidate, final HttpServletRequest request,RedirectAttributes redirectAttributes,
			String contentToGenerate, final HttpServletResponse response, HttpSession session) throws MessagingException {		
		Candidate candidateObj = candidateService.findByEmail(candidate.getEmail());
		if (candidateObj != null) {
			LOGGER.debug("verififcation:::");
			redirectAttributes.addFlashAttribute("message", candidate.getEmail() + "  is already exists!! please try with another mail!!");
			return "redirect:/candidate";
		} else {
			System.out.println("else:::::");
		//	String qrCodeBase64 = qrCodeGeneratorService.generateQrCodeBase64(candidate.getEmail(), WIDTH, HEIGHT);
			System.out.println("else2::::");
		//	candidate.setQrCode(qrCodeBase64);
			System.out.println("else3:::");
			final StringBuffer uri = request.getRequestURL();
			LOGGER.debug(uri);
			model.addAttribute("Emailid", candidate.getEmail());
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			LOGGER.debug(":::::::::::::::::2" + candidate.getEmail());
			mailMessage.setTo(candidate.getEmail());
			LOGGER.debug("::::::::::::::::: 3" + candidate.getEmail());
			mailMessage.setSubject("Complete Registration!");
			LOGGER.debug("::::::::::::::::: 4" + candidate.getEmail());
			mailMessage.setFrom("resourcingproject360@gmail.com");
			LOGGER.debug("::::::::::::::::: 5" + candidate.getEmail());
			mailMessage.setText("To confirm your account, please click here : " + session.getAttribute("url")
					+ "/saveCandidate?email=" + candidate.getEmail() + "&password=" + candidate.getPassword());
			LOGGER.debug("::::::::::::::::: 6" + session.getAttribute("url") + "saveCandidate?email="
					+ candidate.getEmail() + "&password=" + candidate.getPassword());
			candidateService.sendSimpleEmail(candidate.getEmail(),
					"To confirm your account, please click here : " + session.getAttribute("url")
							+ "/saveCandidate?email=" + candidate.getEmail() + "&password=" + candidate.getPassword(),
					"Mailid Verification");
			LOGGER.debug("::::::::::::::::: 7" + candidate.getEmail());
			model.addAttribute("Emailid", candidate.getEmail());
			LOGGER.debug("::::::::::::::::: 8" + candidate.getEmail());
			return "successfulRegistration";
		}
	}

	@GetMapping("/dashboardSettings/{id}")
	public String dashboardSettingsPage(Model model, @PathVariable int id, @RequestParam(required = false) String message) {
		Candidate candidate = candidateService.getCandidateById(id);
		model.addAttribute("candidate", candidate);
		if(!StringUtils.isEmpty(message)) {
			model.addAttribute(message, "message");
		}
		return "candidate_dashboard_settings";
	}

	@RequestMapping(value = "/updateCandidateDetails/{id}", method = RequestMethod.POST)
	public String updateCandidateDetails(Model model, @ModelAttribute("candidate") Candidate candidateObj,RedirectAttributes redirectAttributes,
			@RequestParam("file") MultipartFile file, @PathVariable int id) throws IOException {
		Candidate existCandidate = candidateService.getCandidateById(id);
		LOGGER.debug("is active status:::" + existCandidate.getIsActive());
		candidateObj.setUpdatedBy(existCandidate.getCandidateId());
		candidateObj.setUpdatedDate(LocalDateTime.now());
		candidateObj.setCreatedDate(existCandidate.getCreatedDate());
		candidateObj.setCreatedBy(existCandidate.getCandidateId());
		candidateObj.setEmployeeObj(existCandidate.getEmployeeObj());
		candidateObj.setQrCode(existCandidate.getQrCode());
		LocalDate currentDate = LocalDate.now();
		Period period = Period.between(candidateObj.getDateOfBirth(), currentDate);
		candidateObj.setAge(period.getYears() + " years " + period.getMonths() + " months " + period.getDays() + " days");
		if (file.getOriginalFilename() == "") {
			candidateObj.setImage(existCandidate.getImage());
		} else {
			candidateObj.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
		}
		candidateService.addCandidate(candidateObj);
		redirectAttributes.addFlashAttribute("message", "candidate details are updates");
		return "redirect:/candidate/dashboardSettings/" + id;
	}


	@GetMapping("/deleteCandidate/{id}")
	public String deleteCandidate(Model model, @PathVariable int id, HttpSession session) {
		LOGGER.debug("inside deleteCandidate id:::" + id);
		Candidate candidate = candidateService.getCandidateById(id);
		candidate.setIsActive('N');
		candidateService.updateCandidate(candidate);
		LOGGER.debug("candidated updated successfully");
		LOGGER.debug("id value is:::" + id);
		return "redirect:/candidate";
	}
				
	// candidate can login here
	@GetMapping("/login")
	public String login(Model model, @RequestParam(value="candidate" , required = false) Candidate candidate,Candidate candidateObj,
			@RequestParam(required = false) String message) {
		//this is for passing a message purpouse, after register or invalid creadentials
		model.addAttribute("candidate", candidate);
		//this is for login first time
		model.addAttribute("candidate", candidateObj);
		if(!StringUtils.isEmpty(message)) {
		model.addAttribute("message", message);
		}
		return "candidate_pages_login";
	}

	@RequestMapping(value = "/checkCredentials", method = RequestMethod.POST)
	public String checkCredentials(Model model, @ModelAttribute("candidate") Candidate candidate,RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		LOGGER.debug("getCaptcha:::::" + candidate.getCaptcha());
		LOGGER.debug("getUserCaptcha::::::" + candidate.getUserCaptcha());
		if (!candidate.getCaptcha().equals(candidate.getUserCaptcha())) {
			redirectAttributes.addFlashAttribute("message", "Please enter valid Captcha");
			redirectAttributes.addFlashAttribute("candidate", candidate);
			return "redirect:/candidate/login";
		}
		String strEncPassword = Utilities.getEncryptSecurePassword(candidate.getPassword(), "GLAM");
		Candidate objCandidate = candidateService.getCandidate(candidate.getEmail(), strEncPassword);
		LOGGER.debug("email:::::" + candidate.getEmail());
		LOGGER.debug("password:::::" + strEncPassword);
		if (objCandidate != null) {
			if (objCandidate.getIsActive() == 'Y') {
				LOGGER.debug("login successfully");
				LOGGER.debug("credential login user::::" + objCandidate.getEmail());
				HttpSession session = request.getSession();
				LOGGER.debug("session created:::::");
				session.setAttribute("candidateObj", objCandidate);
				LOGGER.debug("session created:::::::::::::::::::::::::::::::::");
				LOGGER.debug("session working:::::");
				LOGGER.debug("objschedule::::::");
				List<CandidateJdAssociation> list = candidateJdAssociationService.getAllCandidateJdAssociations();
				List<CandidateJdAssociation> candidateJobList = list.stream().filter(obj -> obj.getCandidate().getCandidateId()== objCandidate.getCandidateId()).collect(Collectors.toList());
				List<CandidateJdAssociation> selected = candidateJobList.stream().filter(obj ->obj.getStatus().equals("SELECTED")).collect(Collectors.toList());
				List<CandidateJdAssociation> rejected = candidateJobList.stream().filter(obj ->obj.getStatus().equals("REJECTED")).collect(Collectors.toList());
				List<CandidateJdAssociation> pending = candidateJobList.stream().filter(obj ->obj.getStatus().equals("SCHEDULED")).collect(Collectors.toList());
				List<CandidateJdAssociation> scheduleJobs = candidateJobList.stream().filter(obj -> obj.getStatus().equals("SCHEDULED")).collect(Collectors.toList());
				List<CandidateJdAssociation> notScheduleJobs = candidateJobList.stream().filter(obj -> obj.getStatus().equals("NOT SCHEDULED")).collect(Collectors.toList());
				List<Schedule> allSchedules = scheduleService.getAllSchedules();
				List<Schedule> mySchedules = allSchedules.stream().filter(obj -> obj.getCandidate().getCandidateId()==objCandidate.getCandidateId()).collect(Collectors.toList());
				List<Schedule> todaySchedule= mySchedules.stream().filter(today ->today.getDate().isEqual(LocalDate.now())).collect(Collectors.toList());
				List<Schedule> upcomingSchedule= mySchedules.stream().filter(upcoming -> upcoming.getDate().isAfter(LocalDate.now())).collect(Collectors.toList());
				model.addAttribute("todaySchedule", todaySchedule.size());
				model.addAttribute("upcomingSchedule", upcomingSchedule.size());
				model.addAttribute("selectedJobs", selected.size());
				model.addAttribute("pendingJobs", pending.size());
				model.addAttribute("rejectedJobs", rejected.size());
				model.addAttribute("appliedJobs", candidateJobList.size());
				model.addAttribute("notScheduledJobs", notScheduleJobs.size());
				model.addAttribute("scheduledJobs", scheduleJobs.size());
				model.addAttribute("candidate", objCandidate);
				return "candidate_dashboard";
			} else {   
				redirectAttributes.addFlashAttribute("message", "your account is not here.. pls register or login correctly..");
				return "redirect:/candidate/login";
			}
		} else {
			redirectAttributes.addFlashAttribute("message", "invalid Credentials");
			model.addAttribute("candidate", candidate);
			return "redirect:/candidate/login";
		}
	}

	@GetMapping("/dashboard/{id}")
	public String dashboard(Model model, @PathVariable int id) {
		Candidate candidate = candidateService.getCandidateById(id);
		List<CandidateJdAssociation> list = candidateJdAssociationService.getAllCandidateJdAssociations();
		List<CandidateJdAssociation> candidateJobList = list.stream().filter(obj -> obj.getCandidate().getCandidateId()== candidate.getCandidateId()).collect(Collectors.toList());
		List<CandidateJdAssociation> selected = candidateJobList.stream().filter(obj ->obj.getStatus().equals("SELECTED")).collect(Collectors.toList());
		List<CandidateJdAssociation> rejected = candidateJobList.stream().filter(obj ->obj.getStatus().equals("REJECTED")).collect(Collectors.toList());
		List<CandidateJdAssociation> pending = candidateJobList.stream().filter(obj ->obj.getStatus().equals("SCHEDULED")).collect(Collectors.toList());
		List<CandidateJdAssociation> scheduleJobs = candidateJobList.stream().filter(obj -> obj.getStatus().equals("SCHEDULED")).collect(Collectors.toList());
		List<CandidateJdAssociation> notScheduleJobs = candidateJobList.stream().filter(obj -> obj.getStatus().equals("NOT SCHEDULED")).collect(Collectors.toList());
		List<Schedule> allSchedules = scheduleService.getAllSchedules();
		List<Schedule> mySchedules = allSchedules.stream().filter(obj -> obj.getCandidate().getCandidateId()==id).collect(Collectors.toList());
		List<Schedule> todaySchedule= mySchedules.stream().filter(today ->today.getDate().isEqual(LocalDate.now())).collect(Collectors.toList());
		List<Schedule> upcomingSchedule= mySchedules.stream().filter(upcoming ->upcoming.getDate().isAfter(LocalDate.now())).collect(Collectors.toList());
		model.addAttribute("todaySchedule", todaySchedule.size());
		model.addAttribute("upcomingSchedule", upcomingSchedule.size());
		model.addAttribute("selectedJobs", selected.size());
		model.addAttribute("pendingJobs", pending.size());
		model.addAttribute("rejectedJobs", rejected.size());
		model.addAttribute("appliedJobs", candidateJobList.size());
		model.addAttribute("notScheduledJobs", notScheduleJobs.size());
		model.addAttribute("scheduledJobs", scheduleJobs.size());
		model.addAttribute("candidate", candidate);
		return "candidate_dashboard";
	}

	// ==========================forgot password and reset======================//
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
	public ModelAndView candidateForgotPasswordPage(HttpSession session, Candidate newCandidate) {
		LOGGER.debug("entered into candidate/controller::::forgot paswword method");
		ModelAndView mav = new ModelAndView("candidate_forgot_password");
		mav.addObject("newCandidateDetails", newCandidate);
		return mav;
	}

	@PostMapping(value = "/validateEmail")
	public String checkMailId(HttpSession session, Model model, Candidate tempCandidate) {
		LOGGER.debug("entered into Candidate/controller::::check EmailId existing or not");
		LOGGER.debug("UI given mail Id:" + tempCandidate.getEmail());
		Candidate existCandidate = candidateService.findByEmail(tempCandidate.getEmail());
		if (existCandidate != null) {
			model.addAttribute("newCandidateDetails", tempCandidate);
			return "candidate_reset_password";
		} else {
			model.addAttribute("newCandidateDetails", tempCandidate);
			model.addAttribute("message", "email doesn't exist!!!");
			return "candidate_forgot_password";
		}
	}

	@RequestMapping(value = "/updateCandidatePassword", method = RequestMethod.POST)
	public ModelAndView updateCandidatePassword(Model model,
			@ModelAttribute("newCandidateDetails") Candidate tempCandidate, HttpSession session) {
		Candidate existCandidate = candidateService.findByEmail(tempCandidate.getEmail());
		LOGGER.debug(" in update method employee created date::" + existCandidate.getCreatedDate());
		LOGGER.debug("in update method pUser:: name " + existCandidate.getFirstName());
		existCandidate.setUpdatedBy(existCandidate.getCandidateId());
		existCandidate.setUpdatedDate(LocalDateTime.now());
		LOGGER.debug("new password is::::" + existCandidate.getPassword());
		String strEncPassword = Utilities.getEncryptSecurePassword(tempCandidate.getPassword(), "GLAM");
		tempCandidate.setPassword(strEncPassword);
		existCandidate.setPassword(tempCandidate.getPassword());
		candidateService.updateCandidate(existCandidate);
		LOGGER.debug("password is updated sucessfully:::" + existCandidate.getPassword());
		ModelAndView mav = new ModelAndView("candidate_pages_login");
		LOGGER.debug("login page is displayed");
		mav.addObject("candidate", existCandidate);
		mav.addObject("message", "your password is upadated!!");
		return mav;
	}// ==========================forgot password and reset======================//

	// candidate logout method
	@GetMapping("/logout")
	public String logout(Model model,HttpSession session, Candidate logoutCandidate, HttpServletRequest request) {
		session.setAttribute("candidateObj", null);
		LOGGER.debug("session:::"+session.getAttribute("candidateObj"));
		return "redirect:/resourcing";
	}


// uploading files =============================//
// =============================================//

	@GetMapping("/doc/{candidateId}")
	public String listOfCandidateDocs(Model model, @PathVariable int candidateId) {
		LOGGER.debug("candidateId in list method:::::" + candidateId);
		Candidate candidate = candidateService.getCandidateById(candidateId);
		List<Doc> docs = docStorageService.getAllFilesById(candidate.getCandidateId());
		List<Doc> sortedDocs = docs.stream().sorted(Comparator.comparing(Doc::getDocName)).collect(Collectors.toList());
		model.addAttribute("docs", sortedDocs);
		return "candidate_doc";
	}

	@PostMapping("/uploadFiles/{candidateId}")
	public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, Doc docObj,
			@PathVariable int candidateId) throws IOException {
		for (MultipartFile file : files) {
			LOGGER.debug("candidateId in upload method::;" + candidateId);
			docObj.setCandidateId(candidateId);
			docStorageService.saveFile(file, docObj);
			LOGGER.debug("uploaded successfully::::::");
		}
		return "redirect:/candidate/doc/" + candidateId;
	}

	// download the document by file id
	@GetMapping("/downloadFile/{fileId}")
	public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Integer fileId) throws Exception {
		Doc doc = docStorageService.getFileById(fileId);
		LOGGER.debug(":::::::::::");
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(doc.getDocType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + doc.getDocName() + "\"")
				.body(new ByteArrayResource(doc.getData()));
	}

	// delete a document by id
	@GetMapping("/deleteFile/{candidateId}/{fileId}")
	public String deleteFile(@PathVariable int candidateId, @PathVariable int fileId, Doc doc) {
		LOGGER.debug("fileid:::::" + fileId);
		docStorageService.deleteFileById(fileId);
		LOGGER.debug("file deleted:::::");
		return "redirect:/candidate/doc/" + candidateId;
	}

	//get Feedback of interview
	@GetMapping("/feedbackOfInterview/{id}")
	public String scheduleList(Model model, @PathVariable int id) {
		LOGGER.debug("method invoked:::::::::");
		Candidate candidateObj = candidateService.getCandidateById(id);
		List<Schedule> objSchedule = scheduleService.finalFeedbackList(candidateObj);
		LOGGER.debug("size:::::" + objSchedule.size());
		model.addAttribute("scheduled JObs", objSchedule.size());
		model.addAttribute("scheduleList", objSchedule);
		return "candidate_interview_feedbacklist";
	}

	
	// save the Candidate JD Association when the candidate applying for the JOB
		@GetMapping(value = "/addCJA/{id}")
		public String applyingPage(Model model, @PathVariable(value = "id") int jdId, Candidate candidate,
				RedirectAttributes redirectAttributes, CandidateJdAssociation candidateJdAssociation, HttpSession session) {
			LOGGER.debug("method started:::::::::;");
			Candidate candidateObj = (Candidate) session.getAttribute("candidateObj");
			JobDescription job = jobDescriptionService.getJobDescriptionById(jdId);
			LOGGER.debug("session is not null::::::::::");
			LOGGER.debug("id::::" + jdId);
			// Checking whether the Candidate is already applied for this job Or Not
			CandidateJdAssociation candidateJdAssociationObj = candidateJdAssociationService.findByCandidateIdAndJdId(candidateObj, job);
			if (candidateJdAssociationObj == null) {
				LOGGER.debug("candidate not applied for this job::::::::");
				candidateJdAssociation.setCandidate(candidateObj);
				candidateJdAssociation.setCreatedBy(candidateObj.getCandidateId());
				candidateJdAssociation.setIsActive('Y');
				candidateJdAssociation.setCreatedDate(LocalDateTime.now());
				candidateJdAssociation.setJobDescription(job);
				candidateJdAssociation.setStatus("NOT SCHEDULED");
				LOGGER.debug("if condition satisfied::::");
				candidateJdAssociationService.updateCandidateJdAssociation(candidateJdAssociation);
				redirectAttributes.addFlashAttribute("success", "applied");
				redirectAttributes.addAttribute("jobDescription", job);
				return "redirect:/job/jdListForUnknown";
			} else {
				LOGGER.debug("else condition working:::");
				redirectAttributes.addFlashAttribute("message", "already applied");
				redirectAttributes.addAttribute("jobDescription", job);
				return "redirect:/job/jdListForUnknown";
			}
		}
	
	
	
	//=========methods for dashboard having all the details about the Schedule..===========//
	
	@GetMapping("/selectedJobs/{candidateId}")
	public String statusOfTheInterview(@PathVariable int candidateId,Model model) {
		List<CandidateJdAssociation> list = candidateJdAssociationService.getAllCandidateJdAssociations();
		List<CandidateJdAssociation> schedule = list.stream().filter(obj -> obj.getCandidate().getCandidateId()==candidateId).collect(Collectors.toList());
		LOGGER.debug("check::"+schedule.size());
		List<CandidateJdAssociation> selected = schedule.stream().filter(result ->  result.getStatus().equals("SELECTED")).collect(Collectors.toList());
		model.addAttribute("TableName"," Congratulations!! you are selected for "+ selected.size() +" JOBs ");
		model.addAttribute("result", selected);
		return "candidate_jd_status";
	}
	
	@GetMapping("/rejectedJobs/{candidateId}")
	public String candidateRejectedJobs(@PathVariable int candidateId,Model model) {
		List<CandidateJdAssociation> list = candidateJdAssociationService.getAllCandidateJdAssociations();
		List<CandidateJdAssociation> schedule = list.stream().filter(obj -> obj.getCandidate().getCandidateId()==candidateId).collect(Collectors.toList());
		LOGGER.debug("check::"+schedule.size());
		List<CandidateJdAssociation> selected = schedule.stream().filter(result -> result.getStatus().equals("REJECTED")).collect(Collectors.toList());
		model.addAttribute("TableName",  " you are rejected for "+ selected.size() +" JOBs ");
		model.addAttribute("result", selected);
		return "candidate_jd_status";
	}
	
	@GetMapping("/pendingJobs/{candidateId}")
	public String candidatePendingJobs(@PathVariable int candidateId,Model model) {
		List<CandidateJdAssociation> list = candidateJdAssociationService.getAllCandidateJdAssociations();
		List<CandidateJdAssociation> schedule = list.stream().filter(obj -> obj.getCandidate().getCandidateId()==candidateId).collect(Collectors.toList());
		LOGGER.debug("check::"+schedule.size());
		List<CandidateJdAssociation> selected = schedule.stream().filter(result -> result.getStatus().equals("SCHEDULED")).collect(Collectors.toList());
		model.addAttribute("TableName",   selected.size() + " JOBs are in pending state");
		model.addAttribute("result", selected);
		return "candidate_jd_status";
	}
	
	@GetMapping("/scheduledJobs/{candidateId}")
	public String scheduledJobs(@PathVariable int candidateId,Model model) {
		List<CandidateJdAssociation> list = candidateJdAssociationService.getAllCandidateJdAssociations();
		List<CandidateJdAssociation> candidateList = list.stream().filter(obj -> obj.getCandidate().getCandidateId()==candidateId).collect(Collectors.toList());
		List<CandidateJdAssociation> scheduled = candidateList.stream().filter(result -> result.getStatus().equals("SCHEDULED")).collect(Collectors.toList());
		model.addAttribute("TableName",   scheduled.size() +" JOBs are scheduled for you!!");
		model.addAttribute("association", scheduled);
		return "candidate_jd_status";
	}
	
	@GetMapping("/notScheduledJobs/{candidateId}")
	public String notScheduledJobs(@PathVariable int candidateId,Model model) {
		List<CandidateJdAssociation> list = candidateJdAssociationService.getAllCandidateJdAssociations();
		List<CandidateJdAssociation> candidateList = list.stream().filter(obj -> obj.getCandidate().getCandidateId()==candidateId).collect(Collectors.toList());
		List<CandidateJdAssociation> scheduled = candidateList.stream().filter(result -> result.getStatus().equals("NOT SCHEDULED")).collect(Collectors.toList());
		model.addAttribute("TableName",   scheduled.size() +" JOBs are need to schedule for you!!");
		model.addAttribute("association", scheduled);
		return "candidate_jd_status";
	}
	
	@GetMapping("/detailViewOfSchedule/{candidateId}/{jdId}")
	public String detaileViewOfSchedule(@PathVariable int candidateId,@PathVariable int jdId,Model model) {
		Candidate candidate = candidateService.getCandidateById(candidateId);
		JobDescription jobDescription = jobDescriptionService.getJobDescriptionById(jdId);
		List<Schedule> schedule = scheduleService.listOfCandidateAndJdAssociation(candidate, jobDescription);
		model.addAttribute("schedule", schedule);
		model.addAttribute("TableName",   "Feedback of all your Interview Phases for Job "+jobDescription.getPosition().toUpperCase());
		return "candidate_jd_feedback";
	}
	
	@GetMapping("/appliedJobs/{candidateId}")
	public String appliedJobs(@PathVariable int candidateId,Model model) {
		LOGGER.debug("Start time:\t" + System.currentTimeMillis());
		List<CandidateJdAssociation> list = candidateJdAssociationService.getAllCandidateJdAssociations();
		List<CandidateJdAssociation> jdList = list.stream().filter(obj -> obj.getCandidate().getCandidateId()==candidateId).collect(Collectors.toList());
		model.addAttribute("TableName",   "Feedback Of all your "+jdList.size() +"JOBs");
		model.addAttribute("appliedJobs", jdList);
		return "candidate_jd_status";
	}
	
	@GetMapping("/todaysSchedule/{candidateId}")
	public String todaySchedule(Model model,@PathVariable int candidateId) {
		List<Schedule> allSchedules = scheduleService.getAllSchedules();
		List<Schedule> mySchedules = allSchedules.stream().filter(obj -> obj.getCandidate().getCandidateId()==candidateId).collect(Collectors.toList());
		List<Schedule> todaySchedule= mySchedules.stream().filter(list ->list.getDate().isEqual(LocalDate.now())).collect(Collectors.toList());
		model.addAttribute("schedule", todaySchedule);
		model.addAttribute("TableName", "TODAY SCHEDULES");
		return "candidate_today_schedule";
	}
	
	@GetMapping("/upcomingSchedule/{candidateId}")
	public String upcomingSchedule(Model model,@PathVariable int candidateId) {
		List<Schedule> allSchedules = scheduleService.getAllSchedules();
		List<Schedule> mySchedules = allSchedules.stream().filter(obj -> obj.getCandidate().getCandidateId()==candidateId).collect(Collectors.toList());
		List<Schedule> upcomingSchedule= mySchedules.stream().filter(list ->list.getDate().isAfter(LocalDate.now())).toList();
		LOGGER.debug("today schedule count::"+upcomingSchedule.size());
		model.addAttribute("schedule", upcomingSchedule);
		model.addAttribute("TableName", "UPCOMING SCHEDULES");
		return "candidate_today_schedule";
	}
	
	//==================	Dashboard  Closed		=================================//
	
	
	
	
	
}
