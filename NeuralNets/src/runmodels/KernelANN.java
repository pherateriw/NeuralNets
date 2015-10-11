package runmodels;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class KernelANN {

    //private ArrayList<Double> inputs;
    private ArrayList<RBFNode> functions = new ArrayList();//the list of RBF functions
    private ArrayList<Double> outputs = new ArrayList();//ordinarily a single double but need an arbitrary number of outputs
    private ArrayList<Double> targets = new ArrayList();//list of outputs of Rosenbrock function, our ideal numbers
    private double eta = 0.1;//learning rate
    private Distance dist = new Distance();
    private double variance; //a constant representation of error in the dataset
    public double oldError;//Error with each training example
    public double newError; //Error after update
    //public double totalError;
    public int dim2 = 0;//number of dimensions in the data set, set at read data
    public int datasize = 0;//number of items in the data set

    public KernelANN() {
    }

    public void buildNetwork(int numInputs, int numOutputs, int numFunctions) {
        Random rand = new Random();
        double r = 0.0;
        for (int j = 0; j < numOutputs; j++) {//construct output nodes
            outputs.add(1.0);
        }
        for (int m = 0; m < numFunctions; m++) {//construct basis function nodes
            RBFNode node = new RBFNode(numOutputs);
            functions.add(node);
        }
    }

    public ArrayList<ArrayList> readData(String fname) {
        BufferedReader br = null;//read from data
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<ArrayList> input = new ArrayList();//list of examples in data set
        int count = 0;
        try {
            br = new BufferedReader(new FileReader(fname));
            while ((line = br.readLine()) != null) {
                String[] example = line.split(cvsSplitBy);
                dim2 = example.length;//set number of dimensions in dataset
                input.add(new ArrayList());
                for (int i = 0; i < dim2 - 1; i++) {
                    int x = Integer.parseInt(example[i]);//change input to integers
                    input.get(count).add(x);//add each dimension to list
                }
                dim2 = dim2 - 1;
                double x = Double.parseDouble(example[dim2]);//add each output to the target
                targets.add(x);
                count++;
            }
            datasize = count;//set the datasize so we know where to count to
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }

    public int trainNetwork(String fname, double var, int k, double threshold) {
        ArrayList<ArrayList> input = readData(fname);//initialize data
        kMeansClustering(k, input, datasize, var);//use input to cluster data
        oldError = (double) (Double.MAX_VALUE);//set initial error to maximum value
        int count2 = 0;
        while (oldError > threshold && count2 < Integer.MAX_VALUE) {//until either convergence, or a certain number of iterations
            for (int i = 0; i < datasize; i++) {//for each training example
                for (int j = 0; j < k; j++) {//for each RBF function
                    functions.get(j).activationOut
                            = functions.get(j).calculateActivation(input.get(i), variance, dim2);//input each dimension into each RBF and calculate activation
                }
                int sum = 0;
                for (int d = 0; d < dim2; d++) {
                    int n = (int) input.get(i).get(d);
                    sum += n;
                }
                //System.out.println(sum);
                //System.out.println(input.get(i));
                double oldOut = outputs.get(i);
                double newOut = generateOutputs();//generate the output for the training example
                updateAllWeights(targets.get(i), newOut, sum);//update all the weights using the new output
               //System.out.println(newOut);
                outputs.set(i, newOut);
                newError = calculateTotalError(targets, outputs);
                //System.out.println("New Error: " + newError + ", Old Error: " + oldError);
                if (newError <= oldError) {
                    
                    oldError = newError;
                } else {
                    outputs.set(i, oldOut);
                    oldError = calculateTotalError(targets, outputs);
                    revertWeights();
                }
                //System.out.println("Iteration: " + count2 + ", Total Error: " + totalError);
            }
            //totalError = calculateTotalError(targets, outputs);
            count2++;
            //System.out.println(outputs.size());
            //System.out.println(outputs);
            //System.out.println(targets);
            System.out.println("Iteration: " + count2 + ", Total Error: " + oldError);
        }
        return count2;
    }
    public double calculateTotalError(ArrayList target, ArrayList out){
        return dist.calculateDistance(target, out, 1);
    }
    public double calcError(ArrayList target, ArrayList out) {
        return dist.calculateDistance(target, out, 1);
    }

    public double calcError(double target, double out) {
        return dist.calculateDistance(target, out);
    }

    public void kMeansClustering(int k, ArrayList<ArrayList> in, int outDim, double var) {//input all examples and k
        KMeans kmeans = new KMeans();
        ArrayList<ArrayList> meanslist = kmeans.createClusters(k, in, dim2);
        buildNetwork(dim2, outDim, k);
        for (int i = 0; i < functions.size(); i++) {
            //System.out.println("means: " + meanslist.get(i));
            functions.get(i).means.addAll(meanslist.get(i));
        }
        variance = var;
    }

    public double updateIndWeight(double inweight, double input, double target, double observed) {
        double outweight = inweight;
        outweight += (target - observed) * eta * input;//multiply this by input
        return outweight;
    }

    public void updateAllWeights(double target, double output, double input) {
        double x = 0;
        for (int i = 0; i < functions.size(); i++) {//for each hidden node
            for (int j = 0; j < 1; j++) {
                functions.get(i).oldweights.set(j, functions.get(i).outweights.get(j));
                x = updateIndWeight(functions.get(i).outweights.get(j),
                        functions.get(i).activationOut, target, output);
                functions.get(i).outweights.set(j, x);
            }
        }
    }

    public double generateOutputs() {
        double sum = 0;
        for (int i = 0; i < 1; i++) {
            //for each output node calculate weighted sum of hidden layers
            for (int j = 0; j < functions.size(); j++) {//each hidden node has output
                sum += functions.get(j).activationOut * functions.get(j).outweights.get(i);
            }
        }
        return sum;
    }

    public void revertWeights() {
        //System.out.println("Weights reverted");
        for (int i = 0; i < functions.size(); i++) {
            for (int j = 0; j < 1; j++) {
                functions.get(i).outweights.set(j, functions.get(i).oldweights.get(j));
            }
        }
    }
}
