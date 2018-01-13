package com.winthishackathon.xd.blindspot.indoorwayMapPackage;

/**
 * Created by hub on 2018-01-13.
 */

public class FromToContainer {
    Long from;
    Long to;

    public FromToContainer(Long from, Long to){
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FromToContainer that = (FromToContainer) o;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        return to != null ? to.equals(that.to) : that.to == null;
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}

