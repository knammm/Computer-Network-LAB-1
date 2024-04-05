import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Get_Home_Page {
    public static void main(String[] args) {
        String url = "https://mybk.hcmut.edu.vn/my/index.action"; // test with mybk
        String filePath = "output.txt";
        
        try {
            // Create URL object
            URL obj = new URL(url);
            
            // Open connection
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            // Request GET method
            con.setRequestMethod("GET");
            
            // Get response code
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            // Read response content
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append("\n");
            }
            in.close();
            
            // Write content to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(response.toString());
                System.out.println("Content saved to: " + filePath);
            }
			
			System.out.println("Download successfully !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
