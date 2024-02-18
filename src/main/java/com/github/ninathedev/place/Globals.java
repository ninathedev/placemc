package com.github.ninathedev.place;

public class Globals {
    private static boolean placemode = true; // if true, players allowed to place blocks, not affected by breakmode
    private static boolean breakmode = true; // if true, players allowed to break blocks, not affected by placemode

    public static void setBreakOnly(boolean bo) {
        breakmode = bo;
    }

    public static boolean getBreakOnly()  {
        return breakmode;
    }

    public static void setPlaceMode(boolean po) {
        placemode = po;
    }

    public static boolean getPlaceMode()  {
        return placemode;
    }
}
