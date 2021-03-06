package systemdb;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "POSITION_ENTITY".
*/
public class PositionEntityDao extends AbstractDao<PositionEntity, Long> {

    public static final String TABLENAME = "POSITION_ENTITY";

    /**
     * Properties of entity PositionEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Latitue = new Property(1, double.class, "latitue", false, "LATITUE");
        public final static Property Longitude = new Property(2, double.class, "longitude", false, "LONGITUDE");
        public final static Property Address = new Property(3, String.class, "address", false, "ADDRESS");
        public final static Property Province = new Property(4, String.class, "province", false, "PROVINCE");
        public final static Property City = new Property(5, String.class, "city", false, "CITY");
        public final static Property AdCode = new Property(6, String.class, "adCode", false, "AD_CODE");
        public final static Property District = new Property(7, String.class, "district", false, "DISTRICT");
        public final static Property Type = new Property(8, int.class, "type", false, "TYPE");
        public final static Property From = new Property(9, int.class, "from", false, "FROM");
    }


    public PositionEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PositionEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"POSITION_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"LATITUE\" REAL NOT NULL ," + // 1: latitue
                "\"LONGITUDE\" REAL NOT NULL ," + // 2: longitude
                "\"ADDRESS\" TEXT," + // 3: address
                "\"PROVINCE\" TEXT," + // 4: province
                "\"CITY\" TEXT," + // 5: city
                "\"AD_CODE\" TEXT," + // 6: adCode
                "\"DISTRICT\" TEXT," + // 7: district
                "\"TYPE\" INTEGER NOT NULL ," + // 8: type
                "\"FROM\" INTEGER NOT NULL );"); // 9: from
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"POSITION_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PositionEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindDouble(2, entity.getLatitue());
        stmt.bindDouble(3, entity.getLongitude());
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(4, address);
        }
 
        String province = entity.getProvince();
        if (province != null) {
            stmt.bindString(5, province);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(6, city);
        }
 
        String adCode = entity.getAdCode();
        if (adCode != null) {
            stmt.bindString(7, adCode);
        }
 
        String district = entity.getDistrict();
        if (district != null) {
            stmt.bindString(8, district);
        }
        stmt.bindLong(9, entity.getType());
        stmt.bindLong(10, entity.getFrom());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PositionEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindDouble(2, entity.getLatitue());
        stmt.bindDouble(3, entity.getLongitude());
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(4, address);
        }
 
        String province = entity.getProvince();
        if (province != null) {
            stmt.bindString(5, province);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(6, city);
        }
 
        String adCode = entity.getAdCode();
        if (adCode != null) {
            stmt.bindString(7, adCode);
        }
 
        String district = entity.getDistrict();
        if (district != null) {
            stmt.bindString(8, district);
        }
        stmt.bindLong(9, entity.getType());
        stmt.bindLong(10, entity.getFrom());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PositionEntity readEntity(Cursor cursor, int offset) {
        PositionEntity entity = new PositionEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getDouble(offset + 1), // latitue
            cursor.getDouble(offset + 2), // longitude
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // address
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // province
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // city
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // adCode
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // district
            cursor.getInt(offset + 8), // type
            cursor.getInt(offset + 9) // from
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PositionEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLatitue(cursor.getDouble(offset + 1));
        entity.setLongitude(cursor.getDouble(offset + 2));
        entity.setAddress(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setProvince(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCity(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAdCode(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDistrict(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setType(cursor.getInt(offset + 8));
        entity.setFrom(cursor.getInt(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PositionEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PositionEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PositionEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
