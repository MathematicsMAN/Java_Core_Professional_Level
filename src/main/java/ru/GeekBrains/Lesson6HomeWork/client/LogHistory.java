package ru.GeekBrains.Lesson6HomeWork.client;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class LogHistory {
    private final int COUNT_LAST_LINES = 10;
    private final String fileName;
    private final File fileHistory;
    private FileWriter fileWriter;

    public LogHistory(String fileName) {
        this.fileName = "history_" + fileName + ".txt";
        fileHistory = new File(this.fileName);
        if (!fileHistory.exists()){
            try {
                fileHistory.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToHistory(String str){
        try {
            fileWriter = new FileWriter(fileHistory, true);
            fileWriter.write(str);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLastLines(){
        int count;
        long lengthFile = fileHistory.length();
        long seekPosition = lengthFile;

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileName,"r");
            randomAccessFile.seek(lengthFile);
            count = 0;
            while(seekPosition > 0){
                int b = randomAccessFile.read();
                if (b == (byte)'\n'){
                    if (++count == COUNT_LAST_LINES) {
                        break;
                    }
                    b = randomAccessFile.read();
                }
                seekPosition --;
                randomAccessFile.seek(seekPosition);
            }
            String strLines;
            StringBuilder stringBuilder = new StringBuilder();
            while ((strLines = randomAccessFile.readLine()) != null){
                String res = new String(strLines.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);//"windows-1251");
                stringBuilder.append(res).append("\n");
            }
            randomAccessFile.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
