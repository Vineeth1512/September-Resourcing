
/**
  *	
  *@author:Nikhil Gundla
  *
  *
  **/

package com.resourcing.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.resourcing.beans.Position;
import com.resourcing.repository.PositionRepository;

@Service
public class PositionServiceImpl implements PositionService {

	@Autowired
	PositionRepository positionRepository;

	@Override
	public Position addPosition(Position details) {
		return positionRepository.save(details);
	}


	@Override
	public Position getPositionById(int id) {
		return positionRepository.findById(id).get();
	}

	@Override
	public void deletePositionById(int id) {
		positionRepository.deleteById(id);

	}

	@Override
	public List<Position> findPositionByDepartment(String deptName) {
		return positionRepository.findPositionByDepartment(deptName);
	}

	@Override
	public Position updatePosition(Position post) {
		return positionRepository.save(post);
	}

	@Override
	public Position findByPostionIgnoreCaseAndDepartmentIgnoreCase(String deptName, String postName) {
		return positionRepository.findByPostionIgnoreCaseAndDepartmentIgnoreCase(deptName, postName);
	}

	@Override
	public List<Position> getAllPositions() {
		return positionRepository.findAll();
	}

}
