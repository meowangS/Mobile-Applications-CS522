package edu.stevens.cs522.chatserver.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ChatDbAdapter {

    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "peers";

    private static final int DATABASE_VERSION = 5;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {


        private static final String DATABASE_CREATE_PEER =
                "create table " + PEER_TABLE + " ("
                        + PeerContract._ID + " integer primary key, "
                        + PeerContract.NAME + " text not null, "
                        + PeerContract.TIMESTAMP + " text not null, "
                        + PeerContract.ADDRESS + " text not null "
                        + ")";
        private static final String DATABASE_CREATE_MESSAGE =
                "create table " + MESSAGE_TABLE + " ("
                        + MessageContract._ID + " integer primary key, "
                        + MessageContract.MESSAGE_TEXT + " text not null, "
                        + MessageContract.TIMESTAMP + " text not null, "
                        + MessageContract.SENDER + " text not null, "
                        + MessageContract.SENDERID + " integer not null, "
                        + "foreign key(" + MessageContract.SENDERID + ") references " + PEER_TABLE + "(" + PeerContract._ID + ") "
                        + ")";
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
           db.execSQL(DATABASE_CREATE_PEER);
           db.execSQL(DATABASE_CREATE_MESSAGE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("ChatDbAdapter",
                  "Upgrading from version " + oldVersion + " to " + newVersion);

            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE);
            onCreate(db);
        }
    }


    public ChatDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public Cursor fetchAllMessages() {
        Cursor c = db.query(MESSAGE_TABLE, new String[] {MessageContract._ID, MessageContract.MESSAGE_TEXT, MessageContract.TIMESTAMP, MessageContract.SENDER, MessageContract.SENDERID},
                null, null, null, null, null);
        return c;
    }

    public Cursor fetchAllPeers() {
        Cursor c = db.query(PEER_TABLE, new String[] {PeerContract._ID, PeerContract.NAME, PeerContract.TIMESTAMP, PeerContract.ADDRESS},
                null, null, null, null, null);
        return c;
    }

    public Peer fetchPeer(long peerId) {
        String[] projection = {PeerContract._ID, PeerContract.NAME, PeerContract.TIMESTAMP, PeerContract.ADDRESS};
        String selection = PeerContract._ID + "=?";
        String[] selectionArgs = {Long.toString(peerId)};

        Cursor c = db.query(PEER_TABLE,
                            projection,
                            selection,
                            selectionArgs, null, null, null);
        Peer p = null;
        if (c.moveToFirst()){
            p = new Peer(c);
        }
        return p;
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        String[] projection = {MessageContract._ID, MessageContract.MESSAGE_TEXT, MessageContract.TIMESTAMP, MessageContract.SENDER, MessageContract.SENDERID};
        String selection = MessageContract.SENDERID + "=?";
        String[] selectionArgs = {Long.toString(peer.id)};

        Cursor c = db.query(MESSAGE_TABLE,
                            projection,
                            selection,
                            selectionArgs, null, null, null);
        return c;
    }

    public long persist(Message message) throws SQLException {
        ContentValues cv = new ContentValues();
        message.writeToProvider(cv);

        return db.insert(MESSAGE_TABLE,
                 null,
                  cv);
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     */
    public long persist(Peer peer) throws SQLException {
        ContentValues cv = new ContentValues();
        peer.writeToProvider(cv);

        String[] projection = {PeerContract._ID, PeerContract.NAME, PeerContract.TIMESTAMP, PeerContract.ADDRESS};
        String selection = PeerContract.NAME + "=?";
        String[] selectionArgs = {peer.name};

        Cursor c = db.query(PEER_TABLE,
                            projection,
                            selection,
                            selectionArgs, null, null, null);

        //Check if record exists. Insert if it doesn't, update if it does
        long retId;
        if(c.moveToFirst()){
            retId = MessageContract.getId(c);
            db.update(PEER_TABLE,
                      cv,
                      selection,
                      selectionArgs);
        }
        else{
            retId = db.insert(PEER_TABLE,
                    null,
                    cv);
        }
        return retId;
    }

    public void close() {
        db.close();
    }
}