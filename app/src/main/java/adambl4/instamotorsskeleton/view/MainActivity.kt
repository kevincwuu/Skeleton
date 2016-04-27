package adambl4.instamotorsskeleton.view

import adambl4.instamotorsskeleton.logic.NotificationsModule
import android.R
import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.squareup.picasso.Picasso
import flow.Dispatcher
import flow.Flow
import flow.Traversal
import flow.TraversalCallback
import org.jetbrains.anko.*
import rx.Subscription
import rx.lang.kotlin.BehaviorSubject
import rx.subscriptions.CompositeSubscription

class MainActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context?) {
        val flowContext = Flow.configure(newBase, this)
                .dispatcher(MainActivityDispatcher(this))
                .defaultKey(MainActivityKeys.NotificationList())
                .install()
        super.attachBaseContext(flowContext);
    }
}

val Activity.frame: ViewGroup
    get() = this.findViewById(R.id.content) as ViewGroup

class MainActivityDispatcher(val mainActivity: MainActivity) : Dispatcher {
    override fun dispatch(traversal: Traversal?, callback: TraversalCallback?) {
        mainActivity.frame.removeAllViews()

        when (traversal?.destination?.top<MainActivityKeys>()) {
            is MainActivityKeys.NotificationList -> {
                mainActivity.frame.addView(NotificationsList(mainActivity, NotificationListBinding()))
            }
        }
    }
}

sealed class MainActivityKeys() {
    class NotificationList : MainActivityKeys()
}

class NotificationListBinding {
    val items = BehaviorSubject<List<NotificationsModule.NotificationDTO>>()

    init {
        NotificationsModule.query().subscribe { items.onNext(it) }
        NotificationsModule.Actions.buildFetchAction().invoke()
    }
}

class NotificationsList(context: Context, val binding: NotificationListBinding) : FrameLayout(context) {
    init {
        listView {
            val myAdapter = object : UpdatableArrayAdapter<NotificationsModule.NotificationDTO>() {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                    return NotificationItem(context, getItem(position)!!)
                }
            }
            adapter = myAdapter
            lifecycle {
                binding.items.subscribe { myAdapter.update(it) }
            }
        }
    }

    inner class NotificationItem(context: Context, dto: NotificationsModule.NotificationDTO) : FrameLayout(context) {
        init {
            linearLayout {
                padding = dip(20)
                orientation = LinearLayout.HORIZONTAL

                orientation = LinearLayout.VERTICAL
                textView("id = ${dto.id}")
                if (dto.jsonSchemaless != null) {
                    val name = dto.jsonSchemaless.get("name")?.asString
                    val potentialBuyerName = dto.jsonSchemaless.get("potential_buyer")?.asJsonObject?.get("name")?.asString
                    val title = dto.jsonSchemaless.get("title")?.asString
                    val listingId = dto.jsonSchemaless.get("listing")?.asJsonObject?.get("id")?.asString
                    val listingImage = dto.jsonSchemaless.get("listing")?.asJsonObject?.get("cover_image_url")?.asString

                    textView("name $name")
                    textView("potentialBuyerName $potentialBuyerName")
                    textView("title ${Html.fromHtml(title)}")

                    textView {
                        text = Html.fromHtml(title)
                    }
                    textView("listingId $listingId")

                    imageView {
                        Picasso.with(context).load(listingImage).into(this);
                    }
                }

            }
        }
    }
}


fun android.view.View.lifecycle(l: CompositeSubscription.() -> Subscription) {
    onAttachStateChangeListener {
        var s: Subscription? = null
        onViewAttachedToWindow {
            s = l.invoke(CompositeSubscription())
        }
        onViewDetachedFromWindow {
            s?.unsubscribe()
        }
    }
}

