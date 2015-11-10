package com.beintoo.wrappers;

import com.google.gson.Gson;

import java.util.List;

public class CardsContainer {

    private List<CardsInfoResponse> items;

    public List<CardsInfoResponse> getItems() {
        return items;
    }

    public void setItems(List<CardsInfoResponse> items) {
        this.items = items;
    }

    public int size() {
        return this.items.size();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
