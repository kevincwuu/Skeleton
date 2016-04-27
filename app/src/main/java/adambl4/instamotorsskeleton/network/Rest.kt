package adambl4.instamotorsskeleton.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import rx.Observable
import java.util.*

/**
 * Created by Adambl4 on 25.04.2016.
 */

data class NotificationResponse(
        val id: String,
        val created_at: Date,
        val seen_at: Date,
        val read_at: Date,
        val event_data: JsonObject
)

data class Listing(
        var id: String,
        var cover_image_url: String
)

data class PotentialBuyer(
        val name: String
)

data class EventData(
        val name: String,
        val potential_buyer: PotentialBuyer,
        val title: String,
        val listing: Listing
)


interface InstamotorsAPI {
    @GET("notifications")
    fun getNotificationList(): Observable<List<NotificationResponse>>

    companion object {
        fun build(): InstamotorsAPI {
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://api.instamotorlabs.com/v2/mp/debug/")
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create()))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(InstamotorsAPI::class.java);
        }
    }
}
