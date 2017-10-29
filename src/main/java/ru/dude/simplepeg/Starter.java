package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.State;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dude on 29.10.2017.
 */
public class Starter {

    public static void main(String [] args)throws Exception{

        File f = new File("input.txt");

        State state = new State();
        state.loadByStream(new FileInputStream(f));

        System.out.println(state.getTextData());

        SpegParser spegParser = new SpegParser(state);
        PegNode res = spegParser.peg();
        res.exec();

        printTree(res);
    }

    public static void printTree(PegNode node){
        StringBuilder sb = new StringBuilder();
        node.toJson(sb,0);
        System.out.println(sb);
    }
}
