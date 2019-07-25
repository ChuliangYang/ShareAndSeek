package com.me.cl.capstoneproject.event;

/**
 * Created by CL on 2/4/18.
 */

public class CommandEvent {
    public static class RefreshCommercialList{
        public boolean animate;

        public RefreshCommercialList(boolean animate) {
            this.animate = animate;
        }
    }
}
