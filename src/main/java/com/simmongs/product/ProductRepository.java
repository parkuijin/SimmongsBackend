package com.simmongs.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> { // 실제 DB에 Access 하여 쿼리를 수행하는 등의 역할, DB에 CRUD 쿼리를 사용 가능

}
