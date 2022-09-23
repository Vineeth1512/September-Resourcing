//@author: HarshKashyap

package com.resourcing.service;

import java.util.List;

import com.resourcing.beans.SkillInterviewerAssosiation;

public interface SkillInterviewerAssociationService {

    List<SkillInterviewerAssosiation> getAllSkillInterviewerAssociations();

	void addNewSia(SkillInterviewerAssosiation skillInterviewerAssociation);
	
	List<SkillInterviewerAssosiation> findSkillListByInterviewerId(int interviewerId);
	
    void updateSia(SkillInterviewerAssosiation sia);
    void deleteSkillInterviewerAssociationBySkillId(int skillId);
    SkillInterviewerAssosiation getAssociationById(int skillId);
    SkillInterviewerAssosiation updateSkillAssociation(SkillInterviewerAssosiation soa);
     
	public List<SkillInterviewerAssosiation> findBySkill(int skillId);

	public List<SkillInterviewerAssosiation> findInterviewerBySkillId(int skillId);
	
	public SkillInterviewerAssosiation findBySkillIdAndInterviewerId(int interviewerId,int skillId);

	

}
