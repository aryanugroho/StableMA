package code.sma.main;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import code.sma.dpncy.NetflixMovieLensDiscretizer;
import code.sma.recommender.RecConfigEnv;
import code.sma.recommender.Recommender;
import code.sma.recommender.ensemble.WEMAREC;
import code.sma.recommender.rank.SMARank;
import code.sma.recommender.standalone.GradientBoostedMA;
import code.sma.recommender.standalone.GroupSparsityMF;
import code.sma.recommender.standalone.RegularizedSVD;
import code.sma.recommender.standalone.StableMA;
import code.sma.util.StringUtil;

/**
 * stand-alone recommender algorithm Factory class
 * 
 * @author Chao.Chen
 * @version $Id: RecommenderFactory.java, v 0.1 2016年9月27日 下午1:18:06 Chao.Chen Exp $
 */
public final class RecommenderFactory {
    private RecommenderFactory() {
    };

    public static Recommender instance(String algName, RecConfigEnv rce) {
        int featureCount = ((Double) rce.get("FEATURE_COUNT_VALUE")).intValue();
        double lrate = (double) rce.get("LEARNING_RATE_VALUE");
        double regularized = (double) rce.get("REGULAIZED_VALUE");
        int maxIteration = ((Double) rce.get("MAX_ITERATION_VALUE")).intValue();

        int userCount = ((Double) rce.get("USER_COUNT_VALUE")).intValue();
        int itemCount = ((Double) rce.get("ITEM_COUNT_VALUE")).intValue();
        double maxValue = ((Double) rce.get("MAX_RATING_VALUE")).doubleValue();
        double minValue = ((Double) rce.get("MIN_RATING_VALUE")).doubleValue();
        boolean showProgress = (Boolean) rce.get("VERBOSE_BOOLEAN");

        if (StringUtil.equalsIgnoreCase(algName, "RegSVD")) {
            // Improving Regularized Singular Value Decomposition Collaborative Filtering
            return new RegularizedSVD(userCount, itemCount, maxValue, minValue, featureCount, lrate,
                regularized, 0, maxIteration, showProgress, rce);
        } else if (StringUtil.equalsIgnoreCase(algName, "SMA")) {
            // Stable Matrix Approximation
            return new StableMA(userCount, itemCount, maxValue, minValue, featureCount, lrate,
                regularized, 0, maxIteration, showProgress, rce);
        } else if (StringUtil.equalsIgnoreCase(algName, "GSMF")) {
            // Recommendation by Mining Multiple User Behaviors with Group Sparsity
            return new GroupSparsityMF(userCount, itemCount, maxValue, minValue, featureCount,
                maxIteration, 3, showProgress, rce);
        } else if (StringUtil.equalsIgnoreCase(algName, "WEMAREC")) {
            // WEMAREC: Accurate and Scalable Recommendation through Weighted and Ensemble Matrix Approximation
            String rootDir = (String) rce.get("ROOT_DIR");
            String[] cDirStrs = ((String) rce.get("CLUSTERING_SET")).split("\\,");

            Queue<String> clusterDirs = new LinkedList<String>();
            for (String cDirStr : cDirStrs) {
                String clusterDir = rootDir + cDirStr + File.separator;
                clusterDirs.add(clusterDir);
            }

            return new WEMAREC(userCount, itemCount, maxValue, minValue, featureCount, lrate,
                regularized, 0, maxIteration, showProgress, rce,
                new NetflixMovieLensDiscretizer(userCount, itemCount, maxValue, minValue),
                clusterDirs);
        } else if (StringUtil.equalsIgnoreCase(algName, "SMARank")) {
            // unpublished
            return new SMARank(userCount, itemCount, maxValue, minValue, featureCount, lrate,
                regularized, 0, maxIteration, showProgress, rce);
        } else if (StringUtil.equalsIgnoreCase(algName, "GBMA")) {
            return new GradientBoostedMA(userCount, itemCount, maxValue, minValue, featureCount,
                lrate, regularized, 0, maxIteration, showProgress, rce);
        } else {
            return null;
        }
    }
}
