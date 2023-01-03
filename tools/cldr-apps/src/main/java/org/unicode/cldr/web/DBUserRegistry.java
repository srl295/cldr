package org.unicode.cldr.web;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.json.bind.annotation.JsonbProperty;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.unicode.cldr.test.CheckCLDR;
import org.unicode.cldr.util.CLDRConfig;
import org.unicode.cldr.util.CLDRConfig.Environment;
import org.unicode.cldr.util.CLDRInfo.UserInfo;
import org.unicode.cldr.util.CLDRLocale;
import org.unicode.cldr.util.LocaleNormalizer;
import org.unicode.cldr.util.LocaleSet;
import org.unicode.cldr.util.Organization;
import org.unicode.cldr.util.SpecialLocales;
import org.unicode.cldr.util.VoteResolver;
import org.unicode.cldr.util.VoteResolver.Level;
import org.unicode.cldr.util.VoteResolver.VoterInfo;
import org.unicode.cldr.util.VoterInfoList;
import org.unicode.cldr.web.UserRegistry.InfoType;
import org.unicode.cldr.web.UserRegistry.LogoutException;
import org.unicode.cldr.web.UserRegistry.ModifyDenial;
import org.unicode.cldr.web.UserRegistry.UserChangedListener;

import com.ibm.icu.dev.util.ElapsedTimer;


public class DBUserRegistry extends UserRegistry {
    private static final java.util.logging.Logger logger = SurveyLog.forClass(DBUserRegistry.class);

    /**
     * Called by SM to create the DB registry
     */
    public static DBUserRegistry createRegistry(SurveyMain theSm) throws SQLException {
        UserRegistry.sm = theSm;
        DBUserRegistry reg = new DBUserRegistry();
        reg.setupDB();
        if (UserRegistry.NORMALIZE_USER_TABLE_ORGS) {
            reg.normalizeUserTableOrgs();
        }
        return reg;
    }

        /**
     * internal - called to setup db
     */
    protected void setupDB() throws SQLException {
        // must be set up first.
        userSettings = UserSettingsData.getInstance(sm);

        String sql = null;
        Connection conn = DBUtils.getInstance().getDBConnection();
        try {
            synchronized (conn) {
                boolean hadUserTable = DBUtils.hasTable(CLDR_USERS);
                if (!hadUserTable) {
                    createUserTable(conn);
                    conn.commit();
                } else if (!DBUtils.db_Derby) {
                    /* update table to DATETIME instead of TIMESTAMP */
                    Statement s = conn.createStatement();
                    sql = "alter table cldr_users change lastlogin lastlogin DATETIME";
                    s.execute(sql);
                    s.close();
                    conn.commit();
                }

                //create review and post table
                sql = "(see ReviewHide.java)";
                ReviewHide.createTable(conn);
                boolean hadInterestTable = DBUtils.hasTable(CLDR_INTEREST);
                if (!hadInterestTable) {
                    Statement s = conn.createStatement();

                    sql = ("create table " + CLDR_INTEREST + " (uid INT NOT NULL , " + "forum  varchar(256) not null " + ")");
                    s.execute(sql);
                    sql = "CREATE  INDEX " + CLDR_INTEREST + "_id_loc ON " + CLDR_INTEREST + " (uid) ";
                    s.execute(sql);
                    sql = "CREATE  INDEX " + CLDR_INTEREST + "_id_for ON " + CLDR_INTEREST + " (forum) ";
                    s.execute(sql);
                    SurveyLog.debug("DB: created " + CLDR_INTEREST);
                    sql = null;
                    s.close();
                    conn.commit();
                }

                myinit(); // initialize the prepared statements

                if (!hadInterestTable) {
                    setupIntLocs(); // set up user -> interest table mapping
                }

            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("SQL err: " + DBUtils.unchainSqlException(se));
            System.err.println("Last SQL run: " + sql);
            throw se;
        } finally {
            DBUtils.close(conn);
        }
    }

    protected void myinit() throws SQLException {
    }

    protected void setupIntLocs() throws SQLException {
        Connection conn = DBUtils.getInstance().getDBConnection();
        PreparedStatement removeIntLoc = null;
        PreparedStatement updateIntLoc = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            removeIntLoc = conn.prepareStatement(SQL_removeIntLoc);
            updateIntLoc = conn.prepareStatement(SQL_updateIntLoc);
            ps = list(null, conn);
            rs = ps.executeQuery();
            ElapsedTimer et = new ElapsedTimer();
            int count = 0;
            while (rs.next()) {
                int user = rs.getInt(1);
                updateIntLocs(user, false, conn, removeIntLoc, updateIntLoc);
                count++;
            }
            conn.commit();
            SurveyLog.debug("update:" + count + " user's locales updated " + et);
        } finally {
            DBUtils.close(removeIntLoc, updateIntLoc, conn, ps, rs);
        }
    }

    /**
     * @param conn
     * @throws SQLException
     */
    private void createUserTable(Connection conn) throws SQLException {
        String sql;
        Statement s = conn.createStatement();

        sql = ("create table " + CLDR_USERS + "(id INT NOT NULL " + DBUtils.DB_SQL_IDENTITY + ", " + "userlevel int not null, "
            + "name " + DBUtils.DB_SQL_UNICODE + " not null, " + "email varchar(128) not null UNIQUE, "
            + "org varchar(256) not null, " + "password varchar(100) not null, " + "audit varchar(1024) , "
            + "locales varchar(1024) , " +
            // "prefs varchar(1024) , " + /* deprecated Dec 2010. Not used
            // anywhere */
            "intlocs varchar(1024) , " + // added apr 2006: ALTER table
            // CLDR_USERS ADD COLUMN intlocs
            // VARCHAR(1024)
            "lastlogin " + DBUtils.DB_SQL_TIMESTAMP0 + // added may 2006:
            // alter table
            // CLDR_USERS ADD
            // COLUMN lastlogin
            // TIMESTAMP
            (!DBUtils.db_Mysql ? ",primary key(id)" : "") + ")");
        s.execute(sql);
        sql = ("INSERT INTO " + CLDR_USERS + "(userlevel,name,org,email,password) " + "VALUES(" + ADMIN + "," + "'admin',"
            + "'SurveyTool'," + "'" + ADMIN_EMAIL + "'," + "'" + SurveyMain.vap + "')");
        s.execute(sql);
        SurveyLog.debug("DB: added user Admin");
        s.close();
    }


    /**
     * assumes caller has a lock on conn
     */
    private String updateIntLocs(int user, Connection conn) throws SQLException {
        PreparedStatement removeIntLoc = null;
        PreparedStatement updateIntLoc = null;
        try {
            removeIntLoc = conn.prepareStatement(SQL_removeIntLoc);
            updateIntLoc = conn.prepareStatement(SQL_updateIntLoc);
            return updateIntLocs(user, true, conn, removeIntLoc, updateIntLoc);
        } finally {
            DBUtils.close(removeIntLoc, updateIntLoc);
        }
    }

    /**
     * assumes caller has a lock on conn
     */
    private String updateIntLocs(int id, boolean doCommit, Connection conn, PreparedStatement removeIntLoc, PreparedStatement updateIntLoc)
        throws SQLException {

        User user = getInfo(id);
        if (user == null) {
            return "";
        }
        logger.finer("uil: remove for user " + id);
        removeIntLoc.setInt(1, id);
        removeIntLoc.executeUpdate();

        LocaleSet intLocSet = user.getInterestLocales();
        logger.finer("uil: intlocs " + id + " = " + intLocSet.toString());
        if (!intLocSet.isAllLocales() && !intLocSet.isEmpty()) {
            /*
             * Simplify locales. For example simplify "pt_PT" to "pt" with loc.getLanguage().
             * Avoid adding duplicates to the table. For example, if the same user has both
             * "pt" and "pt_PT", only add one row, for "pt".
             */
            Set<String> languageSet = new TreeSet<>();
            for (CLDRLocale loc : intLocSet.getSet()) {
                languageSet.add(loc.getLanguage());
            }
            for (String lang : languageSet) {
                logger.finer("uil: intlocs " + id + " + " + lang);
                updateIntLoc.setInt(1, id);
                updateIntLoc.setString(2, lang);
                updateIntLoc.executeUpdate();
            }
        }

        if (doCommit) {
            conn.commit();
        }
        return "";
    }
}
