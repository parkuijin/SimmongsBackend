package com.simmongs.bom;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Builder // 객체 생성은 Setter 대신 Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 어노테이션
@Entity(name = "bom")
public class BOM {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bom_id;

    private String bom_code;
    private String product_code;
    private String child_product_code;
    private int bom_amount;

    public BOM(String bom_code, String product_code, String child_product_code, int bom_amount) {
        this.bom_code = bom_code;
        this.product_code = product_code;
        this.child_product_code = child_product_code;
        this.bom_amount = bom_amount;
    }
}
