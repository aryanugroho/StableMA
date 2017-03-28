package code.sma.recmmd;

import code.sma.datastructure.Accumulator;

/**
 * regularizer
 * 
 * @author Chao.Chen
 * @version $Id: Regularizer.java, v 0.1 2017年3月9日 下午1:55:44 Chao.Chen Exp $
 */
public enum Regularizer {
                         L1, //L1-norm
                         L2, // L2-norm
                         L12; // Group-sparse norm

    /**
     * compute the regularization of the given parameter
     * 
     * @param accFactr      the accumulator of the latent factor
     * @param accId         the index within the accumulator
     * @param factrVal      the value of the latent factor
     * @return
     */
    public double reg(Accumulator accFactr, int accId, double factrVal) {
        switch (this) {
            case L1:
                return Math.signum(factrVal);
            case L2:
                return factrVal;
            case L12:
                return factrVal / accFactr.rm(accId);
            default:
                return 0.0d;
        }

    }
}
