package com.mlshop.engine.service;

import com.mlshop.engine.Constant;
import com.mlshop.engine.model.PredictionResult;
import com.mlshop.engine.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.M5Rules;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelService {
    @Autowired MetaDataService metaDataService;
    public PredictionResult createModel(String trainFileName) throws Exception {
        System.out.println(trainFileName);
        String trainFilePath = Constant.DIR_NAME_ARFF + "/" + trainFileName;
        String predictFilePath = Constant.FILE_NAME_MAIN_PREDICT;
        Instances trainInst =  getInstances(trainFilePath);
        Instances predictInst =  getInstances(predictFilePath);
        M5Rules m5Rules = new M5Rules();
        m5Rules.buildClassifier(trainInst);
//        Evaluation evaluation = new Evaluation(trainInst);
        PredictionResult result = new PredictionResult();
        for (int i = 0; i < predictInst.numInstances(); i++) {
            Instance instance = predictInst.instance(i);
            Double predictedValue = m5Rules.classifyInstance(predictInst.instance(i));
            Record record = new Record(
                instance.stringValue(0),
                instance.stringValue(1),
                instance.stringValue(2),
                predictedValue
            );
            result.addRecord(record, metaDataService.getMetaData());
            result.compute();
        }
        return result;
    }

    private Instances getInstances(String filePath) throws IOException {
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(filePath));
        Instances instances = loader.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }
}
