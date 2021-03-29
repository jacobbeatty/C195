package JacobBeatty.Logs;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Log {

    /**
     * This function generates the login_activity.txt log file.
     * @param str
     * @throws IOException
     */
    public static void generateLog(String str) throws IOException {
        str = LocalDateTime.now().toString() + ": " + str + "\n";
            FileWriter logWriter = new FileWriter("login_activity.txt", true);
            logWriter.append(str);
            logWriter.close();
    }
}
