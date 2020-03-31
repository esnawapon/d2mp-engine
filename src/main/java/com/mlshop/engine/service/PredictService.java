package com.mlshop.engine.service;

import com.mlshop.engine.Constant;
import com.mlshop.engine.model.MetaData;
import com.mlshop.engine.model.PredictionSummary;
import com.mlshop.engine.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.rules.M5Rules;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class PredictService {
    @Autowired MetaDataService metaDataService;
    public PredictionSummary predict() throws Exception {
        String predictFilePath = Constant.FILE_NAME_MAIN_PREDICT;
        Instances predictInst =  getInstances(predictFilePath);
        Classifier m5Rules = getM5Model();
        PredictionSummary result = new PredictionSummary();
        final List<MetaData.Item> items = metaDataService.getMetaData().getItems();
        // keep loop in same way as predict file generated
        int instanceNumber = 0;
        while (instanceNumber < predictInst.numInstances()) {
            for (MetaData.Item item: items) {
                for (String size: item.getSizeAndPrice().keySet()) {
                    Instance instance = predictInst.instance(instanceNumber);
                    Double predictedValue = m5Rules.classifyInstance(predictInst.instance(instanceNumber));
                    Double roundDownPredictedValue = Math.floor(predictedValue);
                    result.addItem(
                            instance.stringValue(0),
                            item.getIndex(),
                            item.getName(),
                            size,
                            item.getSizeAndPrice().get(size),
                            roundDownPredictedValue,
                            item.getSizeAndPrice().get(size) * roundDownPredictedValue
                    );
                    instanceNumber++;
                }
            }
        }
        write(result);
        return result;
    }

    private Instances getInstances(String filePath) throws Exception {
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(filePath));
        Instances instances = loader.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    private M5Rules getM5Model() throws Exception {
        try (FileInputStream fis = new FileInputStream(Constant.FILE_NAME_M5_MAIN_MODEL)) {
            return (M5Rules) SerializationHelper.read(fis);
        } catch (IOException e) {
            throw e;
        }

    }

    private void write(PredictionSummary predictionSummary) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String filePath = Constant.DIR_NAME_PREDICT + "/predict-" + sdf.format(Calendar.getInstance().getTime());
        String filePathJson = filePath + ".json";
        String filePathCsv = filePath + ".csv";
        FileUtils.writeReplaceJsonFile(filePathJson, predictionSummary);
        FileUtils.writeReplaceCsvFile(filePathCsv, predictionSummary);
    }
}
