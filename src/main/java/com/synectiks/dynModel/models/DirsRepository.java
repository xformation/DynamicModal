package com.synectiks.dynModel.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirsRepository extends JpaRepository<Dirs, Long> {

}