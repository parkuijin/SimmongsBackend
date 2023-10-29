package com.simmongs.storingunstoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "STORING_UNSTORING_TB")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoringUnStoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORING_UNSTORING_ID", nullable = false)
    private Long storingUnstoringId;

    @Column(name = "STORING_UNSTORING_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime storingUnstoringDate;

    @Column(name = "STORING_UNSTORING_TYPE")
    private String storingUnstoringType;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_TYPE")
    private String productType;

    @Column(name = "STORING_UNSTORING_AMOUNT")
    private int storingUnstoringAmount;

    public StoringUnStoring(LocalDateTime storingUnstoringDate, String storingUnstoringType, String productCode, String productName, String productType, int storingUnstoringAmount) {
        this.storingUnstoringDate = storingUnstoringDate;
        this.storingUnstoringType = storingUnstoringType;
        this.productCode = productCode;
        this.productName = productName;
        this.productType = productType;
        this.storingUnstoringAmount = storingUnstoringAmount;
    }

}
