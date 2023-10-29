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
    @Id
    private String bomId;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "CHILD_PRODUCT_CODE")
    private String childProductCode;

    @Column(name = "BOM_AMOUNT")
    private int bomAmount;

    public BOMs(String product_code, String child_product_code, int bom_amount) {
        this.productCode = product_code;
        this.childProductCode = child_product_code;
        this.bomAmount = bom_amount;
    }
}
