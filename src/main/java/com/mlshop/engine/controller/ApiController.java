package com.mlshop.engine.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.mlshop.engine.service.FileService;
import com.mlshop.engine.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired private FileService fileService;
    @Autowired private ModelService modelService;

    public ResponseEntity success(Object object) {
        return new ResponseEntity<>(object == null ? "null" : object, HttpStatus.OK);
    }

    @GetMapping(path="/test")
    public ResponseEntity test() throws Exception {
        String fileName = fileService.transformAll();
        Map result = new HashMap();
        result.put("fileName", fileName);
        return success(result);
    }

    @GetMapping(path="/predict")
    public ResponseEntity predict(@RequestParam String fileName) throws Exception {
        return success(modelService.createModel(fileName));
    }

    @GetMapping(path="/load/predict")
    public ResponseEntity loadThenPredict() throws Exception {
        String fileName = fileService.transformAll();
        return success(modelService.createModel(fileName));
    }



    @GetMapping(path="")
    public ResponseEntity status() {
        Map r = new HashMap();
        r.put("status", "up");
        return success(r);
    }
}
