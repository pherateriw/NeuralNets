/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runmodels;

import java.util.ArrayList;

/**
 *
 * @author Janette
 */
public class RBFNode {
    ArrayList<Double> outweights = new ArrayList(); //weights to each output node from this RBF
    ArrayList<Double> oldweights = new ArrayList();//array to keep track of old weights so can revert if error is bigger
    ArrayList<Double> means = new ArrayList(); //center of the cluster as vector
    double activationOut;//output calculate Activation function
    double input;
    Distance dist = new Distance();
    public RBFNode(){
        
    }
    public double calculateActivation(ArrayList<Double> x, double var, int dim){
        //input = (double)(x.get(0))+(double)(x.get(1));
        double variance = 1/(2*Math.pow(var, 2));//calculates 1/2*sigma^2
        double error = Math.pow(dist.calculateDistance(x, means, dim),2);//Calculates the squared distance between the input and the mean
        return Math.exp(variance*error);//outputs e^variance*error
    }
    
}
