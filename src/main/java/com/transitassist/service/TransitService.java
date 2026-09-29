package com.transitassist.service;

import com.transitassist.dto.CreateRequestDto;
import com.transitassist.exception.BusinessRuleException;
import com.transitassist.exception.NotFoundException;
import com.transitassist.model.*;
import com.transitassist.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TransitService {

    private final UserRepository users;
    private final HelperRepository helpers;
    private final AssistanceRequestRepository requests;

    public TransitService(
            UserRepository users,
            HelperRepository helpers,
            AssistanceRequestRepository requests) {

        this.users = users;
        this.helpers = helpers;
        this.requests = requests;
    }

    // ================= USER =================

    // POST - Create user
    public User createUser(User u) {
        return users.save(u);
    }

    // GET - Get all users
    public List<User> listUsers() {
        return users.findAll();
    }

    // ================= HELPER =================

    // POST - Create helper
    public Helper createHelper(Helper h) {
        return helpers.save(h);
    }

    // GET - Get all helpers
    public List<Helper> listHelpers() {
        return helpers.findAll();
    }

    // ================= ASSISTANCE REQUEST =================

    // GET - Get all requests
    public List<AssistanceRequest> listRequests() {
        return requests.findAll();
    }

    // GET - Get request by ID
    public AssistanceRequest getRequest(Long id) {

        return requests.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Request " + id + " not found"));
    }

    // POST - Create assistance request
    public AssistanceRequest createRequest(CreateRequestDto dto) {

        User user = users.findById(dto.userId())
                .orElseThrow(() ->
                        new NotFoundException(
                                "User " + dto.userId() + " not found"));

        int duration = dto.durationMinutes() == null
                ? 30
                : dto.durationMinutes();

        AssistanceRequest r = new AssistanceRequest();

        r.setUser(user);
        r.setPickupPoint(dto.pickupPoint());
        r.setPickupTime(dto.pickupTime());
        r.setEndTime(
                dto.pickupTime().plusMinutes(duration)
        );
        r.setAssistanceType(dto.assistanceType());
        r.setStatus(RequestStatus.REQUESTED);

        return requests.save(r);
    }

    // PUT - Update assistance request
    public AssistanceRequest updateRequest(
            Long id,
            CreateRequestDto dto) {

        AssistanceRequest request = getRequest(id);

        // Prevent updating completed request
        if (request.getStatus() == RequestStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "A completed request cannot be updated");
        }

        // Prevent updating cancelled request
        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "A cancelled request cannot be updated");
        }

        request.setPickupPoint(dto.pickupPoint());
        request.setPickupTime(dto.pickupTime());
        request.setAssistanceType(dto.assistanceType());

        int duration = dto.durationMinutes() == null
                ? 30
                : dto.durationMinutes();

        request.setEndTime(
                dto.pickupTime().plusMinutes(duration)
        );

        return requests.save(request);
    }

    // DELETE - Delete assistance request
    public void deleteRequest(Long id) {

        AssistanceRequest request = getRequest(id);

        requests.delete(request);
    }

    // ================= ASSIGN HELPER =================

    // POST - Assign helper
    public AssistanceRequest assign(
            Long requestId,
            Long helperId) {

        if (helperId == null) {
            throw new BusinessRuleException(
                    "helperId is required");
        }

        AssistanceRequest r = getRequest(requestId);

        Helper h = helpers.findById(helperId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Helper " + helperId + " not found"));

        if (r.getStatus() == RequestStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "A completed request cannot be reassigned");
        }

        if (r.getStatus() == RequestStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "A cancelled request cannot be assigned");
        }

        if (requests.existsOverlap(
                helperId,
                r.getPickupTime(),
                r.getEndTime(),
                r.getId())) {

            throw new BusinessRuleException(
                    "Helper " + h.getName()
                            + " already has an overlapping request at that time");
        }

        r.setHelper(h);
        r.setStatus(RequestStatus.ASSIGNED);

        return requests.save(r);
    }

    // ================= COMPLETE =================

    // POST - Complete request
    public AssistanceRequest complete(Long id) {

        AssistanceRequest r = getRequest(id);

        if (r.getStatus() == RequestStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "Request is already completed");
        }

        if (r.getStatus() == RequestStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "A cancelled request cannot be completed");
        }

        if (r.getStatus() != RequestStatus.ASSIGNED) {
            throw new BusinessRuleException(
                    "Only an assigned request can be completed");
        }

        r.setStatus(RequestStatus.COMPLETED);

        return requests.save(r);
    }

    // ================= CANCEL =================

    // POST - Cancel request
    public AssistanceRequest cancel(Long id) {

        AssistanceRequest r = getRequest(id);

        if (r.getStatus() == RequestStatus.COMPLETED) {
            throw new BusinessRuleException(
                    "A completed request cannot be cancelled");
        }

        if (r.getStatus() == RequestStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "Request is already cancelled");
        }

        r.setStatus(RequestStatus.CANCELLED);

        return requests.save(r);
    }

    // ================= HELPER WORKLOAD =================

    // GET - Helper workload for a day
    public List<AssistanceRequest> workload(
            Long helperId,
            LocalDate date) {

        if (!helpers.existsById(helperId)) {
            throw new NotFoundException(
                    "Helper " + helperId + " not found");
        }

        return requests
                .findByHelperIdAndPickupTimeBetweenAndStatusInOrderByPickupTime(
                        helperId,
                        date.atStartOfDay(),
                        date.plusDays(1)
                                .atStartOfDay()
                                .minusNanos(1),
                        List.of(
                                RequestStatus.ASSIGNED,
                                RequestStatus.COMPLETED
                        )
                );
    }
}