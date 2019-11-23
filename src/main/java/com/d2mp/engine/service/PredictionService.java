package com.d2mp.engine.service;

import com.d2mp.engine.model.PredictRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class PredictionService {
    private static final int DATA_DEFAULT = 1;
    private static final int DATA_MY_TEAM = 2;
    private static final int DATA_OP_TEAM = 0;

    @Value("${file.model}") private String fileModelPath;
    @Value("${file.template}") private String fileTemplatePath;
    @Value("${file.feedback}") private String fileFeedbackPath;
    private Logistic logisticModel;

    private Logistic getLogisticModel() throws Exception {
        if (logisticModel == null) {
            logisticModel = (Logistic) SerializationHelper.read(fileModelPath);
        }
        return logisticModel;
    }

    public boolean predictWinResult(PredictRequest request) throws Exception {
        try {
            ArffLoader loader = new ArffLoader();
            loader.setFile(new File(fileTemplatePath));
            Instances instances = loader.getDataSet();
            instances.setClassIndex(instances.numAttributes() - 1);
            Instance predictDataSet = instances.instance(0);

            // set all hero data to default (start and end are not hero data)
            for (int i = 1; i < instances.numAttributes() - 1; i++) {
                predictDataSet.setValue(i, DATA_DEFAULT);
            }
            predictDataSet.setValue(0, request.getMode());
            predictDataSet.setValue(request.getHero1(), DATA_MY_TEAM);
            predictDataSet.setValue(request.getHero2(), DATA_MY_TEAM);
            predictDataSet.setValue(request.getHero3(), DATA_MY_TEAM);
            predictDataSet.setValue(request.getHero4(), DATA_MY_TEAM);
            predictDataSet.setValue(request.getHero5(), DATA_MY_TEAM);
            predictDataSet.setValue(request.getHero6(), DATA_OP_TEAM);
            predictDataSet.setValue(request.getHero7(), DATA_OP_TEAM);
            predictDataSet.setValue(request.getHero8(), DATA_OP_TEAM);
            predictDataSet.setValue(request.getHero9(), DATA_OP_TEAM);
            predictDataSet.setValue(request.getHero10(), DATA_OP_TEAM);
            Double predictedValue = getLogisticModel().classifyInstance(predictDataSet);
            return predictedValue >= 1;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void recordFeedback(PredictRequest request) {
        if (request.getWin() != null) {
            int[] raw = new int[115];
            // set all hero data to default (start and end are not hero data)
            for (int i = 1; i < raw.length - 1; i++) {
                raw[i] = 0;
            }
            raw[0] = request.getMode();
            raw[raw.length - 1] = request.getWin() ? 1 : -1;
            raw[request.getHero1()] = 1;
            raw[request.getHero2()] = 1;
            raw[request.getHero3()] = 1;
            raw[request.getHero4()] = 1;
            raw[request.getHero5()] = 1;
            raw[request.getHero6()] = -1;
            raw[request.getHero7()] = -1;
            raw[request.getHero8()] = -1;
            raw[request.getHero9()] = -1;
            raw[request.getHero10()] = -1;
            String rawStr = Arrays.stream(raw)
                    .mapToObj(each -> String.valueOf(each))
                    .collect(Collectors.joining(","));

            try (FileWriter writer = new FileWriter(fileFeedbackPath, true)) {
                writer.write("\n" + rawStr);
            } catch (Exception e) {
                System.out.println("Cannot write feedback");
            }
        }
    }
}
