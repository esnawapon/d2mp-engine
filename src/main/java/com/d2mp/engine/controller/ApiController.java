package com.d2mp.engine.controller;

import com.d2mp.engine.model.PredictRequest;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api")
public class ApiController {
    public ResponseEntity success(Object object) {
        return new ResponseEntity<>(object == null ? "null" : object, HttpStatus.OK);
    }

    @GetMapping(path="")
    public ResponseEntity status() {
        Map r = new HashMap();
        r.put("status", "up");
        return success(r);
    }

    @PostMapping(path="/predict")
    public ResponseEntity predict(@Valid @RequestBody PredictRequest request) throws Exception {
        Thread.sleep(1500);
        Map r = new HashMap();
        r.put("win", Math.random() > 0.5);
        r.put("correctness", Math.random());
        return success(r);
    }
}
