package com.gini.notifygateway.dao;

import com.gini.notifygateway.config.SchemaConfig;
import com.gini.notifygateway.model.Sgsms;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SgsmsRepository extends PagingAndSortingRepository<Sgsms, String> {
    @Query(value = "SELECT id,body,success,msg,time FROM "+ SchemaConfig.schema+".Sgsms WHERE time BETWEEN :startTime AND :endTime WITH UR",nativeQuery = true)
    List<Sgsms> findAllByTime(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM "+SchemaConfig.schema+".Sgsms WHERE time BETWEEN :startTime AND :endTime",nativeQuery = true)
    void deleteALLByTime(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
