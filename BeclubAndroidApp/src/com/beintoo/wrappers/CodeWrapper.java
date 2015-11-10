package com.beintoo.wrappers;

public class CodeWrapper {
    private String imagecode;
    private String alphanumeric;
    private String url;
    private CardCodeWrapper cardcode;
    private UrlCodeWrapper urlcode;

    public String getImagecode() {
        return imagecode;
    }

    public void setImagecode(String imagecode) {
        this.imagecode = imagecode;
    }

    public String getAlphanumeric() {
        return alphanumeric;
    }

    public void setAlphanumeric(String alphanumeric) {
        this.alphanumeric = alphanumeric;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CardCodeWrapper getCardcode() {
        return cardcode;
    }

    public void setCardcode(CardCodeWrapper cardcode) {
        this.cardcode = cardcode;
    }

    public UrlCodeWrapper getUrlcode() {
        return urlcode;
    }

    public void setUrlcode(UrlCodeWrapper urlcode) {
        this.urlcode = urlcode;
    }
}
