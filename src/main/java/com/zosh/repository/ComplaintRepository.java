package com.zosh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zosh.model.Complaint;
import com.zosh.model.User;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>  {
	List<Complaint> findByUser(User user);

}
