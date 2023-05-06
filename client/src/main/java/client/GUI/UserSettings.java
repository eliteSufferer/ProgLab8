package client.GUI;

import java.util.Locale;

public class UserSettings {
    private static UserSettings instance;
    private Locale selectedLocale;

    private UserSettings() {
    }

    public static UserSettings getInstance() {
        if (instance == null) {
            instance = new UserSettings();
        }
        return instance;
    }

    public Locale getSelectedLocale() {
        return selectedLocale;
    }

    public void setSelectedLocale(Locale selectedLocale) {
        this.selectedLocale = selectedLocale;
    }
}