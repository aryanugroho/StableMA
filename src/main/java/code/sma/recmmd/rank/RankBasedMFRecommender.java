package code.sma.recmmd.rank;

import code.sma.datastructure.MatlabFasionSparseMatrix;
import code.sma.recmmd.RecConfigEnv;
import code.sma.recmmd.standalone.MFRecommender;
import code.sma.util.EvaluationMetrics;

/**
 * Matrix Factorization Methods in application of Top-N Problem
 * 
 * @author Chao.Chen
 * @version $Id: RankBasedMFRecommender.java, v 0.1 2016年9月30日 上午10:49:35 Chao.Chen Exp $
 */
public class RankBasedMFRecommender extends MFRecommender {
    /** Top-N recommendations*/
    protected int                      topN;
    /** Training data*/
    protected MatlabFasionSparseMatrix rateMatrix;
    /**  serialVersionUID */
    private static final long          serialVersionUID = 1L;

    /*========================================
     * Constructors
     *========================================*/
    public RankBasedMFRecommender(RecConfigEnv rce) {
        super(rce);
        topN = ((Double) rce.get("TOP_N_VALUE")).intValue();
    }

    /** 
     * @see code.sma.recmmd.standalone.MFRecommender#buildModel(code.sma.datastructure.MatlabFasionSparseMatrix, code.sma.datastructure.MatlabFasionSparseMatrix)
     */
    @Override
    public void buildModel(MatlabFasionSparseMatrix rateMatrix, MatlabFasionSparseMatrix tMatrix) {
        super.buildModel(rateMatrix, tMatrix);
        this.rateMatrix = rateMatrix;
    }

    /** 
     * @see code.sma.recmmd.standalone.MFRecommender#evaluate(code.sma.datastructure.MatlabFasionSparseMatrix)
     */
    @Override
    public EvaluationMetrics evaluate(MatlabFasionSparseMatrix testMatrix) {
        return new EvaluationMetrics(this, testMatrix, rateMatrix, topN);
    }

    /** 
     * @see code.sma.recmmd.standalone.MFRecommender#predict(int, int)
     */
    @Override
    public double predict(int u, int i) {
        return offset + userDenseFeatures.innerProduct(u, i, itemDenseFeatures, false);
    }

}
