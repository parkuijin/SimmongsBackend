package com.simmongs.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder // 객체 생성은 Setter 대신 Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 어노테이션
@Entity(name = "PRODUCT_TB")
public class Products { // 데이터를 저장할 Entity Class

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID", nullable = false)
    private Long product_id; // id

    @Column(name = "PRODUCT_CODE")
    private String product_code;

    @Column(name = "PRODUCT_NAME")
    private String product_name;

    @Column(name = "PRODUCT_AMOUNT", columnDefinition = "integer default 0")
    private int product_amount;

    @Column(name = "PRODUCT_UNIT")
    private String product_unit;

    @Column(name = "PRODUCT_TYPE")
    private String product_type;

    @Column(name = "PRODUCT_CREATION_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime product_creation_date;

    public Products(String product_code, String product_name, int product_amount, String product_unit, String product_type, LocalDateTime product_creation_date){
        this.product_code = product_code;
        this.product_name = product_name;
        this.product_amount = product_amount;
        this.product_unit = product_unit;
        this.product_type = product_type;
        this.product_creation_date = product_creation_date;
    }

}
