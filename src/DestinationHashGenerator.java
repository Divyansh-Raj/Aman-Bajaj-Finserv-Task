import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileReader;

public class DestinationHashGenerator {
    public static void main(String[] args) throws Exception {
        // Arguments: RollNumber and JSON file path
        if (args.length != 2) {
            System.out.println("Usage: java DestinationHashGenerator <RollNumber> <PathToJsonFile>");
            return;
        }

        String rollNumber = args[0].toLowerCase().trim();
        String jsonFilePath = args[1];

        // Read JSON file content into a String
        StringBuilder jsonData = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                jsonData.append(line);
            }
        }

        // Parse the JSON
        JSONObject jsonObject = new JSONObject(jsonData.toString());
        String destinationValue = findFirstDestination(jsonObject);

        if (destinationValue == null) {
            System.out.println("Key 'destination' not found in the JSON file.");
            return;
        }

        // Generate a random string
        String randomString = generateRandomString(8);

        // Create the MD5 hash
        String input = rollNumber + destinationValue + randomString;
        String hash = generateMD5Hash(input);

        // Output the result
        System.out.println(hash + ";" + randomString);
    }

    private static String findFirstDestination(JSONObject jsonObject) {
        for (Object keyObj : jsonObject.keySet()) {
            String key = (String) keyObj;  // Cast the Object to String
            if (key.equals("destination")) {
                return jsonObject.getString(key);
            }
    
            Object value = jsonObject.get(key);
            
            if (value instanceof JSONObject) {
                // Recursively search in nested JSONObject
                String result = findFirstDestination((JSONObject) value);
                if (result != null) return result;
            } else if (value instanceof JSONArray) {
                // Iterate over JSONArray if the value is a JSONArray
                JSONArray jsonArray = (JSONArray) value;
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object obj = jsonArray.get(i);
                    if (obj instanceof JSONObject) {
                        String result = findFirstDestination((JSONObject) obj);
                        if (result != null) return result;
                    }
                }
            }
        }
        return null;
    }
    
    

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws Exception {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
