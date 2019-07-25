package com.me.cl.capstoneproject.event;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by CL on 1/7/18.
 */

public class StartEvent {
    public static class Activity{
        public Intent intent;
        public Bundle bundle;

        public enum activity{
            MainActivity
        }
        public activity target;
        public boolean withSharedAnim;

        public Activity(Intent intent, activity target, boolean withSharedAnim) {
            this.intent = intent;
            this.target = target;
            this.withSharedAnim = withSharedAnim;
        }

    }
}
