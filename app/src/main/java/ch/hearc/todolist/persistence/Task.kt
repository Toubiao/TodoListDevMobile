package ch.hearc.todolist.persistence

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "tbl_task")
class Task(@PrimaryKey(autoGenerate = true)
           var id: Int,
           var title: String?,
           var description: String?,
           var tag:String?,
           var color: String?,
           var imgUri: String?,
           var date:String?,
           var isDone: Int) :Parcelable{


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt())


    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(tag)
        parcel.writeString(color)
        parcel.writeString(imgUri)
        parcel.writeString(date)
        parcel.writeInt(isDone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (tag?.hashCode() ?: 0)
        return result
    }


}