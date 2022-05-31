package com.dot.filereader.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Super class for domain entities
 */

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

    /**
     * Properties
     */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    protected Long id;
    protected String userIp;

}
