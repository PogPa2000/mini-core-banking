package com.example.pogpa.mini_core_banking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("init-project")
public class InitProject {
    @GetMapping("/test")
    public String test(){
        return "Hoang Van Long";
    }
}
