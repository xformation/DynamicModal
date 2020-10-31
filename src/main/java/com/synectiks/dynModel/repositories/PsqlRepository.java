/**
 * 
 */
package com.synectiks.dynModel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.synectiks.commons.entities.PSqlEntity;

/**
 * @author Rajesh Upadhyay
 */
@Repository
public interface PsqlRepository extends JpaRepository<PSqlEntity, Long> {

}
