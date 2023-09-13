import android.net.
public class ApiRequestHelper
{

    DefaultHttpClient httpClient;
    HttpContext localContext;
    private String ret;

    HttpResponse response = null;
    HttpPost httpPost = null;
    HttpGet httpGet = null;

    public ApiRequestHelper()
    {
        this.setDefaultOptions();
    }

    public void setDefaultOptions()
    {
        HttpParams myParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myParams, RGeneralSettings.getInstance().getSettingInt(RConstants.CONNECTION_TIMEOUT, false));
        HttpConnectionParams.setSoTimeout(myParams, RGeneralSettings.getInstance().getSettingInt(RConstants.CONNECTION_TIMEOUT, false));
        httpClient = new DefaultHttpClient(myParams);
        localContext = new BasicHttpContext();
    }
    public void clearCookies()
    {
        httpClient.getCookieStore().clear();
    }

    public void abort()
    {
        try
        {
            if (httpClient != null)
            {
                System.out.println("Abort.");
                httpPost.abort();
            }
        }
        catch (Exception e)
        {
            System.out.println("Your App Name Here" + e);
        }
    }

    public String sendPost(String url, String data)
    {
        return sendPost(url, data, null);
    }

    public String sendJSONPost(String url, JSONObject data)
    {
        return sendPost(url, data.toString(), "application/json");
    }

    public String sendPost(String url, String data, String contentType)
    {
        ret = null;

        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2109);

        httpPost = new HttpPost(url);
        response = null;

        StringEntity tmp = null;

        Log.d("Your App Name Here", "Setting httpPost headers");

        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:32.0) Gecko/20100101 Firefox/32.0");
        httpPost.setHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");

        if (contentType != null)
        {
            httpPost.setHeader("Content-Type", contentType);
        }
        else
        {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        }

        try
        {
            tmp = new StringEntity(data,"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("Your App Name Here", "HttpUtils : UnsupportedEncodingException : "+e);
        }

        httpPost.setEntity(tmp);

        Log.d("Your App Name Here", url + "?" + data);

        try
        {
            response = httpClient.execute(httpPost,localContext);

            if (response != null)
            {
                ret = EntityUtils.toString(response.getEntity());
            }
        }
        catch (Exception e)
        {
            Log.e("Your App Name Here", "HttpUtils: " + e);
        }

        Log.d("Your App Name Here", "Returning value:" + ret);

        return ret;
    }

    public String sendGet(String url) {
        httpGet = new HttpGet(url);

        try {
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
            Log.e("Your App Name Here", e.getMessage());
        }

        //int status = response.getStatusLine().getStatusCode();  

        // we assume that the response body contains the error message  
        try {
            ret = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.e("Your App Name Here", e.getMessage());
        }

        return ret;
    }

    public InputStream getHttpStream(String urlString) throws IOException
    {
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
        {
            throw new IOException("Not an HTTP connection");
        }

        try
        {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            response = httpConn.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception e)
        {
            throw new IOException("Error connecting");
        } // end try-catch

        return in;
    }
}