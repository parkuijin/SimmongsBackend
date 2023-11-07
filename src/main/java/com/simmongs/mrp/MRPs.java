package com.simmongs.mrp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "MRP_TB")
public class MRPs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MRP_ID", nullable = false)
    private Long mrpId;

    @Column(name = "WORK_ORDER_ID")
    private String workOrderId;

    @Column(name = "NEEDED_PRODUCT_CODE")
    private String neededProductCode;

    @Column(name = "TOTAL_NEEDED_PRODUCT_AMOUNT")
    private int totalNeededProductAmount;

    @Column(name = "CURRENT_USED_PRODUCT_AMOUNT")
    private int currentUsedProductAmount;

    public MRPs(String workOrderId, String neededProductCode, int totalNeededProductAmount, int currentUsedProductAmount) {
        this.workOrderId = workOrderId;
        this.neededProductCode = neededProductCode;
        this.totalNeededProductAmount = totalNeededProductAmount;
        this.currentUsedProductAmount = currentUsedProductAmount;
    }

    public void currentUsedProductAmountAdd(int currentUsedProductAmount) {
        this.currentUsedProductAmount += currentUsedProductAmount;
    }

    public void currentUsedProductAmountSub(int currentUsedProductAmount) {
        this.currentUsedProductAmount -= currentUsedProductAmount;
    }

}
