package com.simmongs.mrp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MRPRepository extends JpaRepository<MRPs, Long> {

    MRPs getByNeededProductCode(String code);
}
