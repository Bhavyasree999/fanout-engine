package com.fanout.ingestion;

import com.fanout.model.Record;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class FileProducer implements Runnable {

    private final String path;
    private final BlockingQueue<Record> queue;

    public FileProducer(String path, BlockingQueue<Record> queue) {
        this.path = path;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream =
                FileProducer.class.getClassLoader().getResourceAsStream(path);

            if (inputStream == null) {
            throw new RuntimeException("File not found in resources: " + path);
            }

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                queue.put(new Record(line));
            }

        // signal end
            queue.put(new Record("EOF"));

            reader.close();

        } catch (Exception e) {
        e.printStackTrace();
        }
    }
}
