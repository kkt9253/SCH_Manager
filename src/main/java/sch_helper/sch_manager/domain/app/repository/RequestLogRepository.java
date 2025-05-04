package sch_helper.sch_manager.domain.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sch_helper.sch_manager.domain.app.entity.RequestLog;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
}