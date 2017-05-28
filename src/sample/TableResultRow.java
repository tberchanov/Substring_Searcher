package sample;

/**
 * Created by User on 26.10.2016.
 */
public class TableResultRow{
    private int avtomatResult;
    private int simpleResult;

    public TableResultRow(int avtomatResult, int simpleResult){
        this.avtomatResult = avtomatResult;
        this.simpleResult = simpleResult;
    }

    public int getAvtomatResult() {
        return avtomatResult;
    }

    public void setAvtomatResult(int avtomatResult) {
        this.avtomatResult = avtomatResult;
    }

    public int getSimpleResult() {
        return simpleResult;
    }

    public void setSimpleResult(int simpleResult) {
        this.simpleResult = simpleResult;
    }
}
