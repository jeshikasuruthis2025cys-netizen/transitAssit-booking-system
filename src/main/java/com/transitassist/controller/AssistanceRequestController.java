package com.transitassist.controller;

import com.transitassist.dto.AssignDto;
import com.transitassist.dto.CreateRequestDto;
import com.transitassist.model.AssistanceRequest;
import com.transitassist.model.Helper;
import com.transitassist.model.User;
import com.transitassist.service.TransitService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AssistanceRequestController {

    private final TransitService transitService;

    public AssistanceRequestController(TransitService transitService) {
        this.transitService = transitService;
    }

    // ================= USER =================

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        return transitService.createUser(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return transitService.listUsers();
    }

    // ================= HELPER =================

    @PostMapping("/helpers")
    public Helper createHelper(@Valid @RequestBody Helper helper) {
        return transitService.createHelper(helper);
    }

    @GetMapping("/helpers")
    public List<Helper> getAllHelpers() {
        return transitService.listHelpers();
    }

    @GetMapping("/helpers/{helperId}/workload")
    public List<AssistanceRequest> getHelperWorkload(
            @PathVariable Long helperId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return transitService.workload(helperId, date);
    }

    // ================= ASSISTANCE REQUEST =================

    // POST - Create request
    @PostMapping("/requests")
    public AssistanceRequest createRequest(
            @Valid @RequestBody CreateRequestDto dto) {

        return transitService.createRequest(dto);
    }

    // GET - Get all requests
    @GetMapping("/requests")
    public List<AssistanceRequest> getAllRequests() {
        return transitService.listRequests();
    }

    // GET - Get request by ID
    @GetMapping("/requests/{id}")
    public AssistanceRequest getRequestById(
            @PathVariable Long id) {

        return transitService.getRequest(id);
    }

    // PUT - Update request
    @PutMapping("/requests/{id}")
    public AssistanceRequest updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody CreateRequestDto dto) {

        return transitService.updateRequest(id, dto);
    }

    // DELETE - Delete request
    @DeleteMapping("/requests/{id}")
    public String deleteRequest(
            @PathVariable Long id) {

        transitService.deleteRequest(id);

        return "Assistance request deleted successfully";
    }

    // ================= STATUS OPERATIONS =================

    // POST - Assign helper
    @PostMapping("/requests/{id}/assign")
    public AssistanceRequest assign(
            @PathVariable Long id,
            @Valid @RequestBody AssignDto dto) {

        return transitService.assign(id, dto.helperId());
    }

    // POST - Complete request
    @PostMapping("/requests/{id}/complete")
    public AssistanceRequest complete(
            @PathVariable Long id) {

        return transitService.complete(id);
    }

    // POST - Cancel request
    @PostMapping("/requests/{id}/cancel")
    public AssistanceRequest cancel(
            @PathVariable Long id) {

        return transitService.cancel(id);
    }
}