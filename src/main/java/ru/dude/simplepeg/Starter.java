package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.State;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dude on 29.10.2017.
 */
public class Starter {

    public static void main(String[] args) throws Exception {

        File f = new File("input.txt");

        State state = new State(new FileInputStream(f));

        System.out.println(state.getTextData());

        SpegParser spegParser = new SpegParser(state);
        PegNode res = spegParser.peg().exec(state);

        printTree(res);
    }

    public static void printTree(PegNode node) throws IOException {
        System.out.println(node.getResultType());
        System.out.println(node.getError());

        StringBuilder sb = new StringBuilder();
        node.toJson(sb, 0);
        BufferedWriter bw = new BufferedWriter(new FileWriter("tree.json"));

        bw.write(sb.toString());
        bw.close();
    }
}
