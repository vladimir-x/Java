package ru.dude.simplepeg.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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

    private State() {
        textData = new StringBuilder();
        position = 0;
    }

    public State(InputStream is) {
        textData = new StringBuilder();
        position = 0;
        loadByStream(is);
    }

    public State(String grammar) {
        textData = new StringBuilder(grammar);
        position = 0;
    }

    /**
     * Load data from input stream
     *
     * @param is
     */
    private void loadByStream(InputStream is) {
        textData = new StringBuilder();

        try (Reader r = new InputStreamReader(is, "UTF-8")) {
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
     *
     * @param len
     * @return
     */
    public int appendPosition(int len) {
        position += len;
        return position;
    }

    public String atPos() {
        if (position < textData.length()) {
            return textData.charAt(position) + "";
        }
        return null;
    }

    public State copy() {
        State state = new State();
        state.textData = textData;
        state.position = position;
        return state;
    }


    public void setPosition(int position) {
        this.position = position;
    }
}
