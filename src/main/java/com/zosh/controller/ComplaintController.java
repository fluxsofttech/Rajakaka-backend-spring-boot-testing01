package com.zosh.controller;

import com.zosh.exception.UserException;
import com.zosh.model.Complaint;
import com.zosh.model.User;
import com.zosh.service.ComplaintService;
import com.zosh.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manage/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserService userService;

    // ✅ Raise a new complaint
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(
            @RequestBody Complaint complaint,
            @RequestHeader("Authorization") String jwt
    ) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        Complaint saved = complaintService.createComplaint(user, complaint);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ Get logged-in user's complaints
    @GetMapping("/me")
    public ResponseEntity<List<Complaint>> getMyComplaints(
            @RequestHeader("Authorization") String jwt
    ) throws UserException {
        User user = userService.findUserProfileByJwt(jwt);
        List<Complaint> complaints = complaintService.getComplaintsByUser(user);
        return new ResponseEntity<>(complaints, HttpStatus.OK);
    }

    // ✅ Admin: Get all complaints
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        return new ResponseEntity<>(complaints, HttpStatus.OK);
    }

    // ✅ Admin: Update complaint status
    @PutMapping("/{complaintId}/status")
    public ResponseEntity<Complaint> updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestParam String status
    ) {
        Complaint updated = complaintService.updateComplaintStatus(complaintId, status);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
