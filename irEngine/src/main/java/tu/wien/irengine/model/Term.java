/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tu.wien.irengine.model;

/**
 *
 */
public class Term implements Comparable<Term> {

    private String termText;
    private double measureValue;

    public Term(String termText) {
        this.termText = termText;
    }

    public Term(String termText, double measureValue) {
        this.termText = termText;
        this.measureValue = measureValue;
    }

    public String getTermText() {
        return termText;
    }

    public double getMeasureValue() {
        return measureValue;
    }

    public void setMeasureValue(double measureValue) {
        this.measureValue = measureValue;
    }

    public int compareTo(Term o) {
        return o.getMeasureValue() == this.getMeasureValue() ? 0
                : o.getMeasureValue() < this.getMeasureValue() ? 1 : -1;
    }
}
