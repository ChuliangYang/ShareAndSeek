package com.me.cl.capstoneproject.event;

/**
 * Created by CL on 12/16/17.
 */
public class TitleChangeEvent {

    //MainActivity to CommercialListActivity
    public static class FromMainActivity {
        public final String NewTitle;
        public final int NewPosition;

        public FromMainActivity(String newTitle, int newPosition) {
            NewTitle = newTitle;
            NewPosition = newPosition;
        }

    }

    //CommercialListActivity to MainActivity

    public static class FromCommercialListActivity {
        public final String NewTitle;
        public final int NewPosition;

        public FromCommercialListActivity(String newTitle, int newPosition) {
            NewTitle = newTitle;
            NewPosition = newPosition;
        }

    }

    public static class ToMainPageTags{
        public final String CurrentTitle;
        public final int CurrentPosition;

        public ToMainPageTags(String currentTitle, int currentPosition) {
            CurrentTitle = currentTitle;
            CurrentPosition = currentPosition;
        }
    }

}
