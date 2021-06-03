/**
 * 
 */
package com.synectiks.dynModel.repositories;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.synectiks.commons.entities.CloudEntity;

/**
 * @author Rajesh
 */
@Repository
public interface CloudEntityRepository extends CrudRepository<CloudEntity, Long> {

	@Query("select ce from #{#entityName} ce "
			//+ "group by ce.cloudName, ce.groupName "
			+ "order by ce.cloudName Asc, ce.groupName Asc, ce.entity Asc")
	public List<CloudEntity> findByOrderAndSort();

	public List<CloudEntity> findByCloudName(String cloudName, Sort sortby);

}
