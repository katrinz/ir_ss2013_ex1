package tu.wien.irengine.vectors;

import tu.wien.irengine.contract.ITermVector;
import tu.wien.irengine.contract.ITermVectorCreator;

public abstract class AbstractVectorCreator implements ITermVectorCreator {

    private boolean normalize = false;
    private int maxSize = -1;
    private double lowerBound = 0.0;
    private double upperBound = 0.0;

    @Override
    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    @Override
    public void setMaxSize(int n) {
        maxSize = n;
    }

    @Override
    public void setBounds(double lower, double upper) {
        this.lowerBound = lower;
        this.upperBound = upper;
    }

    protected void cleanUp(ITermVector tv) {
        if (maxSize != -1) {
            tv.truncateTo(maxSize);
        }

        if (normalize) {
            tv.normalize();
        }

        if (lowerBound != 0 || upperBound != 0) {
            tv.limit(lowerBound, upperBound);
        }
    }
}
