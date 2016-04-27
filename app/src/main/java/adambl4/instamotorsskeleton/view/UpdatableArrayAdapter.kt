package adambl4.instamotorsskeleton.view

import android.widget.BaseAdapter

/**
 * Created by Adambl4 on 27.04.2016.
 */


abstract class UpdatableArrayAdapter<T>() : BaseAdapter() {

    var items: List<T>? = null

    override fun getCount(): Int = items?.size ?: 0

    override fun getItemId(position: Int): Long = 0L

    override fun getItem(position: Int): T? = items?.get(position) ?: null

    fun update(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

}
