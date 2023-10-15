package com.simmongs.bom;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder // 객체 생성은 Setter 대신 Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 어노테이션
@Entity(name = "BOM_TB")
public class BOMs {

    @Column(name = "BOM_ID")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bom_id;

    @Column(name = "PRODUCT_CODE")
    private String product_code;

    @Column(name = "CHILD_PRODUCT_CODE")
    private String child_product_code;

    @Column(name = "BOM_AMOUNT")
    private int bom_amount;

    public BOMs(String product_code, String child_product_code, int bom_amount) {
        this.product_code = product_code;
        this.child_product_code = child_product_code;
        this.bom_amount = bom_amount;
    }
}
