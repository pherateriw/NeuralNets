package runmodels;


// https://svn.cms.waikato.ac.nz/svn/weka/branches/stable-3-2-1/weka/classifiers/neural/NeuralNode.java

public abstract class AbstractNode {
	
	
	public AbstractNode(){
		
	}
	

	abstract double calcOutput(double[] inputs);
	
	

}