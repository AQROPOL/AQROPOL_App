package com.example.bashar.aqropol_app;



import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 21/03/2018.
 */

class AndroidToNuc extends AsyncTask {
    private static final int BUFFER_SIZE = 4096;
    private AllData rest;

    public AndroidToNuc(AllData r){
        this.rest=r;
    }

    public static void insertFile(String saveDir, String content) throws IOException {
            String saveFilePath = saveDir + File.separator + "data";
            FileOutputStream outputStream = new FileOutputStream(saveFilePath, true);
            outputStream.write(content.getBytes());
            Log.i("JSON : ", content);
            outputStream.close();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
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

    @Override
    protected Object doInBackground(Object[] objects) {
        File root = new File(Environment.getExternalStorageDirectory(), "AQROPOL");

        if (!root.exists()) {
            root.mkdirs();
        }
        String saveDir = root.getAbsolutePath();
        try {
            //JSONObject jsonNUCS = readJsonFromUrl(rest.getUrlnuc());
            JSONObject jsonMEASURES = readJsonFromUrl(rest.getUrlmeasures()+"?size=1000");

            //AndroidToNuc.insertFile(saveDir, jsonNUCS.toString());

/*LA DATE N'EST PAS GERE COTE SERVEUR
            //stocker date d'envoi
            SimpleDateFormat formater = null;
            Date now = new Date();
            formater = new SimpleDateFormat("dd-MM-yyyy");
            AndroidToNuc.insertFile(saveDir,  "\n"+formater.format(now)+"\n");
*/

            JSONObject page = new JSONObject(jsonMEASURES.getString("page"));
            int nbPages = Integer.parseInt(page.getString("totalPages"));
            if(nbPages!=0){
                for(int i=0;i</*nbPages*/1;i++){//remplacer 1 par nbPages quand le serveurs pourra supporter plus de requetes...
                    jsonMEASURES = readJsonFromUrl(rest.getUrlmeasures()+"?size=1000&page="+i+"");
                    AndroidToNuc.insertFile(saveDir, jsonMEASURES.toString());
                    float pg = i*100/(nbPages-1);
                    MainActivity.progress((int) pg);//actualise la progressBar si plusieurs pages de données sont récupérées
                }
            }else{
                jsonMEASURES = readJsonFromUrl(rest.getUrlmeasures()+"?size=1000&page=0");
                AndroidToNuc.insertFile(saveDir, jsonMEASURES.toString());
            }

            File fHash = new File(saveDir+ File.separator + "hashs");
            if(fHash.exists()){
                FileReader fr = new FileReader(fHash.getAbsoluteFile());
                BufferedReader br = new BufferedReader(fr);
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }

                /*reconstruire la hashmap a partir du fichier*/
                HashMap<String, byte[]> trueHm = new HashMap<String, byte[]>();
                StringBuilder sb = new StringBuilder(buffer.toString());
                sb.deleteCharAt(0);
                sb.deleteCharAt(sb.length()-1);
                String[] tab1 = sb.toString().split(",");
                for (String s1 : tab1){
                    String[] tab2 = s1.split("=");
                    trueHm.put(tab2[0], tab2[1].getBytes());
                }
                rest.setHashTab(trueHm);
                fHash.delete();
            }else{
                rest.setHashTab(new HashMap<String, byte[]>());
            }

            /*POST les hash*/
            URL urlPost = new URL("http://10.42.0.1:8080/LastHashs");
            HttpURLConnection postConn = (HttpURLConnection) urlPost.openConnection();
            postConn.setRequestMethod("POST");
            postConn.setDoOutput(true);
            postConn.setDoInput(true);
            postConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            Gson gson = new Gson();
            String testgson = gson.toJson(rest.getHashTab());

            Log.i("hashmap : ", testgson);

            OutputStream wr = postConn.getOutputStream();
            wr.write(testgson.getBytes());

            wr.flush();
            wr.close();

            if(postConn.getResponseCode()==202){
                Log.i("ça marche ", "de mon coté");
            }else{
                Log.i("ça ne marche pas", "de mon coté " +postConn.getResponseCode());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
