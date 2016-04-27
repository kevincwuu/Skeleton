package adambl4.instamotorsskeleton.persistence

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Adambl4 on 26.04.2016.
 */

open class EventRaw(
        @PrimaryKey open var eventId: String = "",
        open var json: String? = null
) : RealmObject()


open class NotificationEntity(
        @PrimaryKey open var id: String = "",
        open var createdAt : Date? = null,
        open var seenAt: Date? = null,
        open var readAt: Date? = null,
        open var eventDataRaw: EventRaw? = null
) : RealmObject()

fun<T : RealmObject> Realm.saveOrUpdate(any : T){
    beginTransaction();
    copyToRealmOrUpdate(any);
    commitTransaction();
}