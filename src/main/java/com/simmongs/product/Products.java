package com.simmongs.product;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Builder // 객체 생성은 Setter 대신 Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 어노테이션
@Entity(name = "product")
public class Products { // 데이터를 저장할 Entity Class

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long product_id;

    private String product_code;
    private String product_name;
    private int product_amount;
    private String product_unit;
    private String product_type;
    private String product_creation_date;

    public Products(String product_code, String product_name, int product_amount, String product_unit, String product_type, String product_creation_date){
        this.product_code = product_code;
        this.product_name = product_name;
        this.product_amount = product_amount;
        this.product_unit = product_unit;
        this.product_type = product_type;
        this.product_creation_date = product_creation_date;
    }
}
