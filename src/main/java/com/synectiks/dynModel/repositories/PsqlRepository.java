/**
 * 
 */
package com.synectiks.dynModel.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.synectiks.dynModel.models.PSqlEntity;

/**
 * @author Rajesh Upadhyay
 */
@Repository
public interface PsqlRepository extends CrudRepository<PSqlEntity, Long> {

}
