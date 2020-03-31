package com.mlshop.engine.controller;

import java.util.HashMap;
import java.util.Map;

import com.mlshop.engine.service.ArffService;
import com.mlshop.engine.service.PredictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired private ArffService arffService;
    @Autowired private PredictService predictService;

    public ResponseEntity success(Object object) {
        return new ResponseEntity<>(object == null ? "null" : object, HttpStatus.OK);
    }

    @GetMapping(path="/test")
    public ResponseEntity test() throws Exception {
        String fileName = arffService.transformAll();
        Map result = new HashMap();
        result.put("fileName", fileName);
        return success(result);
    }

    @GetMapping(path="/predict")
    public ResponseEntity predict() throws Exception {
        return success(predictService.predict());
    }

    @GetMapping(path="")
    public ResponseEntity status() {
        Map r = new HashMap();
        r.put("status", "up");
        return success(r);
    }
}
