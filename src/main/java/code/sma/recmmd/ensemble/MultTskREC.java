package code.sma.recmmd.ensemble;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import code.sma.recmmd.RecConfigEnv;
import code.sma.recmmd.standalone.GLOMA;
import code.sma.recmmd.standalone.MatrixFactorizationRecommender;
import code.sma.util.SerializeUtil;

/**
 * 
 * @author Chao.Chen
 * @version $Id: MultTskREC.java, v 0.1 2017年2月28日 下午12:32:54 Chao.Chen Exp $
 */
public class MultTskREC extends EnsembleMFRecommender {
    /** SerialVersionNum */
    protected static final long serialVersionUID = 1L;

    /*========================================
     * Model specific parameters
     *========================================*/
    /** the arrays containing random seeds*/
    private Queue<Long>         randSeeds;
    /** the sampling rate of randomized submatrix */
    private double              samplingRate;
    /** the path of the auxiliary model */
    private String              auxRcmmdPath;

    /*========================================
     * Constructors
     *========================================*/
    public MultTskREC(RecConfigEnv rce) {
        super(rce);
        this.samplingRate = (double) rce.get("SAMPLE_RATE_VALUE");
        this.auxRcmmdPath = (String) rce.get("AUXILIARY_RCMMD_MODEL_PATH");

        randSeeds = new LinkedList<Long>();
        {
            String[] randSds = ((String) rce.get("RANDOM_SEED_SET")).split(",|\t| ");
            for (String rand : randSds) {
                long seed = Long.valueOf(rand.trim());
                randSeeds.add(seed == -1 ? System.currentTimeMillis() : seed);
            }
        }
    }

    /** 
     * @see code.sma.thread.TaskMsgDispatcher#map()
     */
    @Override
    public Object map() {
        synchronized (MAP_MUTEX) {
            if (randSeeds.isEmpty()) {
                return null;
            } else {
                Random ran = new Random(randSeeds.poll().longValue());
                boolean[] raf = new boolean[userCount];
                for (int u = 0; u < userCount; u++) {
                    if (ran.nextFloat() < samplingRate) {
                        raf[u] = true;
                    }
                }

                boolean[] caf = new boolean[itemCount];
                for (int i = 0; i < itemCount; i++) {
                    if (ran.nextFloat() < samplingRate) {
                        caf[i] = true;
                    }
                }

                MatrixFactorizationRecommender auxRec = (MatrixFactorizationRecommender) SerializeUtil
                    .readObject(auxRcmmdPath);
                return new GLOMA(userCount, itemCount, maxValue, minValue, featureCount,
                    learningRate, regularizer, momentum, maxIter, true, lossFunction, raf, caf,
                    auxRec);
            }
        }
    }

}