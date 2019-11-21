package com.d2mp.engine.controller;

import com.d2mp.engine.model.PredictRequest;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;

import com.d2mp.engine.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired
    private PredictionService predictionService;

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
        boolean result = predictionService.predictWinResult(request);
        Map r = new HashMap();
        r.put("win", result);
        return success(r);
    }

    @PostMapping(path="/feedback")
    public ResponseEntity feedback(@Valid @RequestBody PredictRequest request) throws Exception {
        predictionService.recordFeedback(request);
        return success(null);
    }
}
