import java.util.Random;

public class Perceptron {
    private double[] weights;
    private double learningRate;

    public Perceptron(int inputSize, double learningRate) {
        this.weights = new double[inputSize + 1]; // +1 for the bias
        this.learningRate = learningRate;
        initializeWeights();
    }

    private void initializeWeights() {
        Random random = new Random();
        for (int i = 0; i < weights.length; i++) {
            weights[i] = -0.5 + random.nextDouble(); // random value between -0.5 and 0.5
        }
    }

    public void trainEpoch(double[][] trainingData, int[] labels) {
        for (int i = 0; i < trainingData.length; i++) {
            int prediction = predict(trainingData[i]);
            int error = labels[i] - prediction;
            for (int j = 0; j < trainingData[i].length; j++) {
                weights[j] += learningRate * error * trainingData[i][j];
            }
            weights[weights.length - 1] += learningRate * error; // update the bias
        }
    }

    public int predict(double[] inputData) {
        double sum = 0.0;
        for (int i = 0; i < inputData.length; i++) {
            sum += inputData[i] * weights[i];
        }
        sum += weights[weights.length - 1]; // add the bias
        return (sum >= 0) ? 1 : 0;
    }

    public double calculateAccuracy(double[][] trainingData, int[] labels) {
        int correct = 0;
        for (int i = 0; i < trainingData.length; i++) {
            if (predict(trainingData[i]) == labels[i]) {
                correct++;
            }
        }
        return (double) correct / labels.length;
    }

    public double[] getWeights() {
        return weights;
    }
}
