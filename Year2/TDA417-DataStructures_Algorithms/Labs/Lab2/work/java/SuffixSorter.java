
// Abstract class for Suffix sorting algorithms.
public abstract class SuffixSorter {

    abstract void sort(SuffixArray suffixArray);

    PivotSelector pivotSelector;    
    public void setPivotSelector(PivotSelector pivotSelector) {
        this.pivotSelector = pivotSelector;
    }    
    
    boolean debug;
    public void setDebugging(boolean debug) {
        this.debug = debug;
        ProgressBar.visible = !debug;
    }
}

