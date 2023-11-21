package com.simmongs.storingunstoring;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoringUnStoringRepository extends JpaRepository<StoringUnStoring, Long>, StoringUnStoringRepositoryCustom {

    List<StoringUnStoring> findByProductCode(String code);

}
