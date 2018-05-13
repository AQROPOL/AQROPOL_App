package com.example.bashar.aqropol_app;


        import android.os.AsyncTask;
        import android.os.Environment;
        import android.util.Log;

        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.message.BasicNameValuePair;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.FileReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.Reader;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.net.URLEncoder;
        import java.nio.charset.Charset;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;


/**
 * Created by admin on 21/03/2018.
 */

public class AndroidToServer extends AsyncTask {

    private AllData rest;

    public AndroidToServer(AllData r){
        this.rest=r;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpURLConnection postConn = null;
        try {

            File root = new File(Environment.getExternalStorageDirectory(), "AQROPOL");
            if (!root.exists()) {
                root.mkdirs();
            }
            String saveDir = root.getAbsolutePath();
            //récupérer le fichier
            File f = new File(root+"/data");

            URL uuu = new URL("http://pilic27.irisa.fr/API/v2/ReceiveData.php");
            postConn = (HttpURLConnection) uuu.openConnection();
            postConn.setRequestMethod("POST");
            postConn.setDoOutput(true);
            postConn.setDoInput(true);

            FileReader fr = new FileReader(f.getAbsoluteFile());
            BufferedReader br = new BufferedReader(fr);
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }

            //envoyer le fichier
            List namePairValue = new ArrayList();
            namePairValue.add(new BasicNameValuePair("file", buffer.toString()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(namePairValue);

            OutputStream wr = postConn.getOutputStream();
            entity.writeTo(wr);

            wr.flush();
            wr.close();

            if(postConn.getResponseCode()==200){
                Log.i("code reponse : ", postConn.getResponseCode()+"");
                //suprimer le fichier
                f.delete();

                HashMap<String, byte[]> hm = new HashMap<>();
                JSONArray jsonHashTab = readJsonFromUrl(uuu.toString());

                for(int i=0;i<jsonHashTab.length();i++){
                    JSONObject id = jsonHashTab.getJSONObject(i);
                    String id_capt = id.getString("id_hub");
                    String hash = id.getString("hash");
                    byte[] bhash = hash.getBytes();
                    hm.put(id_capt, bhash);
                }

                //sauvegarde du tableau de hash dans un fichier (persistance des données)
                FileOutputStream outputStream = new FileOutputStream(saveDir+ File.separator + "hashs", false);
                outputStream.write(hm.toString().getBytes());
                Log.i("hasmap : ", hm.toString());
                outputStream.close();
            }else {
                Log.i("code reponse : ", postConn.getResponseCode()+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        postConn.disconnect();

        return null;
    }

    public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public AllData getRest() {
        return rest;
    }

    public void setRest(AllData rest) {
        this.rest = rest;
    }
}
