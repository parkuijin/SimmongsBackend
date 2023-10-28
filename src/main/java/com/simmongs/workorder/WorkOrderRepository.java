package com.simmongs.workorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrders, Long> {

    Optional<WorkOrders> findByWorkOrderId(Long id);
}
