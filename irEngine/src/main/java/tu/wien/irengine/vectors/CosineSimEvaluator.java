/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.vectors;

import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.contract.ITermVectorEvaluator;

/**
 *
 */
public class CosineSimEvaluator implements ITermVectorEvaluator {

    public double evaluate(ITermVector tv1, ITermVector tv2) {

        double numerator = 0, sum1 = 0, sum2 = 0;

        if (tv1.size() == 0 && tv2.size() == 0) {
            return 1.0;
        }
        if (tv1.size() == 0 || tv2.size() == 0) {
            return 0.0;
        }

        for (String term : tv1.termSet()) {
            numerator += tv1.get(term) * tv2.get(term);
            sum1 += tv1.get(term) * tv2.get(term);
        }

        for (String term : tv2.termSet()) {
            double a = tv2.get(term);
            sum2 += a * a;
        }

        return numerator / Math.sqrt(sum1 * sum2);
    }
}
