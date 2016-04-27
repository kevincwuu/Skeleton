package adambl4.instamotorsskeleton.logic

import adambl4.instamotorsskeleton.network.InstamotorsAPI
import adambl4.instamotorsskeleton.network.NotificationResponse
import adambl4.instamotorsskeleton.persistence.EventRaw
import adambl4.instamotorsskeleton.persistence.NotificationEntity
import adambl4.instamotorsskeleton.persistence.saveOrUpdate
import adambl4.instamotorsskeleton.utils.extensions.invoke
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.realm.Realm
import nl.komponents.kovenant.rx.toPromise
import org.funktionale.partials.invoke
import rx.Observable
import rx.lang.kotlin.BehaviorSubject
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Created by Adambl4 on 25.04.2016.
 */

object NotificationsModule {
    data class NotificationDTO(val id: String, val jsonSchemaless : JsonObject?)

    object Actions {
        fun buildFetchAction() = actionAsyncWrapper(fetchNotifications(), onSuccess = { EventBus.push(Events.NotificationsFetched()) })
    }

    sealed class Events {
        class NotificationsFetched : Events()
    }

    fun query(): Observable<List<NotificationDTO>> {
        val subject = BehaviorSubject<List<NotificationDTO>>()

        fun queryAndPush(subject: BehaviorSubject<List<NotificationDTO>>, realm: Realm = Injekt.get()){
            val entityToDTO = { entity: NotificationEntity -> NotificationDTO(entity.id, JsonParser().parse(entity.eventDataRaw?.json).asJsonObject) }
            realm
                    .allObjects(NotificationEntity::class.java)
                    .asObservable()
                    .flatMap { Observable.just(it.map(entityToDTO)) }
                    .subscribe {subject.onNext(it); }
        }

        queryAndPush(subject)
        EventBus.stream().subscribe {/*queryAndPush(subject) */}
        return subject.asObservable()
    }

    fun fetchNotifications(api: InstamotorsAPI = Injekt.get(), realm : Lazy<Realm> = Injekt.injectLazy()): () -> Unit {
        val loadFromServer = { api: InstamotorsAPI -> api.getNotificationList().subscribeOn(Schedulers.io()).toPromise() }
        val mapToEntity = { it: NotificationResponse -> NotificationEntity(it.id, it.created_at, it.seen_at, it.read_at, EventRaw(it.id, it.event_data.toString())) }
        val cacheEntity = { entity: NotificationEntity, realm: Realm -> realm.saveOrUpdate(entity) }

        return syncLocalWithRemote(
                loadFromServer(p = api),
                { list: List<NotificationResponse> -> list.map(mapToEntity) },
                { list: List<NotificationEntity> -> list.forEach(cacheEntity(p2 = realm.value)) })
    }
}












