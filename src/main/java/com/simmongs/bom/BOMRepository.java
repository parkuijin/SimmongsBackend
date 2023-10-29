package com.simmongs.bom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BOMRepository extends JpaRepository<BOMs, Long> {
    List<BOMs> findByProductCode(String productCode);
    List<BOMs> findByChildProductCode(String childProductCode);
    Optional<BOMs> findByBomId(String bomCode);

}
