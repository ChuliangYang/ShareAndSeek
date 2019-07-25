package com.me.cl.capstoneproject.event;

/**
 * Created by CL on 12/25/17.
 */

public class UploadCompleteEvent {

    public static class services{
        public boolean success;
        private int category;

        public services() {
        }
    }

    public static class CommercialService extends services{
        public CommercialService(boolean success) {
            this.success=success;
        }
    }
    public static class FreeService extends services{
        public FreeService(boolean success) {
            this.success=success;
        }
    }
    public static class HelpService extends services{
        public HelpService(boolean success) {
            this.success=success;
        }
    }

    public static class CommentPublish{
        public boolean success;
        public float addScore;
        public CommentPublish(boolean success,float addScore) {
            this.success = success;
            this.addScore=addScore;
        }
    }
}
