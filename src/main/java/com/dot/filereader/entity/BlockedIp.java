package com.dot.filereader.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Blocked_IP is a domain entity that represents ips that have exceeded specified rate limit
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "blocked_ip")
public class BlockedIp extends BaseEntity {

    /**
     * Properties
     */
    private String requestNumber;
    private String comments;

    @Override
    public String toString() {
        return String.format(
                "BlockedIp[userIp=%s, requestNumber=%s, comments=%s]",
                userIp, requestNumber, comments);
    }
}
