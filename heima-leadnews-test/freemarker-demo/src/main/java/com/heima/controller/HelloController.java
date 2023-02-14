package com.heima.controller;

import com.heima.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: xiaocai
 * @since: 2023/02/13/21:38
 */

@Controller
public class HelloController {

    @GetMapping("/basic")
    public String testHello(Model model){
        model.addAttribute("name","freemarker");


        Student student = new Student();
        student.setAge(18);
        student.setName("zs");
        model.addAttribute("stu",student);
        return "01-basic";
    }

    @GetMapping("/list")
    public String testList(Model model){
        Student s1 = new Student();
        s1.setAge(18);
        s1.setName("zs");

        Student s2 = new Student();
        s2.setAge(20);
        s2.setName("ls");

        List<Student> stus = new ArrayList<>();

        stus.add(s1);
        stus.add(s2);
        model.addAttribute("stus",stus);

        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("s1",s1);
        stuMap.put("s2",s2);
        model.addAttribute("stuMap",stuMap);

        return "02-list";
    }
}
