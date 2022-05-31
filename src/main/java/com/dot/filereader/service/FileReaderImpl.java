package com.dot.filereader.service;

import com.dot.filereader.entity.BlockedIp;
import com.dot.filereader.entity.UserAccessLog;
import com.dot.filereader.repository.BlockIpRepository;
import com.dot.filereader.repository.UserAccessLogRepository;
import com.dot.filereader.utils.TimeDuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileReaderImpl implements FileReader {

    @Autowired
    private UserAccessLogRepository userAccessLogRepository;
    @Autowired
    private BlockIpRepository blockIpRepository;

    @Override
    public void openFile() {
        Scanner input = null;
        try {
            input = new Scanner( new File( "user_access.txt" ) );
            while (input.hasNextLine())
                readCurrentLine(input.nextLine());
        }
        catch ( FileNotFoundException fileNotFoundException ) {
            System.err.println( "Error opening file." );
            System.exit( 1 );
        }finally {
            if (input != null)
                input.close();
        }
    }

    @Override
    public void processFile() {
        BufferedReader bufferedReader = null;
        try {
            String currentLine;
            bufferedReader = new BufferedReader(new java.io.FileReader("user_access.txt"));
            while ((currentLine = bufferedReader.readLine()) != null)
                readCurrentLine(currentLine);
        }
        catch ( FileNotFoundException fileNotFoundException ) {
            System.err.println( "Error opening file." );
            System.exit( 1 );
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void readCurrentLine(String currentLine) {
        try {
            UserAccessLog userAccessLog = new UserAccessLog();
            ArrayList<UserAccessLog> userAccessLogsList = new ArrayList<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            Scanner recordScanner = new Scanner(currentLine);
            recordScanner.useDelimiter("\\|");
            while (recordScanner.hasNext()) {
                String dateTime = recordScanner.next();
                userAccessLog.setTimeStamp(LocalDateTime.parse(dateTime, dateTimeFormatter));
                userAccessLog.setUserIp(recordScanner.next());
                userAccessLog.setRequestScheme(recordScanner.next());
                userAccessLog.setStatusCode(recordScanner.next());
                userAccessLog.setUserAgent(recordScanner.next());

                log.info(userAccessLog.toString());
                userAccessLogsList.add(userAccessLog);
            }
            userAccessLogRepository.saveAll(userAccessLogsList);
            recordScanner.close();
        } catch ( NoSuchElementException elementException ) {
            System.err.println( "File improperly formed." );
            System.exit( 1 );
        } catch ( IllegalStateException stateException ) {
            System.err.println( "Error reading from file." );
            System.exit( 1 );
        }
    }

    @Override
    public List<UserAccessLog> checkRequestRate(String ip, LocalDateTime startTime, LocalDateTime endTime) {
        List<UserAccessLog> userAccessLogs;
        try{
            userAccessLogs = userAccessLogRepository.findByUserIpAndTimeStampBetween(ip, startTime, endTime);
        }catch (ObjectRetrievalFailureException | EmptyResultDataAccessException e){
            return null;
        }
        log.info("IP request rate {}", userAccessLogs);
        return userAccessLogs;
    }

    @Override
    public Long CheckRequestRateCount(String ip, LocalDateTime startTime, LocalDateTime endTime, int limit) {
        Long count = userAccessLogRepository.countUserAccessLogByUserIpAndTimeStampIsBetween(ip, startTime, endTime);
        List<UserAccessLog> limitExceededList;
        List<BlockedIp> blockedIps = new ArrayList<>();
        if (count > limit){
            BlockedIp newBlockIp = new BlockedIp();
            limitExceededList = checkRequestRate(ip, startTime, endTime);
            for(UserAccessLog blockedIp: limitExceededList){
                newBlockIp.setUserIp(blockedIp.getUserIp());
                newBlockIp.setRequestNumber(String.valueOf(count));
                newBlockIp.setComments("Request rate limit exhausted");
                blockedIps.add(newBlockIp);
            }
            blockIpRepository.saveAll(blockedIps);
        }
        log.info("Total request rate count for {} is {}",ip, count);
        return count;
    }

    @Override
    public Long CheckRequestRateCount(String ip, LocalDateTime startTime, String duration, int limit) {
        Long count = 1L;
        LocalDateTime endTime = calculateEndTime(startTime, duration);
        List<UserAccessLog> userAccessLogs = userAccessLogRepository.findUserAccessLogByTimeStampBetween(startTime, endTime);

        List<UserAccessLog> limitExceededList = userAccessLogs.stream()
                .filter((UserAccessLog u) -> u.getTimeStamp().isAfter(endTime))
                .collect(Collectors.toList());

        count = limitExceededList.stream().count();
        log.info("Total request rate count for {} is {}",ip, count);
        return count;
    }

    @Override
    public void checkRequestRateCount(LocalDateTime startTime, String duration, int limit) {
        LocalDateTime endTime = calculateEndTime(startTime, duration);
        List<UserAccessLog> userAccessLogs = userAccessLogRepository.findUserAccessLogByTimeStampBetween(startTime, endTime);

        Map<String, List<UserAccessLog>> userAccessLogsByCount = userAccessLogs.stream()
                .filter((UserAccessLog u) -> u.getTimeStamp().isBefore(endTime))
                .collect(Collectors.groupingBy(UserAccessLog::getUserIp));

        List<BlockedIp> blockedIps = new ArrayList<>();

        userAccessLogsByCount.forEach((k,v)-> {
            BlockedIp newBlockIp = new BlockedIp();
            if (v.size() >= limit){
                log.info("IP : " + k + " || Value : " + v.size());
                for(UserAccessLog blockedIp: v){
                    newBlockIp.setUserIp(blockedIp.getUserIp());
                    newBlockIp.setRequestNumber(String.valueOf(v.size()));
                    newBlockIp.setComments("Request rate limit exhausted");
                }
                blockedIps.add(newBlockIp);
                blockIpRepository.saveAll(blockedIps);
            }
        });
    }

    private LocalDateTime calculateEndTime(LocalDateTime startTime, String duration){
        LocalDateTime endTime = null;

        long hourly = 60 * 60 * 1000;
        long daily = 24 * 60 * 60 * 1000;
        long period;

        if (TimeDuration.HOURLY.name().equalsIgnoreCase(duration)){
            period = calculateTimeDuration(hourly);
            endTime = startTime.plusHours(period);
        }else if (TimeDuration.DAILY.name().equalsIgnoreCase(duration)){
            period = calculateTimeDuration(daily);
            endTime = startTime.plusHours(period);
        }

        log.info("Calculated end time is {}", endTime);
        return endTime;
    }

    private long calculateTimeDuration(long timeInterval){
        Duration duration = Duration.ofMillis(timeInterval);
        long seconds = duration.getSeconds();
        long period = seconds / 3600;
        log.info("Calculated period is {}", period);
        return period;
    }
}
