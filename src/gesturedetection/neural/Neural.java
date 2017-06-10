package gesturedetection.neural;

import gesturedetection.common.Constants;
import gesturedetection.pca.SphericalPcaOutput;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.Perceptron;
import org.neuroph.util.TransferFunctionType;

import java.util.Date;

public class Neural {
    private static final String NETWORK_BASE_PATH = "c:/studia/AAATUTATUTA/";
    private static final String NETWORK_PATH = "c:/studia/AAATUTATUTA/NewNeuralNetwork3.nnet";

    private int inputCount;
    private int outputCount;

    private NeuralNetwork neuralNetwork;
    private DataSet trainingData;
    private long timestamp = 1111;//System.currentTimeMillis() / 1000;

    private String neuralNetworkFilePath;
    private String trainingDataFilePath;

    public Neural() {
        timestamp = new Date().getTime();
        this.neuralNetworkFilePath = NETWORK_BASE_PATH + "neural" + timestamp + ".nnet";
        this.trainingDataFilePath = NETWORK_BASE_PATH + "training" + timestamp + ".tset";
        inputCount = Constants.getInputCount();
        outputCount = (int) Math.floor(Math.log(Constants.GESTURES_COUNT - 1) / Math.log(2)) + 1;
        neuralNetwork = new Perceptron(inputCount, outputCount, TransferFunctionType.STEP);
        neuralNetwork.save(neuralNetworkFilePath);
        trainingData = new DataSet(inputCount, outputCount);
        trainingData.save(trainingDataFilePath);
    }

    public Neural(String neuralFileName) {
        neuralFileName = NETWORK_BASE_PATH + neuralFileName;
        trainingDataFilePath = NETWORK_BASE_PATH + neuralFileName.replace("neural", "training");
        neuralNetwork = NeuralNetwork.load(neuralFileName);
        trainingData = DataSet.load(trainingDataFilePath);
        this.inputCount = neuralNetwork.getInputsCount();
        this.inputCount = neuralNetwork.getOutputsCount();
    }

    public void addTrainingData(SphericalPcaOutput data) {
        double[] inputs = data.getJointsCoords();
        double[] outputs = new double[outputCount];
        for (int i = outputCount - 1; i >= 0; i--) {
            outputs[i] = (data.getOutput() & (1 << i)) != 0 ? 1 : 0;
        }
        DataSetRow row = new DataSetRow(inputs, outputs);
        trainingData.add(row);
        trainingData.save(trainingDataFilePath);
    }

    public void train() {
        System.out.println("UCZĘ");
        System.out.println(trainingData);
        neuralNetwork.learn(trainingData);
        neuralNetwork.save(neuralNetworkFilePath);
    }

    public double[] getRespone(double[] in) {
        neuralNetwork.setInput(in);
        neuralNetwork.calculate();
        return neuralNetwork.getOutput();
    }

}
