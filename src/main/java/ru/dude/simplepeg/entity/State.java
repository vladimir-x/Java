package ru.dude.simplepeg.entity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Store and processing input data
 * Created by dude on 29.10.2017.
 */
public class State {

    /**
     * Input data
     */
    StringBuilder textData;

    /**
     * current position (slide)
     */
    int position;

    public State(){
        textData = new StringBuilder();
        position = 0;
    }

    /**
     * Load data from input stream
     * @param is
     */
    public void loadByStream(InputStream is){
        textData = new StringBuilder();

        try (Reader r = new InputStreamReader(is, "UTF-8")){
            int c = 0;
            while ((c = r.read()) != -1) {
                textData.append((char) c);
            }
            r.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        position = 0;
    }


    public int getPosition() {
        return position;
    }

    public StringBuilder getTextData() {
        return textData;
    }

    /**
     * increase slide
     * @param len
     * @return
     */
    public int appendPosition(int len){
        position+=len;
        return position;
    }

    public String atPos() {
        if (position<textData.length()){
            return textData.charAt(position)+"";
        }
        return null;
    }
}
