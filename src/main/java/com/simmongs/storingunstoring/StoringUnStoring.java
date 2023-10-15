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
    private Long storing_unstoring_id;

    @Column(name = "STORING_UNSTORING_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime storing_unstoring_date;

    @Column(name = "STORING_UNSTORING_TYPE")
    private String storing_unstoring_type;

    @Column(name = "PRODUCT_CODE")
    private String product_code;

    @Column(name = "PRODUCT_NAME")
    private String product_name;

    @Column(name = "PRODUCT_TYPE")
    private String product_type;

    @Column(name = "STORING_UNSTORING_AMOUNT")
    private int storing_unstoring_amount;

    public StoringUnStoring(LocalDateTime storing_unstoring_date, String storing_unstoring_type, String product_code, String product_name, String product_type, int storing_unstoring_amount) {
        this.storing_unstoring_date = storing_unstoring_date;
        this.storing_unstoring_type = storing_unstoring_type;
        this.product_code = product_code;
        this.product_name = product_name;
        this.product_type = product_type;
        this.storing_unstoring_amount = storing_unstoring_amount;
    }
}
