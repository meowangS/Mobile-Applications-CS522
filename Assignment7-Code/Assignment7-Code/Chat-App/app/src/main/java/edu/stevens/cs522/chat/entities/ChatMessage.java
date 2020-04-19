package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable, Persistable {

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public long senderId;

    public ChatMessage(Cursor cursor) {
        id          = MessageContract.getId(cursor);
        seqNum      = MessageContract.getSequenceNumber(cursor);
        messageText = MessageContract.getMessageText(cursor);
        chatRoom    = MessageContract.getChatRoomName(cursor);
        timestamp   = MessageContract.getTimestamp(cursor);
        longitude   = MessageContract.getLongitude(cursor);
        latitude    = MessageContract.getLatitude(cursor);
        sender      = MessageContract.getSender(cursor);
        senderId    = MessageContract.getSenderId(cursor);
    }

    public ChatMessage(Parcel in) {
        id          = in.readLong();
        seqNum      = in.readLong();
        messageText = in.readString();
        chatRoom    = in.readString();
        timestamp   = (Date) in.readSerializable();
        longitude   = in.readDouble();
        latitude    = in.readDouble();
        sender      = in.readString();
        senderId    = in.readLong();
    }

    public ChatMessage(){}

    @Override
    public void writeToProvider(ContentValues out) {
        MessageContract.putSequenceNumber(out, seqNum);
        MessageContract.putMessageText(out, messageText);
        MessageContract.putChatRoomName(out, chatRoom);
        MessageContract.putTimestamp(out, timestamp);
        MessageContract.putLongitude(out, longitude);
        MessageContract.putLatitude(out, latitude);
        MessageContract.putSender(out, sender);
        MessageContract.putSenderId(out, senderId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(seqNum);
        dest.writeString(messageText);
        dest.writeString(chatRoom);
        dest.writeSerializable(timestamp);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(sender);
        dest.writeLong(senderId);
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {

        @Override
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }

    };

}
