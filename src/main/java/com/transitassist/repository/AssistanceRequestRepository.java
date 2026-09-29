package com.transitassist.repository;

import com.transitassist.model.AssistanceRequest;
import com.transitassist.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AssistanceRequestRepository extends JpaRepository<AssistanceRequest, Long> {

    /** true if helper already has an ASSIGNED request overlapping [start, end) */
    @Query("select count(r) > 0 from AssistanceRequest r " +
           "where r.helper.id = :helperId and r.status = com.transitassist.model.RequestStatus.ASSIGNED " +
           "and r.id <> :excludeId and r.pickupTime < :end and r.endTime > :start")
    boolean existsOverlap(@Param("helperId") Long helperId, @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end, @Param("excludeId") Long excludeId);

    List<AssistanceRequest> findByHelperIdAndPickupTimeBetweenAndStatusInOrderByPickupTime(
            Long helperId, LocalDateTime from, LocalDateTime to, List<RequestStatus> statuses);
}
