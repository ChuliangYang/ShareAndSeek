package com.me.cl.capstoneproject.event;

/**
 * Created by CL on 12/23/17.
 */

public class PermissionCheckEvent {
    public static class PhoneCall{
        public static class FreeListAdapterToActivity{
            public final String number;

            public FreeListAdapterToActivity(String number) {
                this.number = number;
            }
        }
        public static class HelpListAdapterToActivity{
            public final String number;

            public HelpListAdapterToActivity(String number) {
                this.number = number;
            }
        }

        public static class MainActivityToFreeListFragment {
            public final String number;

            public MainActivityToFreeListFragment(String number) {
                this.number = number;
            }
        }

        public static class MainActivityToHelpListFragment {
            public final String number;

            public MainActivityToHelpListFragment(String number) {
                this.number = number;
            }
        }
    }

}
