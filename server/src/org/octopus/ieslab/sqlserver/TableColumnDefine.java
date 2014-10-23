package org.octopus.ieslab.sqlserver;


public class TableColumnDefine {
    public String name;
    public String type;
    public int dpSize;
    public String javaTp;

    @Override
    public String toString() {
        return String.format("Column : %s [%s(%d) - %s]", name, type, dpSize, javaTp);
    }
}
