package com.dot.filereader.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_access_log")
public class UserAccessLog extends BaseEntity{

    private LocalDateTime timeStamp;
    private String requestScheme, statusCode, userAgent;

    @Override
    public String toString() {
        return String.format(
                "UserAccessLog[timeStamp=%s, userIp=%s, requestScheme=%s, statusCode=%s, userAgent=%s]",
                timeStamp, userIp, requestScheme, statusCode, userAgent);
    }
}
