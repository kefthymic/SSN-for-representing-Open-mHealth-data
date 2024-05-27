import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThreadForGraphDBServer extends Thread{

    public void run(){
        String os = System.getProperty("os.name").toLowerCase();
        String[] command= null;

        if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // Unix/Linux/Mac
            command = new String[]{"./graphdb-10.6.2/bin/graphdb"};
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
