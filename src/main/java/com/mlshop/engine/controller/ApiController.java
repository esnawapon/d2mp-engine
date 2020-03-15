package com.mlshop.engine.controller;

import java.util.HashMap;
import java.util.Map;

import com.mlshop.engine.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired private FileService fileService;

    public ResponseEntity success(Object object) {
        return new ResponseEntity<>(object == null ? "null" : object, HttpStatus.OK);
    }

    @GetMapping(path="/test")
    public ResponseEntity test(@RequestParam String path) throws Exception {
        return success(fileService.transformAll());
    }

    @GetMapping(path="")
    public ResponseEntity status() {
        Map r = new HashMap();
        r.put("status", "up");
        return success(r);
    }
}
