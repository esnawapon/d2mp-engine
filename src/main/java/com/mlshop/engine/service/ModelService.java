package com.mlshop.engine.service;

import com.mlshop.engine.Constant;
import com.mlshop.engine.model.MetaData;
import com.mlshop.engine.model.PredictionResult;
import com.mlshop.engine.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.rules.M5Rules;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.util.List;

@Service
public class ModelService {
    @Autowired MetaDataService metaDataService;
    public PredictionResult createModel(String trainFileName) throws Exception {
        String trainFilePath = Constant.DIR_NAME_ARFF + "/" + trainFileName;
        String predictFilePath = Constant.FILE_NAME_MAIN_PREDICT;
        Instances trainInst =  getInstances(trainFilePath);
        Instances predictInst =  getInstances(predictFilePath);
        M5Rules m5Rules = new M5Rules();
        m5Rules.buildClassifier(trainInst);
//        Evaluation evaluation = new Evaluation(trainInst);
        PredictionResult result = new PredictionResult();
        final List<MetaData.Item> items = metaDataService.getMetaData().getItems();
        // keep loop in same way as predict file generated
        int instanceNumber = 0;
        while (instanceNumber < predictInst.numInstances()) {
            for (MetaData.Item item: items) {
                for (String size: item.getSizeAndPrice().keySet()) {
                    Instance instance = predictInst.instance(instanceNumber);
                    Double predictedValue = m5Rules.classifyInstance(predictInst.instance(instanceNumber));
                    Record record = new Record(
                            instance.stringValue(0),
                            item.getIndex(),
                            item.getName(),
                            size,
                            predictedValue,
                            item.getSizeAndPrice().get(size)
                    );
                    result.addRecord(record);
                    instanceNumber++;
                }
            }

        }
        result.compute();
        return result;
    }

    private Instances getInstances(String filePath) throws Exception {
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(filePath));
        Instances instances = loader.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }
}
