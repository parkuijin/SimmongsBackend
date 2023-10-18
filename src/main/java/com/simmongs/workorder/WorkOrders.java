package com.simmongs.workorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity(name = "WORK_ORDER_TB")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORK_ORDER_ID")
    private Long work_order_id;

    @Column(name = "DEPARTMENT_NAME")
    private String department_name;

    @Column(name = "WORK_SRART_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime work_start_date;

    @Column(name = "BOM_ID")
    private String bom_id;

    @Column(name = "WORK_TARGET_QUANTITY")
    private int work_target_quantity;

    @Column(name = "WORK_CURRENT_QUANTITY")
    @ColumnDefault("0")
    private int work_current_quantity;

    @Column(name = "WORK_DEADLINE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime work_deadline;

    @Column(name = "WORK_STATUS")
    private String work_status;

    public WorkOrders(String department_name, LocalDateTime work_start_date, String bom_id, int work_target_quantity, int work_current_quantity, LocalDateTime work_deadline, String work_status) {
        this.department_name = department_name;
        this.work_start_date = work_start_date;
        this.bom_id = bom_id;
        this.work_target_quantity = work_target_quantity;
        this.work_current_quantity = work_current_quantity;
        this.work_deadline = work_deadline;
        this.work_status = work_status;
    }

}
