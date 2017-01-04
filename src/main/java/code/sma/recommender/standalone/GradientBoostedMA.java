package code.sma.recommender.standalone;

import code.sma.datastructure.DenseVector;
import code.sma.datastructure.MatlabFasionSparseMatrix;
import code.sma.recommender.RecConfigEnv;

/**
 * Gradient boosted matrix approximation for collaborative filtering
 * 
 * @author Chao.Chen
 * @version $Id: GradientBoostedMA.java, v 0.1 2017年1月4日 上午10:18:35 Chao.Chen Exp $
 */
public class GradientBoostedMA extends MatrixFactorizationRecommender {
    /** SerialVersionNum */
    private static final long serialVersionUID = 1L;

    /*========================================
     * Constructors
     *========================================*/
    /**
     * Construct a matrix-factorization-based model with the given data.
     * 
     * @param uc The number of users in the dataset.
     * @param ic The number of items in the dataset.
     * @param max The maximum rating value in the dataset.
     * @param min The minimum rating value in the dataset.
     * @param fc The number of features used for describing user and item profiles.
     * @param lr Learning rate for gradient-based or iterative optimization.
     * @param r Controlling factor for the degree of regularization. 
     * @param m Momentum used in gradient-based or iterative optimization.
     * @param iter The maximum number of iterations.
     * @param verbose Indicating whether to show iteration steps and train error.
     * @param rce The recommender's specific parameters
     */
    public GradientBoostedMA(int uc, int ic, double max, double min, int fc, double lr, double r,
                             double m, int iter, boolean verbose, RecConfigEnv rce) {
        super(uc, ic, max, min, fc, lr, r, m, iter, verbose, rce);
        avgUser = (DenseVector) rce.get("AVG_USER");
        avgItem = (DenseVector) rce.get("AVG_ITEM");
    }

    /** 
     * @see code.sma.recommender.standalone.MatrixFactorizationRecommender#buildModel(code.sma.datastructure.MatlabFasionSparseMatrix, code.sma.datastructure.MatlabFasionSparseMatrix)
     */
    @Override
    public void buildModel(MatlabFasionSparseMatrix rateMatrix, MatlabFasionSparseMatrix tMatrix) {
        super.buildModel(rateMatrix, tMatrix);

        // Gradient Descent:
        int round = 0;
        int rateCount = rateMatrix.getNnz();
        double prevErr = 99999;
        double currErr = 9999;

        int[] uIndx = rateMatrix.getRowIndx();
        int[] iIndx = rateMatrix.getColIndx();
        double[] Auis = rateMatrix.getVals();

        boolean isCollaps = false;
        while (Math.abs(prevErr - currErr) > 0.0001 && round < maxIter && !isCollaps) {
            double sum = 0.0;

            for (int numSeq = 0; numSeq < rateCount; numSeq++) {
                int u = uIndx[numSeq];
                int i = iIndx[numSeq];

                //global model
                double AuiReal = Auis[numSeq];
                double AuiEst = userDenseFeatures.innerProduct(u, i, itemDenseFeatures, true);
                sum += lossFunction.diff(AuiReal, AuiEst);

                double deriWRTp = lossFunction.dervWRTPrdctn(AuiReal, AuiEst);
                for (int s = 0; s < featureCount; s++) {
                    double Fus = userDenseFeatures.getValue(u, s);
                    double Gis = itemDenseFeatures.getValue(i, s);

                    //global model updates
                    userDenseFeatures.setValue(u, s,
                        Fus + learningRate * (-deriWRTp * Gis - regularizer * Fus), true);
                    itemDenseFeatures.setValue(i, s,
                        Gis + learningRate * (-deriWRTp * Fus - regularizer * Gis), true);
                }
            }

            prevErr = currErr;
            currErr = Math.sqrt(sum / rateCount);

            round++;

            // Show progress:
            isCollaps = recordLoggerAndDynamicStop(round, tMatrix, currErr);
        }

    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Param: FC: " + featureCount + " LR: " + learningRate + " R: " + regularizer
               + " ALG[GBMA]";
    }
}
