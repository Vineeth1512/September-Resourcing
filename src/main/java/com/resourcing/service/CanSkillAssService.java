/**
  *	
  *@author:Praveen Gudimalla
  *@author:Nikhil Gundla
  *
  **/

package com.resourcing.service;

import java.util.List;

import com.resourcing.beans.CandidateSkillAssociation;

public interface CanSkillAssService {

	List<CandidateSkillAssociation> getAllCandidateSkillAssociations();

	void addNewCsa(CandidateSkillAssociation candidateSkillAssociation);

	void updateCandidateSkill(CandidateSkillAssociation candidateSkillAssociation);

	void deleteCandidateSkillAssociationBySkillId(int skillId);

	CandidateSkillAssociation getAssociationById(int skillId);

	List<CandidateSkillAssociation> findSkillListByCandidateId(int candidateId);

	List<CandidateSkillAssociation> findCandidateListBySkillId(int skillId);

	CandidateSkillAssociation findBySkillIdAndCanId(int candidateId, int skillId);

}
