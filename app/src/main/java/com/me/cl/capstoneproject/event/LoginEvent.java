package com.me.cl.capstoneproject.event;

/**
 * Created by CL on 12/26/17.
 */

public class LoginEvent {
    public static  class base{

    }

    public static class LogIn extends base{

    }
    public static class Failed extends base{
        public final String info;

        public Failed(String info) {
            this.info = info;
        }
    }
    public static class LogOut extends base{

    }
}
