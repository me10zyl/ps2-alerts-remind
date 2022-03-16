package com.ps2site.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ServerConstants {

    private static List<String> serverNames = new ArrayList<>();

    public static List<String> getServerNames() {
        return serverNames;
    }

    public static final String SolTech = "SolTech";
    public static final String Emerald = "Emerald";
    public static final String Connery = "Connery";
    public static final String Miller = "Miller";
    public static final String Cobalt = "Cobalt";

    static {
        serverNames.add("Emerald");
        serverNames.add("SolTech");
        serverNames.add("Connery");
        serverNames.add("Miller");
        serverNames.add("Cobalt");
    }

}
