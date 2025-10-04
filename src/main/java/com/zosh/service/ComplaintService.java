package com.zosh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zosh.model.Complaint;
import com.zosh.model.ComplaintStatus;
import com.zosh.model.User;
import com.zosh.repository.ComplaintRepository;

@Service
public class ComplaintService {

	

	@Autowired
    public  ComplaintRepository complaintRepository;

    public Complaint createComplaint(User user, Complaint complaint) {
        complaint.setUser(user);
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getComplaintsByUser(User user) {
        return complaintRepository.findByUser(user);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint updateComplaintStatus(Long complaintId, String status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        // ✅ Convert String → Enum
        ComplaintStatus newStatus = ComplaintStatus.valueOf(status.toUpperCase());
        complaint.setStatus(newStatus);

        return complaintRepository.save(complaint);
    }
}
