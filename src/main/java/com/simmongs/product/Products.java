package com.simmongs.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Getter
@Builder // 객체 생성은 Setter 대신 Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 어노테이션
@Entity(name = "PRODUCT_TB")
public class Products { // 데이터를 저장할 Entity Class

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID", nullable = false)
    private Long productId; // id

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_AMOUNT")
    @ColumnDefault("0")
    private Integer productAmount;

    @Column(name = "PRODUCT_UNIT")
    private String productUnit;

    @Column(name = "PRODUCT_TYPE")
    private String productType;

    @Column(name = "PRODUCT_CREATION_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime productCreationDate;

    public Products(String productCode, String productName, Integer productAmount, String productUnit, String productType, LocalDateTime productCreationDate) {
        this.productCode = productCode;
        this.productName = productName;
        this.productAmount = productAmount;
        this.productUnit = productUnit;
        this.productType = productType;
        this.productCreationDate = productCreationDate;
    }

    public void changeProductCode(String productCode){
        this.productCode = productCode;
    }

    public void changeProductName(String productName){
        this.productName = productName;
    }

    public void changeProductAmount(int productAmount){
        this.productAmount = productAmount;
    }

    public void changeProductUnit(String productUnit){
        this.productUnit = productUnit;
    }

    public void changeProductType(String productType){
        this.productType = productType;
    }

    public void update(String productName, Integer productAmount, String productUnit) {
        this.productName = productName;
        this.productAmount = productAmount;
        this.productUnit = productUnit;
    }

    public void amountSub(Integer productAmount) {
        this.productAmount -= productAmount;
    }

    public void amountAdd(Integer productAmount) {
        this.productAmount += productAmount;
    }
}
