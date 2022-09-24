
package com.resourcing.controller;

import java.nio.file.AccessDeniedException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import javax.persistence.NonUniqueResultException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionClass extends ResponseEntityExceptionHandler {

	@ExceptionHandler(NonUniqueResultException.class)
	public String handleNonUniqueResultException(NonUniqueResultException nonUniqueResultException, Model model) {
		model.addAttribute("message", nonUniqueResultException.getCause());
		System.out.println("nonUniqueResultException");
		return "error";
	}

	@ExceptionHandler(ClassNotFoundException.class)
	public String handelException(ClassNotFoundException classNotFoundException, Model model) {
		model.addAttribute("message", classNotFoundException.getMessage());
		System.out.println("classNotFoundException");
		return "error";
	}

	@ExceptionHandler(IllegalAccessException.class)
	public String handelIllegalAccessException(IllegalAccessException illegalAccessException, Model model) {
		model.addAttribute("message", illegalAccessException.getCause());
		System.out.println("illegalAccessException");
		return "error";
	}

	@ExceptionHandler(NoSuchFieldException.class)
	public String handelNoSuchFieldException(NoSuchFieldException noSuchFieldException, Model model) {
		model.addAttribute("message", noSuchFieldException.getCause());
		System.out.println("noSuchFieldException");
		return "error";
	}

	@ExceptionHandler(NoSuchMethodException.class)
	public String handelNoSuchMethodException(NoSuchMethodException noSuchFieldException, Model model) {
		model.addAttribute("message", noSuchFieldException.getCause());
		System.out.println("noSuchFieldException");
		return "error";
	}

	@ExceptionHandler(InterruptedException.class)
	public String handelInterruptedException(InterruptedException interruptedException, Model model) {
		model.addAttribute("message", interruptedException.getCause());
		System.out.println("interruptedException");
		return "error";
	}

	@ExceptionHandler(IndexOutOfBoundsException.class)
	public String handelIndexOutOfBoundsException(IndexOutOfBoundsException indexOutOfBoundsException, Model model) {
		model.addAttribute("message", indexOutOfBoundsException.getCause());
		System.out.println("indexOutOfBoundsException");
		return "error";
	}

	@ExceptionHandler(IllegalThreadStateException.class)
	public String handelIllegalThreadStateException(IllegalThreadStateException illegalThreadStateException,
			Model model) {
		model.addAttribute("message", illegalThreadStateException.getCause());
		System.out.println("illegalThreadStateException");
		return "error";
	}

	@ExceptionHandler(InputMismatchException.class)
	public String handelInputMismatchException(InputMismatchException inputMismatchException, Model model) {
		model.addAttribute("message", inputMismatchException.getCause());
		System.out.println("inputMismatchException");
		return "error";
	}

	@ExceptionHandler(NumberFormatException.class)
	public String handelNumberFormatException(NumberFormatException numberFormatException, Model model) {
		model.addAttribute("message", numberFormatException.getMessage());
		System.out.println("numberFormatException" + numberFormatException.getMessage());
		return "error";
	}

	@ExceptionHandler(ArithmeticException.class)
	public String handelArithmeticException(ArithmeticException ArithmeticException, Model model) {
		model.addAttribute("message", ArithmeticException.getCause());
		System.out.println("ArithmeticException");
		return "error";
	}

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Element not Found")
	public String handelNoSuchElementException(NoSuchElementException NoSuchElementException, Model model) {
		model.addAttribute("message", NoSuchElementException.getClass());
		System.out.println("NoSuchElementException:::" + NoSuchElementException.getMessage());
		return "error";
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "AccessDenied")
	public String handelAccessDeniedException(AccessDeniedException AccessDeniedException, Model model) {
		model.addAttribute("message", AccessDeniedException.getCause());
		System.out.println("AccessDeniedException");
		return "error";
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public String handelAccessDeniedException(EmptyResultDataAccessException emptyResultDataAccessException,
			Model model) {
		model.addAttribute("message", emptyResultDataAccessException.getCause());
		System.out.println("emptyResultDataAccessException");
		return "error";
	}

	@ExceptionHandler(NullPointerException.class)
	public String handelNullPointerException(NullPointerException nullPointerException, Model model) {
		model.addAttribute("message", nullPointerException.getMessage());
		System.out.println("nullPointerException::" + nullPointerException.getMessage());
		return "error";
	}

	@ExceptionHandler(MBeanServerNotFoundException.class)
	public String handelMBeanServerNotFoundException(MBeanServerNotFoundException MBeanServerNotFoundException,
			Model model) {
		model.addAttribute("message", MBeanServerNotFoundException.getMessage());
		System.out.println("MBeanServerNotFoundException");
		return "error";
	}

	@ExceptionHandler(Exception.class)
	public String globalExceptionHandler(Exception e, Model model) {
		model.addAttribute("message", e.getMessage());
		return "error";
	}

	// 22-08-2022 testing

	@ExceptionHandler(DataAccessException.class)
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "The session has expired")
	public String handleSessionExpired(DataAccessException ex, Model model) {
		model.addAttribute("message", DataAccessException.class);
		System.out.println("DataAccessException:::::" + DataAccessException.class);
		return "error";
	}
	
	@ExceptionHandler(BadRequest.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad Request")
	public String HandleBadRequest(BadRequest badRequest,Model model) {
         model.addAttribute("message", badRequest.getMessage());
		System.out.println("Bad Request:::::" + badRequest.getMessage());
		return "error";
	}
	
 
}
