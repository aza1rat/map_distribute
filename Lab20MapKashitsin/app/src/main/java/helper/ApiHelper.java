package helper;

import android.app.Activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {
    Activity ctx;

    public ApiHelper(Activity ctx)
    {
        this.ctx = ctx;
    }

    public void onReady(String res)
    {

    }

    public void send(String request)
    {
        NetOp nop = new NetOp();
        nop.request = request;
        Thread th = new Thread(nop);
        th.start();
    }

    String httpGet(String request) throws IOException
    {
        URL url = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
        byte[] buf = new byte[512];
        String res = "";
        while (true)
        {
            int num = input.read(buf);
            if (num < 0) break;
            res += new String(buf, 0, num);
        }
        connection.disconnect();
        return res;
    }

    public class NetOp implements Runnable
    {
        public String request;

        @Override
        public void run() {
            try {
                final String res = httpGet(request);
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onReady(res);
                    }
                });
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
