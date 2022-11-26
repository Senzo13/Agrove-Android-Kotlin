package fr.devid.plantR.utils;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownlinkUtilsJava {

    public void addCommand(String message) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"data\":\"36313b3133353b3133382c302c302c313b3133302c35302c302c31353b3134302c302c302c313b3133383b3133343b36323b3b\",\r\n    \"port\":2,\r\n    \"confirmed\" : true\r\n}");
        Request request = new Request.Builder()
                .url("https://liveobjects.orange-business.com/api/v0/vendors/lora/devices/C4AC590000CC8AC2/commands")
                .method("POST", body)
                .addHeader("x-api-key", "4855f5c1e92647d7989c31b1197b466f")
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
    }


    public static String fromHexString(String hex) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2) {
            str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return str.toString();
    }

    public DownlinkUtilsJava() throws IOException {
    }
}
