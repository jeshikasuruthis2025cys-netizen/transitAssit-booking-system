package com.transitassist.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AssistanceRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /** null until staff assigns a helper */
    @ManyToOne
    @JoinColumn(name = "helper_id")
    private Helper helper;

    private String pickupPoint;
    private LocalDateTime pickupTime;
    private LocalDateTime endTime;          // pickupTime + duration (used for overlap check)

    @Enumerated(EnumType.STRING)
    private AssistanceType assistanceType;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.REQUESTED;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Helper getHelper() { return helper; }
    public void setHelper(Helper helper) { this.helper = helper; }
    public String getPickupPoint() { return pickupPoint; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }
    public LocalDateTime getPickupTime() { return pickupTime; }
    public void setPickupTime(LocalDateTime pickupTime) { this.pickupTime = pickupTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public AssistanceType getAssistanceType() { return assistanceType; }
    public void setAssistanceType(AssistanceType t) { this.assistanceType = t; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
}
