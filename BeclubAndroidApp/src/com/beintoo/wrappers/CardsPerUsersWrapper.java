package com.beintoo.wrappers;

import com.google.gson.Gson;

public class CardsPerUsersWrapper {

    private String id;
    private CardsContainer cards;
    private String _uri;
    private Integer maxCardAllowedProperty;

    public Integer getMaxCardAllowedProperty() {
        return maxCardAllowedProperty;
    }

    public void setMaxCardAllowedProperty(Integer maxCardAllowedProperty) {
        this.maxCardAllowedProperty = maxCardAllowedProperty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CardsContainer getCards() {
        return cards;
    }

    public void setCards(CardsContainer cards) {
        this.cards = cards;
    }

    public String get_uri() {
        return _uri;
    }

    public void set_uri(String _uri) {
        this._uri = _uri;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
