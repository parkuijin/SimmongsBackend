package com.simmongs.department;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "department")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final DepartmentService departmentService;

    @PostMapping("registration") // 부서 정보 등록
    public Map<String, Object> DepartmentRegistration(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return departmentService.DepartmentRegistration(obj);
    }

    @GetMapping("showAll") // 부서 정보 조회
    public List<Departments> ShowAllDepartments(){
        return departmentRepository.findAll();
    }

    @DeleteMapping("delete")
    public Map<String, Object> DeleteDepartment(@RequestBody String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        return departmentService.DeleteDepartment(obj);
    }

}
