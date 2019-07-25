package com.me.cl.capstoneproject.ui.upload.help

import android.content.Context
import com.me.cl.capstoneproject.di.HelpUploadQL

import com.me.cl.capstoneproject.ui.upload.help.mvp.HelpUploadInteractor

import javax.inject.Inject

/**
 * Created by CL on 12/1/17.
 */

class HelpUploadInteractorImpl @Inject
constructor(@HelpUploadQL val context: Context) : HelpUploadInteractor
