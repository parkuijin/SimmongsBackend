package com.simmongs.usedcode;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "USED_CODE_TB")
public class UsedCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USED_CODE_ID")
    private Long usedCodeId;

    @Column(name = "USED_PRODUCT_CODE")
    private Integer usedProductCode;

    @Column(name = "USED_COMPONENT_CODE")
    private Integer usedComponentCode;

    public UsedCodes(Integer usedProductCode, Integer usedComponentCode) {
        this.usedProductCode = usedProductCode;
        this.usedComponentCode = usedComponentCode;
    }

    public void usedProductCodeUpdate() {
        this.usedProductCode += 1;
    }

    public void usedComponentCodeUpdate() {
        this.usedComponentCode += 1;
    }

}
