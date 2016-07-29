package me.zouooh.slark.request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import me.zouooh.slark.DataResponse;
import me.zouooh.slark.SlarkException;
import me.zouooh.slark.http.HttpStatus;

/**
 * Created by zouooh on 2016/7/26.
 */
public class SlarkPost extends Request {
    public SlarkPost(String url) {
        super(url);
        setMethod(Method.POST);
        retryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,1));
    }

    @Override
    public URL makeURL() {
        try {
            return new URL(getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DataResponse adpter(DataResponse networkResponse) throws SlarkException {
        if (networkResponse.statusCode == HttpStatus.SC_OK)
            return networkResponse;
        throw new StatusException(networkResponse.statusCode);
    }

    @Override
    public void sendData(DataOutputStream out) throws IOException {
        byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
        // 1. 处理文字形式的POST请求
        if (params != null) {
            for (Map.Entry<String, String> ffkvp : params.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"");
                sb.append(ffkvp.getKey() + "\"");
                sb.append("\r\n");
                sb.append("\r\n");
                sb.append(ffkvp.getValue());
                sb.append("\r\n");
                String boundaryMessage1 = sb.toString();
                out.write(boundaryMessage1.getBytes(getParamsEncoding()));
            }
        }

        if (fileItems != null) {

            int leng = fileItems.size();
            for (int i = 0; i < leng; i++) {
                FormFileItem fname = fileItems.get(i);
                File file = new File(fname.getFileName());
                StringBuilder sb = new StringBuilder();
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data;name=\""
                        + fname.getFormFieldName() + "\";filename=\""
                        + file.getName() + "\"\r\n");
                sb.append("Content-Type:application/octet-stream\r\n\r\n");

                byte[] data = sb.toString().getBytes();
                out.write(data);

                DataInputStream in = new DataInputStream(new FileInputStream(
                        file));
                int bytes;
                byte[] bufferOut = new byte[8192];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                out.write("\r\n".getBytes());
                in.close();
            }
        }
        out.write(end_data);
        out.flush();
    }
}
