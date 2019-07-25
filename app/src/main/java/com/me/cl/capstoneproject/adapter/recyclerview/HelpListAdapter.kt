package com.me.cl.capstoneproject.adapter.recyclerview

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Animatable
import android.support.transition.AutoTransition
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.bean.HelpItem
import com.me.cl.capstoneproject.event.PermissionCheckEvent
import com.me.cl.capstoneproject.util.BaseUtils
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.makeCall
//import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.singleLine

/**
 * Created by CL on 11/8/17.
 */

class HelpListAdapter( val context: Context,  var helpItemBeans: MutableList<HelpItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     val requestOptions= RequestOptions().error(R.drawable.ic_head_avater).centerCrop()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HelpItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_help, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HelpItemViewHolder) {
            holder.run {
                helpItemBeans[position].let {
                    tvTitle.text=it.title
//                    tvAddress.text="${it.location?.street}  ${it.location?.city}  ${it.location?.state}"
                    tvAddress.text="${it.location?.street}  ${it.location?.city}"
                    tvSummary.text=it.summary
                    tvSummary.setSingleLine(true)
                    ivExpand.setImageResource(R.drawable.vector_arrow_down_to_up_accent)
                    rvPhotos.run {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = PhotoAlbumAdapter(context,it.photoBeanList)
                    }

                    Glide.with(context).setDefaultRequestOptions(requestOptions).load(it.head_avator).into(ivAvatar)
                    when (it.expandState){
                        null->tvSummary.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                            override fun onPreDraw(): Boolean {
                                tvSummary.viewTreeObserver.removeOnPreDrawListener(this)
                                val l = tvSummary.layout
                                if (l != null) {
                                    val lines = l.lineCount
                                    if (lines > 0) {
                                        if (l.getEllipsisCount(lines - 1) > 0) {
                                            ivExpand.visibility = View.VISIBLE
                                            it.expandState=2
                                        } else {
                                            ivExpand.visibility = View.GONE
                                            it.expandState=1
                                        }
                                    } else {
                                        ivExpand.visibility = View.GONE
                                        it.expandState=1
                                    }
                                } else {
                                    ivExpand.visibility = View.GONE
                                    it.expandState=1

                                }
                                return true

                            }


                        })
                        1-> ivExpand.visibility = View.GONE

                        2-> {
                            ivExpand.visibility = View.VISIBLE
                            tvSummary.setSingleLine(true)
                        }
                        3->{
                            ivExpand.visibility = View.VISIBLE
                            tvSummary.setSingleLine(false)
                        }
                    }

                }

                ivExpand.setOnClickListener {
                    if (helpItemBeans[position].expand != null) {
                        helpItemBeans[position].expand = !(helpItemBeans[position].expand!!)
                    } else {
                        helpItemBeans[position].expand=false
                    }

                    TransitionManager.beginDelayedTransition(cvRoot, TransitionSet().apply {
                        addTransition(AutoTransition())
                        duration=300
                        addListener(object: android.support.transition.Transition.TransitionListener {
                            override fun onTransitionEnd(transition: android.support.transition.Transition) {
                                removeListener(this)
                            }

                            override fun onTransitionResume(transition: android.support.transition.Transition) {
                            }

                            override fun onTransitionPause(transition: android.support.transition.Transition) {
                            }

                            override fun onTransitionCancel(transition: android.support.transition.Transition) {
                            }

                            override fun onTransitionStart(transition: android.support.transition.Transition) {
                                if (helpItemBeans[position].expand!!) {
                                    ivExpand.setImageDrawable(ContextCompat.getDrawable(this@HelpListAdapter.context, R.drawable.vector_arrow_up_to_down_accent))
                                    (ivExpand.drawable as Animatable).start()
                                } else {
                                    ivExpand.setImageDrawable(ContextCompat.getDrawable(this@HelpListAdapter.context, R.drawable.vector_arrow_down_to_up_accent))
                                    (ivExpand.drawable as Animatable).start()
                                }
                            }
                        })
                    })

                    tvSummary.setSingleLine(helpItemBeans[position].expand!!)
                    if (helpItemBeans[position].expand!!) {
                        helpItemBeans[position].expandState=2
                    } else {
                        helpItemBeans[position].expandState=3
                    }
                }

                if (helpItemBeans[position].contact?.phone?.isEmpty() == true) {
                    btnHelp.visibility=View.GONE
                } else {
                    btnHelp.visibility=View.VISIBLE
                }

                btnHelp.setOnClickListener {
                    MaterialDialog.Builder(this@HelpListAdapter.context)
                            .title("Help")
                            .content("Do you want to ${
                            if (helpItemBeans[position].person_name.isNullOrEmpty()) {
                                "make a phone call"
                            } else {
                                "call "+ helpItemBeans[position].person_name
                            }
                            }?")
                            .positiveText("Yes")
                            .onPositive { _, _ ->
                                if (BaseUtils.System.runtimePermissionCheck(this@HelpListAdapter.context as Activity, Manifest.permission.CALL_PHONE, Constant.MainPage.REQUEST_CODE_PERMISSION_CHECK_HELP)) {
                                    makePhoneCall(helpItemBeans[position].contact?.phone!!)
                                } else {
                                    EventBus.getDefault().postSticky(PermissionCheckEvent.PhoneCall.HelpListAdapterToActivity(helpItemBeans[position].contact?.phone!!))
                                }
                            }
                            .negativeText("No")
                            .show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (helpItemBeans != null) helpItemBeans.size else 0
    }

    fun makePhoneCall(number: String) {
        context.makeCall(number)
    }

    fun getDataSet():List<HelpItem>?{
        return helpItemBeans
    }

    inner class HelpItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.iv_avatar)
        lateinit var ivAvatar: CircleImageView
        @BindView(R.id.tv_title)
        lateinit var tvTitle: TextView
        @BindView(R.id.tv_address)
        lateinit var tvAddress: TextView
        @BindView(R.id.rv_photos)
        lateinit var rvPhotos: RecyclerView
        @BindView(R.id.iv_expand)
        lateinit var ivExpand: ImageView
        @BindView(R.id.tv_summary)
        lateinit var tvSummary: TextView
        @BindView(R.id.btn_help)
        lateinit var btnHelp: Button
        @BindView(R.id.cv_root)
        lateinit var cvRoot: CardView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
