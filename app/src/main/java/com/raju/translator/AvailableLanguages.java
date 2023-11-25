package com.raju.translator;

public class AvailableLanguages {
    private String languageCode;
    private String languageTitle;

    public AvailableLanguages(String languageCode, String languageTitle) {
        this.languageCode = languageCode;
        this.languageTitle = languageTitle;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageTitle() {
        return languageTitle;
    }

    public void setLanguageTitle(String languageTitle) {
        this.languageTitle = languageTitle;
    }
}
