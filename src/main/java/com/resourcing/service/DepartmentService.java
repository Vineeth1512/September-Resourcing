
/**
  *	
  *@author:Praveen Gudimalla
  *@author:Nikhil Gundla
  *
  **/

package com.resourcing.service;

import java.util.List;

import com.resourcing.beans.Department;

public interface DepartmentService {

	Department addDepartment(Department name);

	List<Department> getAllDepartment();

	Department getDepartmentById(int id);

	void deleteCategoryById(int id);

	public Department find(int id);
	
	public Department findByDepartmentName(String deptName);
}
