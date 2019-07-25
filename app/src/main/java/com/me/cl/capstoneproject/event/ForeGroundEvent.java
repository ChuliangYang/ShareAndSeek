package com.me.cl.capstoneproject.event;

/**
 * Created by CL on 12/25/17.
 */

public class ForeGroundEvent {

    public static class MainPageTAG{
        public final String CurrentTitle;
        public final int CurrentTitlePosition;
        public final int CurrentTagPosition;

        public MainPageTAG(String currentTitle, int currentTitlePosition, int currentTagPosition) {
            CurrentTitle = currentTitle;
            CurrentTitlePosition = currentTitlePosition;
            CurrentTagPosition = currentTagPosition;
        }
    }
}
