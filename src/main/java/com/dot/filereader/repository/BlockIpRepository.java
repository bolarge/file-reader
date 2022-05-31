package com.dot.filereader.repository;

import com.dot.filereader.entity.BlockedIp;
import org.springframework.data.repository.CrudRepository;

public interface BlockIpRepository extends CrudRepository<BlockedIp, Long> {
}
