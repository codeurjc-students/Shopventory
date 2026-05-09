package es.codeurjc.shopventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SPAController {

    @GetMapping(value = {"/{path:[^\\.]*}"})
    public String forwardRoot() {
        return "forward:/index.html";
    }

    @GetMapping(value = {"/{path1:[^\\.]*}/{path2:[^\\.]*}", "/{path1:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}"})
    public String forwardNested() {
        return "forward:/index.html";
    }
}
