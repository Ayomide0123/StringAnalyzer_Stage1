package com.hng.StringAnalyzer.repository;

import com.hng.StringAnalyzer.model.StringEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StringRepository extends JpaRepository<StringEntity, String> {
}
