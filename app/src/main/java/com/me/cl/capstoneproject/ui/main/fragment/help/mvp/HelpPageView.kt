package com.me.cl.capstoneproject.ui.main.fragment.help.mvp

import android.os.Parcelable
import com.me.cl.capstoneproject.bean.HelpItem
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageView

/**
 * Created by CL on 11/3/17.
 */

interface HelpPageView:FreePageView {
    fun popHelpList(helpItems: List<HelpItem>, restore: Boolean?, rvState: Parcelable?)
}
