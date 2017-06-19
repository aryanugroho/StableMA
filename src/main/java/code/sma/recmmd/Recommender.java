package code.sma.recmmd;

import code.sma.core.AbstractMatrix;
import code.sma.model.AbstractModel;

/**
 * Abstract Class for Recommender Model
 * 
 * @author Chao Chen
 * @version $Id: Recommender.java, v 0.1 2015-6-8 下午6:59:37 Exp $
 */
public abstract class Recommender {
    /** Runtime environment*/
    public RuntimeEnv    runtimes;
    /** Resulting model */
    public AbstractModel model;

    /*========================================
     * Model Builder
     *========================================*/

    /**
     * Build a model with given training set.
     * 
     * @param train     training data
     * @param test      test data
     */
    public abstract void buildModel(AbstractMatrix train, AbstractMatrix test);

    /**
     * save model
     * 
     * @param fo    the output file
     */
    public abstract void saveModel(String fo);

    /**
     * load model 
     * 
     * @param fi    the input file
     */
    public abstract void loadModel(String fi);

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public abstract String toString();

}
