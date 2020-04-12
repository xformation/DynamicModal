/**
 * 
 */
package com.synectiks.dynModel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.synectiks.dynModel.models.PSqlEntity;

/**
 * @author Rajesh Upadhyay
 */
@Repository
public interface ReqLogRepository extends JpaRepository<PSqlEntity, Long> {

}
