package com.synectiks.dynModel.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirRepository extends JpaRepository<Dir, Long> {

}