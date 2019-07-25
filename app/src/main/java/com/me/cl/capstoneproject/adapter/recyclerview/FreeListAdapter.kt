package com.me.cl.capstoneproject.adapter.recyclerview

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Animatable
import android.support.constraint.ConstraintLayout
import android.support.transition.AutoTransition
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v4.content.ContextCompat.getDrawable
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.base.Constant.MainPage.REQUEST_CODE_PERMISSION_CHECK_FREE
import com.me.cl.capstoneproject.bean.FreeItem
import com.me.cl.capstoneproject.event.PermissionCheckEvent
import com.me.cl.capstoneproject.util.BaseUtils
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.makeCall
//import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.share
import org.jetbrains.anko.singleLine

/**
 * Created by CL on 11/8/17.
 */

class FreeListAdapter(val context: Context, var freeItemBeans: MutableList<FreeItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val requestOptions= RequestOptions().error(R.drawable.ic_head_avater).centerCrop()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FreeItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_free, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FreeItemViewHolder) {
            holder.run {
                freeItemBeans[position].let {
                    tvTitle.text = it.title
//                    tvAddress.text = "${it.location?.street}  ${it.location?.city}  ${it.location?.state}"
                    tvAddress.text = "${it.location?.street}  ${it.location?.city}"
                    tvSummary.text = it.summary
                    tvSummary.setSingleLine(true)
                    ivExpand.setImageResource(R.drawable.vector_arrow_down_to_up_accent)
                    rvPhotos.run {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        adapter = PhotoAlbumAdapter(context, it.photoBeanList)
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
                            tvSummary.setSingleLine(true)
                        }
                    }

                }

                ivExpand.setOnClickListener {
                    if (freeItemBeans[position].expand != null) {
                        freeItemBeans[position].expand = !(freeItemBeans[position].expand!!)
                    } else {
                        freeItemBeans[position].expand=false
                    }

                    TransitionManager.beginDelayedTransition(cvRoot,TransitionSet().apply {
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
                                if (freeItemBeans[position].expand!!) {
                                    //变到下
                                    ivExpand.setImageDrawable(getDrawable(this@FreeListAdapter.context,R.drawable.vector_arrow_up_to_down_accent))
                                    (ivExpand.drawable as Animatable).start()
                                } else {
                                    //变到上
                                    ivExpand.setImageDrawable(getDrawable(this@FreeListAdapter.context,R.drawable.vector_arrow_down_to_up_accent))
                                    (ivExpand.drawable as Animatable).start()
                                }
                            }
                        })
                    })

                    tvSummary.setSingleLine(freeItemBeans[position].expand!!)
                    if (freeItemBeans[position].expand!!) {
                        freeItemBeans[position].expandState=2
                    } else {
                        freeItemBeans[position].expandState=3
                    }
                }


                if (freeItemBeans[position].contact?.phone?.isEmpty() == true) {
                    btnContact.visibility=View.GONE
                } else {
                    btnContact.visibility=View.VISIBLE
                }

                btnContact.setOnClickListener {
                    MaterialDialog.Builder(this@FreeListAdapter.context)
                            .title("Contact")
                            .content("Do you want to ${
                            if (freeItemBeans[position].person_name.isNullOrEmpty()) {
                                "make a phone call"
                            } else {
                                "call "+ freeItemBeans[position].person_name
                            }
                            }?")
                            .positiveText("Yes")
                            .onPositive { _, _ ->
                                if (BaseUtils.System.runtimePermissionCheck(this@FreeListAdapter.context as Activity, Manifest.permission.CALL_PHONE, REQUEST_CODE_PERMISSION_CHECK_FREE)) {
                                    makePhoneCall(freeItemBeans[position].contact?.phone!!)
                                } else {
                                    EventBus.getDefault().postSticky(PermissionCheckEvent.PhoneCall.FreeListAdapterToActivity(freeItemBeans[position].contact?.phone!!))
                                }
                            }
                            .negativeText("No")
                            .show()
                }

                btnShare.setOnClickListener {
                    this@FreeListAdapter.context.share("${freeItemBeans[position].title}   address: ${freeItemBeans[position].location?.street} , ${freeItemBeans[position].location?.city} , ${freeItemBeans[position].location?.state}", freeItemBeans[position].title?:"")
                }

            }
        }
    }

    fun makePhoneCall(number: String) {
        this@FreeListAdapter.context.makeCall(number)
    }

    override fun getItemCount(): Int {
        return if (freeItemBeans != null) freeItemBeans.size else 0
    }

    fun setData(freeItemBeans: MutableList<FreeItem>) {
        this.freeItemBeans = freeItemBeans
    }
    fun getDataSet():List<FreeItem>?{
        return freeItemBeans
    }

    inner class FreeItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        @BindView(R.id.btn_contact)
        lateinit var btnContact: Button
        @BindView(R.id.btn_share)
        lateinit var btnShare: Button
        @BindView(R.id.fl_root)
        lateinit var flRoot: FrameLayout
        @BindView(R.id.cv_root)
        lateinit var cvRoot: CardView
        @BindView(R.id.cl_root)
        lateinit var clRoot: ConstraintLayout
        @BindView(R.id.fl_summary)
        lateinit var flSummary: FrameLayout

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
