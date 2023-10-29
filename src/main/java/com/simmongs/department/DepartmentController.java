package com.simmongs.department;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "department")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @PostMapping("registration") // 부서 정보 등록
    public Departments DepartmentRegistration(@RequestBody String json) throws JSONException {

        JSONObject obj = new JSONObject(json);
        String departmentName = obj.getString("departmentName");

        Departments departments = new Departments(departmentName);
        departmentRepository.save(departments);

        return departments;
    }

    @GetMapping("showAll") // 부서 정보 조회
    public List<Departments> ShowAllDepartments(){
        return departmentRepository.findAll();
    }

}
