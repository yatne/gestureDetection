package gesturedetection.neural;

import gesturedetection.pca.SphericalPcaOutput;
import org.neuroph.contrib.samples.stockmarket.TrainingData;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.transfer.Step;
import org.neuroph.nnet.Perceptron;
import org.neuroph.util.TransferFunctionType;

public class Neural {
    private static final String NETWORK_PATH = "c:/studia/AAATUTATUTA/NewNeuralNetwork3.nnet";

    private int inputCount = 6;
    private int outputCount = 2;

    private NeuralNetwork neuralNetwork;
    private DataSet trainingData;
    private long timestamp = 1111 ;//System.currentTimeMillis() / 1000;

    public Neural() {
        neuralNetwork = new Perceptron(inputCount, outputCount, TransferFunctionType.STEP);
        neuralNetwork.save("c:/studia/AAATUTATUTA/neural" + timestamp + ".nnet");
        trainingData = new DataSet(inputCount, outputCount);
        trainingData.save("c:/studia/AAATUTATUTA/training" + timestamp + ".tset");
    }

    public Neural(String path) {
        neuralNetwork = NeuralNetwork.load(NETWORK_PATH);
        trainingData = DataSet.load(NETWORK_PATH);
    }

    public void addTrainingData(SphericalPcaOutput data) {
        double[] inputs = data.getJointsCoords();
        double[] outputs = new double[outputCount];
        for (int i = outputCount-1; i >= 0; i--) {
            outputs[i] = (data.getOutput() & (1 << i)) != 0 ? 1 : 0;
        }
        DataSetRow row = new DataSetRow(inputs,outputs);
        trainingData.add(row);
        trainingData.save("c:/studia/AAATUTATUTA/training" + timestamp + ".tset");
    }

    public void train(){
        //LearningRule a = neuralNetwork.getLearningRule();
        neuralNetwork.learn(trainingData);
        neuralNetwork.save("c:/studia/AAATUTATUTA/neural" + timestamp + ".nnet");
    }

    public double[] getRespone(double[] in) {
        neuralNetwork.setInput(in);
        neuralNetwork.calculate();
        return neuralNetwork.getOutput();
    }

}
