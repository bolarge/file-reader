package com.dot.filereader.service;

import com.dot.filereader.entity.UserAccessLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FileReader is an interface, that provides the contract for its operations. Its specifies the API for communication with other objects.
 */
public interface FileReader {

    void openFile();
    void processFile();
    void readCurrentLine(String currentLine);
    //
    List<UserAccessLog> checkRequestRate(String ip, LocalDateTime startTime, LocalDateTime endTime);
    Long CheckRequestRateCount(String ip, LocalDateTime startTime, LocalDateTime endTime, int limit);
    Long CheckRequestRateCount(String ip, LocalDateTime startTime, String duration, int limit);
    void checkRequestRateCount(LocalDateTime startTime, String duration, int limit);
}
