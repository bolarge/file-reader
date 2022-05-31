package com.dot.filereader.repository;

import com.dot.filereader.entity.BlockedIp;
import org.springframework.data.repository.CrudRepository;

/**
 * BlockIpRepository provides access to the underlying blocked_ip table of the connected datasource
 */
public interface BlockIpRepository extends CrudRepository<BlockedIp, Long> {
}
