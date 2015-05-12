package org.aksw.gerbil.database;

public class IntDoublePair {

    public int first;
    public double second;

    public IntDoublePair() {
    }

    public IntDoublePair(int first, double second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public double getSecond() {
        return second;
    }

    public void setSecond(double second) {
        this.second = second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + first;
        long temp;
        temp = Double.doubleToLongBits(second);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IntDoublePair other = (IntDoublePair) obj;
        if (first != other.first)
            return false;
        if (Double.doubleToLongBits(second) != Double.doubleToLongBits(other.second))
            return false;
        return true;
    }
}
