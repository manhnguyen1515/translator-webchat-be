package com.translator.webchat.consts;

public enum SupportedLanguage {
    Vietnam ("vi"),
    Japanese ("ja"),
    English ("en");

    private final String name;
    SupportedLanguage(String name) {
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public static SupportedLanguage fromName(String name) {
        for (SupportedLanguage lang : SupportedLanguage.values()) {
            if (lang.name.equalsIgnoreCase(name)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unsupported language: " + name);
    }
}
