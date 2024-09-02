package org.aksw.gerbil.database;

import java.util.Arrays;

public class IntByteArrayPair {

    public int first;
    public byte[] second;

    public IntByteArrayPair() {
    }

    public IntByteArrayPair(int first, byte[] second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public byte[] getSecond() {
        return second;
    }

    public void setSecond(byte[] second) {
        this.second = second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + first;
        result = prime * result + Arrays.hashCode(second);
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
        IntByteArrayPair other = (IntByteArrayPair) obj;
        if (first != other.first)
            return false;
        if (!Arrays.equals(second, other.second))
            return false;
        return true;
    }

}
