package com.simmongs.department;

import com.simmongs.workorder.WorkOrderRepository;
import com.simmongs.workorder.WorkOrders;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final WorkOrderRepository workOrderRepository;

    @Transactional
    public Map<String, Object> DeleteDepartment(JSONObject obj) throws JSONException {
        Map<String, Object> response = new HashMap<>();

        JSONArray departmentList = obj.getJSONArray("departmentList");
        for (int i=0; i<departmentList.length(); i++) {
            JSONObject department = departmentList.getJSONObject(i);
            String departmentName = department.getString("departmentName");
            List<WorkOrders> workOrdersList = workOrderRepository.findByDepartmentName(departmentName);

            // 삭제하려는 부서에서 진행 중인 작업지시가 있는지 확인
            for (WorkOrders workOrders : workOrdersList) {
                if (workOrders.getWorkStatus().equals("진행")) {
                    response.put("success", false);
                    response.put("message", "해당 부서가 진행 중인 작업지시가 존재합니다.");
                    return response;
                }
            }

            // 준비 중인 작업지시는 중단으로 변경
            for (WorkOrders workOrders : workOrdersList) {
                if (workOrders.getWorkStatus().equals("준비")) {
                    workOrders.stopWorkOrder();
                }
            }

            // 부서 삭제
            Departments departments = departmentRepository.getByDepartmentName(departmentName);
            departmentRepository.delete(departments);
        }

        response.put("success", true);
        return response;
    }

    @Transactional
    public Map<String, Object> DepartmentRegistration(JSONObject obj) throws JSONException {
        Map<String, Object> response = new HashMap<>();

        String departmentName = obj.getString("departmentName");

        if (departmentName == null || departmentName.isEmpty()) {
            response.put("success", false);
            response.put("message", "빈칸을 입력해주세요.");
            return response;
        }

        if (departmentRepository.findByDepartmentName(departmentName).isPresent()){
            response.put("success", false);
            response.put("message", "부서명이 이미 존재합니다.");
            return response;
        }

        Departments departments = new Departments(departmentName);
        departmentRepository.save(departments);

        response.put("success", true);

        return response;
    }
}
