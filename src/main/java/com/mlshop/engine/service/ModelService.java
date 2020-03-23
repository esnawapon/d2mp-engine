package com.mlshop.engine.service;

import com.mlshop.engine.Constant;
import org.springframework.stereotype.Service;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.M5Rules;
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
    public List<String> createModel(String trainFileName) throws Exception {
        System.out.println(trainFileName);
        String trainFilePath = Constant.DIR_NAME_ARFF + "/" + trainFileName;
        String predictFilePath = Constant.FILE_NAME_MAIN_PREDICT;
        Instances trainInst =  getInstances(trainFilePath);
        Instances predictInst =  getInstances(predictFilePath);
        M5Rules m5Rules = new M5Rules();
        m5Rules.buildClassifier(trainInst);
//        Evaluation evaluation = new Evaluation(trainInst);
        List<String> predicted = new ArrayList<>();
        for (int i = 0; i < predictInst.numInstances(); i++) {
            Double predictedValue = m5Rules.classifyInstance(predictInst.instance(i));
            String str = "predicted value of " + predictInst.instance(i) + "      \t= " + predictedValue;
            System.out.println(str);
            predicted.add(str);
        }
        return predicted;
    }

    private Instances getInstances(String filePath) throws IOException {
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(filePath));
        Instances instances = loader.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }
}
