import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WifiPasswordExtractor {

    public static void main(String[] args) {
        try {
            // Running command
            ProcessBuilder processBuilder = new ProcessBuilder("netsh", "wlan", "show", "profiles");
            Process process = processBuilder.start();

            // Reading the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder commandOutput = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                commandOutput.append(line).append("\n");
            }

            // Extracting profiles
            String[] profiles = commandOutput.toString().split("\n");
            for (String profile : profiles) {
                if (profile.contains("All User Profile")) {
                    String profileName = profile.split(":")[1].trim();
                    String keyResults = getWifiKey(profileName);
                    System.out.printf("%-30s|  %-20s%n", profileName, keyResults);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Keep the console open
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getWifiKey(String profileName) throws IOException {
        // Running command to get key
        ProcessBuilder processBuilder = new ProcessBuilder("netsh", "wlan", "show", "profile", profileName, "key=clear");
        Process process = processBuilder.start();

        // Reading the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder keyOutput = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            keyOutput.append(line).append("\n");
        }

        // Extracting key content
        String[] results = keyOutput.toString().split("\n");
        for (String result : results) {
            if (result.contains("Key Content")) {
                return result.split(":")[1].trim();
            }
        }

        return "";
    }
}
