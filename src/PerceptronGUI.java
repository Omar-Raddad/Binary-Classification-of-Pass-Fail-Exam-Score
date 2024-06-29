import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class PerceptronGUI {
    private Perceptron perceptron;
    private JTextField mathField, scienceField, englishField;
    private JTextField learningRateField, epochsField, goalField;
    private JLabel resultLabel, accuracyLabel;
    private ArrayList<double[]> trainingDataList = new ArrayList<>();
    private ArrayList<Integer> labelsList = new ArrayList<>();
    private JTextArea trainingDataArea;

    public PerceptronGUI() {
        JFrame frame = new JFrame("Perceptron Pass/Fail Predictor");
        frame.setSize(600, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel mathLabel = new JLabel("Math:");
        mathLabel.setBounds(20, 20, 80, 25);
        frame.add(mathLabel);

        mathField = new JTextField();
        mathField.setBounds(100, 20, 165, 25);
        frame.add(mathField);

        JLabel scienceLabel = new JLabel("Science:");
        scienceLabel.setBounds(20, 60, 80, 25);
        frame.add(scienceLabel);

        scienceField = new JTextField();
        scienceField.setBounds(100, 60, 165, 25);
        frame.add(scienceField);

        JLabel englishLabel = new JLabel("English:");
        englishLabel.setBounds(20, 100, 80, 25);
        frame.add(englishLabel);

        englishField = new JTextField();
        englishField.setBounds(100, 100, 165, 25);
        frame.add(englishField);

        JLabel learningRateLabel = new JLabel("Learning Rate:");
        learningRateLabel.setBounds(20, 140, 100, 25);
        frame.add(learningRateLabel);

        learningRateField = new JTextField();
        learningRateField.setBounds(130, 140, 135, 25);
        frame.add(learningRateField);

        JLabel epochsLabel = new JLabel("Epochs:");
        epochsLabel.setBounds(20, 180, 80, 25);
        frame.add(epochsLabel);

        epochsField = new JTextField();
        epochsField.setBounds(100, 180, 165, 25);
        frame.add(epochsField);

        JLabel goalLabel = new JLabel("Accuracy Goal:");
        goalLabel.setBounds(20, 220, 100, 25);
        frame.add(goalLabel);

        goalField = new JTextField();
        goalField.setBounds(130, 220, 135, 25);
        frame.add(goalField);

        JButton addButton = new JButton("Add Data");
        addButton.setBounds(280, 100, 100, 25);
        frame.add(addButton);

        JButton trainButton = new JButton("Train");
        trainButton.setBounds(280, 140, 100, 25);
        frame.add(trainButton);

        JButton predictButton = new JButton("Predict");
        predictButton.setBounds(100, 260, 80, 25);
        frame.add(predictButton);

        resultLabel = new JLabel("Result: ");
        resultLabel.setBounds(200, 300, 165, 25);
        frame.add(resultLabel);

        accuracyLabel = new JLabel("Accuracy: ");
        accuracyLabel.setBounds(20, 300, 200, 25);
        frame.add(accuracyLabel);

        trainingDataArea = new JTextArea();
        trainingDataArea.setBounds(20, 340, 440, 150);
        trainingDataArea.setEditable(false);
        frame.add(trainingDataArea);

        // Initialize with default data and load data from file
        initializeDefaultData();
        loadDataFromFile();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double mathScore = Double.parseDouble(mathField.getText());
                    double scienceScore = Double.parseDouble(scienceField.getText());
                    double englishScore = Double.parseDouble(englishField.getText());

                    if (mathScore < 0 || mathScore > 100 || scienceScore < 0 || scienceScore > 100 || englishScore < 0 || englishScore > 100) {
                        JOptionPane.showMessageDialog(frame, "Please enter scores between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int passFail = JOptionPane.showConfirmDialog(frame, "Did the student pass?", "Pass/Fail", JOptionPane.YES_NO_OPTION);
                    passFail = (passFail == JOptionPane.YES_OPTION) ? 1 : 0;

                    trainingDataList.add(new double[]{mathScore, scienceScore, englishScore});
                    labelsList.add(passFail);

                    trainingDataArea.append("Math: " + mathScore + ", Science: " + scienceScore + ", English: " + englishScore + ", Pass: " + (passFail == 1 ? "Yes" : "No") + "\n");
                    mathField.setText("");
                    scienceField.setText("");
                    englishField.setText("");

                    saveDataToFile();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        trainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double learningRate = Double.parseDouble(learningRateField.getText());
                    int epochs = Integer.parseInt(epochsField.getText());
                    double goal = Double.parseDouble(goalField.getText());
                    perceptron = new Perceptron(3, learningRate);

                    double[][] trainingData = trainingDataList.toArray(new double[0][0]);
                    int[] labels = labelsList.stream().mapToInt(i -> i).toArray();

                    double accuracy = 0;
                    for (int i = 0; i < epochs; i++) {
                        perceptron.trainEpoch(trainingData, labels);
                        accuracy = perceptron.calculateAccuracy(trainingData, labels);
                        if (accuracy >= goal) {
                            break;
                        }
                    }

                    accuracyLabel.setText("Accuracy: " + accuracy);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid learning rate, epochs, and goal.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        predictButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double mathScore = Double.parseDouble(mathField.getText());
                    double scienceScore = Double.parseDouble(scienceField.getText());
                    double englishScore = Double.parseDouble(englishField.getText());

                    if (mathScore < 0 || mathScore > 100 || scienceScore < 0 || scienceScore > 100 || englishScore < 0 || englishScore > 100) {
                        JOptionPane.showMessageDialog(frame, "Please enter scores between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int result = perceptron.predict(new double[]{mathScore, scienceScore, englishScore});
                    resultLabel.setText("Result: " + (result == 1 ? "Pass" : "Fail"));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    private void initializeDefaultData() {
        trainingDataList.add(new double[]{85.0, 90.0, 88.0});
        labelsList.add(1);
        trainingDataList.add(new double[]{70.0, 60.0, 65.0});
        labelsList.add(0);
        trainingDataList.add(new double[]{95.0, 92.0, 93.0});
        labelsList.add(1);
        trainingDataList.add(new double[]{50.0, 45.0, 55.0});
        labelsList.add(0);
        trainingDataList.add(new double[]{60.0, 65.0, 70.0});
        labelsList.add(0);
    }

    private void saveDataToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("training_data.txt"))) {
            for (int i = 0; i < trainingDataList.size(); i++) {
                double[] data = trainingDataList.get(i);
                int label = labelsList.get(i);
                writer.println(data[0] + "," + data[1] + "," + data[2] + "," + label);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                double[] data = new double[3];
                data[0] = Double.parseDouble(parts[0]);
                data[1] = Double.parseDouble(parts[1]);
                data[2] = Double.parseDouble(parts[2]);
                int label = Integer.parseInt(parts[3]);

                trainingDataList.add(data);
                labelsList.add(label);

                trainingDataArea.append("Math: " + data[0] + ", Science: " + data[1] + ", English: " + data[2] + ", Pass: " + (label == 1 ? "Yes" : "No") + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PerceptronGUI();
    }
}
