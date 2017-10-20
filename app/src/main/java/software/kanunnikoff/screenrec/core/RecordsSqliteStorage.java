package software.kanunnikoff.screenrec.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import software.kanunnikoff.screenrec.model.Record;

/**
 * Created by Dmitry on 31.01.2016.
 */
public class RecordsSqliteStorage extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "screen_rec";
    private static final String TABLE_NAME = "records";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_OUTPUT_FORMAT = "output_format";
    private static final String COLUMN_OUTPUT_FILE = "output_file";
    private static final String COLUMN_AUDIO_ENCODER = "audio_encoder";
    private static final String COLUMN_AUDIO_ENCODING_BIT_RATE = "audio_encoding_bit_rate";
    private static final String COLUMN_AUDIO_SAMPLING_RATE = "audio_sampling_rate";
    private static final String COLUMN_AUDIO_CHANNELS = "audio_channels";
    private static final String COLUMN_VIDEO_ENCODER = "video_encoder";
    private static final String COLUMN_VIDEO_ENCODING_BIT_RATE = "video_encoding_bit_rate";
    private static final String COLUMN_VIDEO_FRAME_RATE = "video_frame_rate";
    private static final String COLUMN_VIDEO_SIZE = "video_size";
    private static final String COLUMN_IS_FAVORED = "is_favored";
    private static final String COLUMN_FILE_SIZE = "file_size";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_THUMBNAIL = "thumbnail";

    public RecordsSqliteStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                TABLE_NAME, COLUMN_ID, COLUMN_TITLE, COLUMN_OUTPUT_FORMAT, COLUMN_OUTPUT_FILE, COLUMN_AUDIO_ENCODER,
                COLUMN_AUDIO_ENCODING_BIT_RATE, COLUMN_AUDIO_SAMPLING_RATE, COLUMN_AUDIO_CHANNELS, COLUMN_VIDEO_ENCODER,
                COLUMN_VIDEO_ENCODING_BIT_RATE, COLUMN_VIDEO_FRAME_RATE, COLUMN_VIDEO_SIZE, COLUMN_IS_FAVORED,
                COLUMN_FILE_SIZE, COLUMN_DURATION, COLUMN_DATE, COLUMN_THUMBNAIL);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public ArrayList<Record> getAllRecords(Long start) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;

        if (start == 0) {
            cursor = db.query(TABLE_NAME,
                    new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_OUTPUT_FORMAT, COLUMN_OUTPUT_FILE, COLUMN_AUDIO_ENCODER,
                            COLUMN_AUDIO_ENCODING_BIT_RATE, COLUMN_AUDIO_SAMPLING_RATE, COLUMN_AUDIO_CHANNELS, COLUMN_VIDEO_ENCODER,
                            COLUMN_VIDEO_ENCODING_BIT_RATE, COLUMN_VIDEO_FRAME_RATE, COLUMN_VIDEO_SIZE, COLUMN_IS_FAVORED,
                            COLUMN_FILE_SIZE, COLUMN_DURATION, COLUMN_DATE, COLUMN_THUMBNAIL }, // SELECT
                    null, null,
                    null, null, COLUMN_DATE + " DESC", "10");
        } else {
            cursor = db.query(TABLE_NAME,
                    new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_OUTPUT_FORMAT, COLUMN_OUTPUT_FILE, COLUMN_AUDIO_ENCODER,
                            COLUMN_AUDIO_ENCODING_BIT_RATE, COLUMN_AUDIO_SAMPLING_RATE, COLUMN_AUDIO_CHANNELS, COLUMN_VIDEO_ENCODER,
                            COLUMN_VIDEO_ENCODING_BIT_RATE, COLUMN_VIDEO_FRAME_RATE, COLUMN_VIDEO_SIZE, COLUMN_IS_FAVORED,
                            COLUMN_FILE_SIZE, COLUMN_DURATION, COLUMN_DATE, COLUMN_THUMBNAIL }, // SELECT
                    String.format(Locale.getDefault(), "%s < ?", COLUMN_ID), new String[] { String.valueOf(start) },
                    null, null, COLUMN_DATE + " DESC", "10");
        }

        if (cursor != null) {
            if (!cursor.moveToFirst() || cursor.getCount() == 0) {
                return new ArrayList<>();
            }

            ArrayList<Record> records = new ArrayList<>();

            do {
                Record record = new Record(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getInt(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        cursor.getString(15),
                        cursor.getBlob(16)
                );

                records.add(record);
            } while (cursor.moveToNext());

            cursor.close();

            return records;
        }

        return null;
    }

    public ArrayList<Record> getFavoredRecords(Long start) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;

        if (start == 0) {
            cursor = db.query(TABLE_NAME,
                    new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_OUTPUT_FORMAT, COLUMN_OUTPUT_FILE, COLUMN_AUDIO_ENCODER,
                            COLUMN_AUDIO_ENCODING_BIT_RATE, COLUMN_AUDIO_SAMPLING_RATE, COLUMN_AUDIO_CHANNELS, COLUMN_VIDEO_ENCODER,
                            COLUMN_VIDEO_ENCODING_BIT_RATE, COLUMN_VIDEO_FRAME_RATE, COLUMN_VIDEO_SIZE, COLUMN_IS_FAVORED,
                            COLUMN_FILE_SIZE, COLUMN_DURATION, COLUMN_DATE, COLUMN_THUMBNAIL }, // SELECT
                    String.format(Locale.getDefault(), "%s = ?", COLUMN_IS_FAVORED), new String[] { "1" },
                    null, null, COLUMN_DATE + " DESC", "10");
        } else {
            cursor = db.query(TABLE_NAME,
                    new String[] { COLUMN_ID, COLUMN_TITLE, COLUMN_OUTPUT_FORMAT, COLUMN_OUTPUT_FILE, COLUMN_AUDIO_ENCODER,
                            COLUMN_AUDIO_ENCODING_BIT_RATE, COLUMN_AUDIO_SAMPLING_RATE, COLUMN_AUDIO_CHANNELS, COLUMN_VIDEO_ENCODER,
                            COLUMN_VIDEO_ENCODING_BIT_RATE, COLUMN_VIDEO_FRAME_RATE, COLUMN_VIDEO_SIZE, COLUMN_IS_FAVORED,
                            COLUMN_FILE_SIZE, COLUMN_DURATION, COLUMN_DATE, COLUMN_THUMBNAIL }, // SELECT
                    String.format(Locale.getDefault(), "%s = ? AND %s < ?", COLUMN_IS_FAVORED, COLUMN_ID), new String[] { "1", String.valueOf(start) },
                    null, null, COLUMN_DATE + " DESC", "10");
        }

        if (cursor != null) {
            if (!cursor.moveToFirst() || cursor.getCount() == 0) {
                return new ArrayList<>();
            }

            ArrayList<Record> records = new ArrayList<>();

            do {
                Record record = new Record(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getInt(12),
                        cursor.getString(13),
                        cursor.getString(14),
                        cursor.getString(15),
                        cursor.getBlob(16)
                );

                records.add(record);
            } while (cursor.moveToNext());

            cursor.close();

            return records;
        }

        return null;
    }

    public Long insertRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, record.getTitle());
        values.put(COLUMN_OUTPUT_FORMAT, record.getOutputFormat());
        values.put(COLUMN_OUTPUT_FILE, record.getOutputFile());
        values.put(COLUMN_AUDIO_ENCODER, record.getAudioEncoder());
        values.put(COLUMN_AUDIO_ENCODING_BIT_RATE, record.getAudioEncodingBitRate());
        values.put(COLUMN_AUDIO_SAMPLING_RATE, record.getAudioSamplingRate());
        values.put(COLUMN_AUDIO_CHANNELS, record.getAudioChannels());
        values.put(COLUMN_VIDEO_ENCODER, record.getVideoEncoder());
        values.put(COLUMN_VIDEO_ENCODING_BIT_RATE, record.getVideoEncodingBitRate());
        values.put(COLUMN_VIDEO_FRAME_RATE, record.getVideoFrameRate());
        values.put(COLUMN_VIDEO_SIZE, record.getVideoSize());
        values.put(COLUMN_IS_FAVORED, record.isFavored());
        values.put(COLUMN_FILE_SIZE, record.getFileSize());
        values.put(COLUMN_DURATION, record.getDuration());
        values.put(COLUMN_DATE, record.getDate());
        values.put(COLUMN_THUMBNAIL, record.getThumbnail());

        long row = db.insertOrThrow(TABLE_NAME, null, values);
        db.close();

        return row;
    }

    public boolean deleteRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        long row = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { String.valueOf(record.getId()) });
        db.close();

        return row != -1;
    }

    @Deprecated
    public int updateItem(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, record.getTitle());
        values.put(COLUMN_OUTPUT_FORMAT, record.getOutputFormat());
        values.put(COLUMN_OUTPUT_FILE, record.getOutputFile());
        values.put(COLUMN_AUDIO_ENCODER, record.getAudioEncoder());
        values.put(COLUMN_AUDIO_ENCODING_BIT_RATE, record.getAudioEncodingBitRate());
        values.put(COLUMN_AUDIO_SAMPLING_RATE, record.getAudioSamplingRate());
        values.put(COLUMN_AUDIO_CHANNELS, record.getAudioChannels());
        values.put(COLUMN_VIDEO_ENCODER, record.getVideoEncoder());
        values.put(COLUMN_VIDEO_ENCODING_BIT_RATE, record.getVideoEncodingBitRate());
        values.put(COLUMN_VIDEO_FRAME_RATE, record.getVideoFrameRate());
        values.put(COLUMN_VIDEO_SIZE, record.getVideoSize());
        values.put(COLUMN_IS_FAVORED, record.isFavored());
        values.put(COLUMN_FILE_SIZE, record.getFileSize());
        values.put(COLUMN_DURATION, record.getDuration());
        values.put(COLUMN_DATE, record.getDate());
        values.put(COLUMN_THUMBNAIL, record.getThumbnail());

        int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] { String.valueOf(record.getId()) });

        db.close();
        return result;
    }

    public int favoriteRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_FAVORED, record.isFavored());

        int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] { String.valueOf(record.getId()) });

        db.close();
        return result;
    }

    public int renameRecord(Record record, String title) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);

        int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] { String.valueOf(record.getId()) });

        db.close();
        return result;
    }
}
