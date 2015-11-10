package com.beintoo.wrappers;


import java.util.List;

public class PaginatedList<T> {
    String lastkey;
    Integer found;
    Integer total;
    List<T> list;

    public String getLastKey() {
        return lastkey;
    }

    public void setLastKey(String lastKey) {
        this.lastkey = lastKey;
    }

    public Integer getFound() {
        return found;
    }

    public void setFound(Integer found) {
        this.found = found;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
