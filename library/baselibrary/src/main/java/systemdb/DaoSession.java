package systemdb;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import systemdb.Login;
import systemdb.PositionEntity;
import systemdb.SearchHistory;

import systemdb.LoginDao;
import systemdb.PositionEntityDao;
import systemdb.SearchHistoryDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig loginDaoConfig;
    private final DaoConfig positionEntityDaoConfig;
    private final DaoConfig searchHistoryDaoConfig;

    private final LoginDao loginDao;
    private final PositionEntityDao positionEntityDao;
    private final SearchHistoryDao searchHistoryDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        loginDaoConfig = daoConfigMap.get(LoginDao.class).clone();
        loginDaoConfig.initIdentityScope(type);

        positionEntityDaoConfig = daoConfigMap.get(PositionEntityDao.class).clone();
        positionEntityDaoConfig.initIdentityScope(type);

        searchHistoryDaoConfig = daoConfigMap.get(SearchHistoryDao.class).clone();
        searchHistoryDaoConfig.initIdentityScope(type);

        loginDao = new LoginDao(loginDaoConfig, this);
        positionEntityDao = new PositionEntityDao(positionEntityDaoConfig, this);
        searchHistoryDao = new SearchHistoryDao(searchHistoryDaoConfig, this);

        registerDao(Login.class, loginDao);
        registerDao(PositionEntity.class, positionEntityDao);
        registerDao(SearchHistory.class, searchHistoryDao);
    }
    
    public void clear() {
        loginDaoConfig.clearIdentityScope();
        positionEntityDaoConfig.clearIdentityScope();
        searchHistoryDaoConfig.clearIdentityScope();
    }

    public LoginDao getLoginDao() {
        return loginDao;
    }

    public PositionEntityDao getPositionEntityDao() {
        return positionEntityDao;
    }

    public SearchHistoryDao getSearchHistoryDao() {
        return searchHistoryDao;
    }

}
