package fr.devid.plantR.models

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.String

@IgnoreExtraProperties
data class UserTeam(
    var description: String = "",
    var image24: String = "",
    var image96: String = "",
    var name: String = ""
)

@IgnoreExtraProperties
data class Messages(
    var senderID: String = "",
    var senderName: String = "",
    var dateCreated: Long = 0,
    var text: String = "",
    var image: Boolean = false
)

@IgnoreExtraProperties
data class Channel(
    var team: Map<String, UserTeam> = emptyMap(),
    var messages: ArrayList<Messages>? = null,
    var title: String = ""
) {
    var id: String = ""
}

@IgnoreExtraProperties
data class ChannelFireBase(
    var team: Map<String, UserTeam> = emptyMap(),
    var messages: ArrayList<Messages> = arrayListOf(),
    var title: String = ""
)

@IgnoreExtraProperties
data class Channels(
    val channels: Map<String, Channel> = emptyMap()
)

@Singleton
class UsersRepository @Inject constructor() {

    companion object {
        private const val Channels = "channels"
        private const val Team = "team"
        private const val Title = "title"
        private const val Description = "description"
        private const val Image24 = "image24"
        private const val Image96 = "image96"
        private const val Name = "name"
        private const val ChannelImage = "channel.jpg"
        private const val Image = "image"
        private const val Messages = "messages"
    }

    private fun rootRepository(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference()
    }

    private fun rootStorage(): StorageReference {
        return FirebaseStorage.getInstance().getReference()
    }

    fun getChannelsRef(): DatabaseReference {
        return rootRepository()
    }

    fun getChannels(): DatabaseReference {
        return rootRepository().child(Channels)
    }

    fun getChannelRefBy(id: String): DatabaseReference {
        return rootRepository().child(Channels).child(id)
    }

    fun getChannelMessageRefBy(id: String): DatabaseReference {
        return rootRepository().child(Channels).child(id).child(Messages)
    }

    fun getSpecifiqueRef(id: String): DatabaseReference {
        return getChannelRefBy(id).child(Title)
    }

    fun getChannelPictureFor(id: String): StorageReference {
        return rootStorage().child(Channels).child(id).child(ChannelImage)
    }

    fun getChannelImageFromMessage(id: String, message: String): StorageReference {
        println("id="+id)
        println("message="+message)
        return rootStorage().child(Channels).child(id).child(Image).child(Messages).child(message + ".jpg")
    }
}