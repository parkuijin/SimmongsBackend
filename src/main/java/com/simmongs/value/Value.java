package com.simmongs.value;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "VALUE_TB")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Value {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VALUE_ID")
    private Long valueId;

    @Column(name = "PRODUCT_UNIT_VLAUE")
    private String productUnitValue;

    @Column(name = "PRODUCT_TYPE_VALUE")
    private String productTypeValue;

    @Column(name = "STORING_UNSTORING_TYPE_VALUE")
    private String storingUnstoringTypeValue;

    public Value(String productUnitValue, String productTypeValue, String storingUnstoringTypeValue) {
        this.productUnitValue = productUnitValue;
        this.productTypeValue = productTypeValue;
        this.storingUnstoringTypeValue = storingUnstoringTypeValue;
    }
}
