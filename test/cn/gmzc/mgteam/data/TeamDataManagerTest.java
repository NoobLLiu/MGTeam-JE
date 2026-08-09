package cn.gmzc.mgteam.data;

import cn.gmzc.mgteam.model.Team;
import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Offline regression test for player-entered team ID resolution.
 *
 * Run with the compiled MGTeam classes and Gson on the classpath:
 * java -cp <classes>;<gson.jar> cn.gmzc.mgteam.data.TeamDataManagerTest
 */
public final class TeamDataManagerTest {
    private TeamDataManagerTest() {}

    public static void main(String[] args) throws Exception {
        File temp = Files.createTempDirectory("mgteam-id-test").toFile();
        try {
            TeamDataManager manager = new TeamDataManager(temp, Logger.getLogger("MGTeamTest"));
            Team mixedCase = new Team("mixed", UUID.randomUUID(), "owner");
            manager.put("m1WT", mixedCase);

            checkEquals("m1WT", manager.resolveId("m1WT"), "exact ID keeps canonical key");
            checkEquals("m1WT", manager.resolveId(" M1wt "), "case-insensitive trimmed ID resolves");
            checkSame(mixedCase, manager.resolve("M1WT"), "resolved team is the stored instance");
            checkNull(manager.resolveId(null), "null ID is rejected");
            checkNull(manager.resolveId(""), "empty ID is rejected");
            checkNull(manager.resolveId("unknown"), "unknown ID is rejected");

            // If legacy data contains keys that differ only by case, an exact
            // key remains preferred over the case-insensitive fallback.
            Team exact = new Team("exact", UUID.randomUUID(), "owner2");
            manager.put("M1WT", exact);
            checkEquals("M1WT", manager.resolveId("M1WT"), "exact key wins over fallback");
            checkSame(exact, manager.resolve("M1WT"), "exact team wins over fallback");

            if (args.length > 0) {
                TeamDataManager live = new TeamDataManager(new File(args[0]), Logger.getLogger("MGTeamLiveTest"));
                live.load();
                checkEquals("m1WT", live.resolveId("M1WT"), "live mixed-case ID resolves case-insensitively");
                checkEquals("test", live.resolve(" M1wT ").getName(), "live team data resolves after trimming");
            }

            System.out.println("TeamDataManagerTest: PASS");
        } finally {
            deleteRecursively(temp);
        }
    }

    private static void checkEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }

    private static void checkSame(Object expected, Object actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message);
        }
    }

    private static void checkNull(Object actual, String message) {
        if (actual != null) {
            throw new AssertionError(message + " (actual=" + actual + ")");
        }
    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists()) return;
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) deleteRecursively(child);
        }
        if (!file.delete() && file.exists()) {
            file.deleteOnExit();
        }
    }
}
