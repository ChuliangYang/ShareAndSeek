package com.me.cl.capstoneproject.event;

/**
 * Created by CL on 1/7/18.
 */

public class AnimEvent {
    public int duration;
    public int id;
    public boolean reverse;

    public AnimEvent(int duration, int id,boolean reverse) {
        this.duration = duration;
        this.id = id;
        this.reverse=reverse;
    }

    public AnimEvent() {
    }
}
