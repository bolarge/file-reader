package com.dot.filereader.repository;

import com.dot.filereader.entity.UserAccessLog;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserAccessLogRepository extends CrudRepository<UserAccessLog, Long> {

    Long countUserAccessLogByUserIpAndTimeStampIsBetween(String userIp, LocalDateTime startDate, LocalDateTime endDate);
    List<UserAccessLog> findByUserIpAndTimeStampBetween(String userIp, LocalDateTime startTime, LocalDateTime endTime);
    List<UserAccessLog> findByTimeStampBetween(LocalDateTime startTime, LocalDateTime endTime);
    List<UserAccessLog> findUserAccessLogByTimeStampBetween(LocalDateTime startTime, LocalDateTime endTime);
}
