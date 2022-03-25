package org.unicode.cldr.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.unicode.cldr.unittest.web.TestAll;
import org.unicode.cldr.util.Organization;
import org.unicode.cldr.util.VoteResolver;
import org.unicode.cldr.web.VettingViewerQueue.LoadingPolicy;

public class TestVettingViewerQueue {
    @Test
    void testSummaryResponse() throws IOException, JSONException, SQLException, InterruptedException {
        String str[] = new String[0];
        TestAll.doResetDb(str);
        TestAll.setupTestDb();

        VettingViewerQueue.Status[] status = new VettingViewerQueue.Status[1];
        StringBuilder sb = new StringBuilder();
        VettingViewerQueue vvq = VettingViewerQueue.getInstance();
        SurveyMain sm = new SurveyMain();
        CookieSession.sm = sm; // hack - of course.
        sm.reg = UserRegistry.createRegistry(sm);
        Connection conn = DBUtils.getInstance().getDBConnection();
        sm.xpt = XPathTable.createTable(conn);
        sm.xpt.getByXpath("//foo/bar[@type='baz']");
        DBUtils.closeDBConnection(conn);
        assertNotNull(sm.getSTFactory(), "STFactory");

        CookieSession cs = CookieSession.newSession(false, "0.0.0.0");

        cs.setUser(sm.reg.createTestUser("Ad Min", Organization.surveytool.name(), "en", VoteResolver.Level.admin, "@admin"));
        String message = vvq.getPriorityItemsSummaryOutput(cs, status, LoadingPolicy.START, sb);
        System.out.println(message);
        // SummaryResponse sr = new SummaryResponse();
        // sr.status = status[0];
        // sr.message = message;
        // sr.percent = vvq.getPercent();
        // sr.output = sb.toString();
        int patience = 10;

        while (sb.length() == 0) {
            patience--;
            assertTrue(patience > 0, "Ran out of patience waiting for VVQ");
            Thread.sleep(5000);
            message = vvq.getPriorityItemsSummaryOutput(cs, status, LoadingPolicy.NOSTART, sb);
            System.out.println(message);
            assertFalse(message.startsWith("Stopped"), "VVQ stopped");
        }

        System.out.println(sb.toString());
        System.out.println("sb length: " + sb.length());
    }

}
