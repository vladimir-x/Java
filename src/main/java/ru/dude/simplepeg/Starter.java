package ru.dude.simplepeg;

import ru.dude.simplepeg.entity.PegNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by dude on 29.10.2017.
 */
public class Starter {

    public static void main(String[] args) throws Exception {

        String grammar = "GRAMMAR url\n" +
                "\n" +
                "url       ->  scheme \"://\" host pathname search hash?;\n" +
                "scheme    ->  \"http\" \"s\"?;\n" +
                "host      ->  hostname port?;\n" +
                "hostname  ->  segment (\".\" segment)*;\n" +
                "segment   ->  [a-z0-9-]+;\n" +
                "port      ->  \":\" [0-9]+;\n" +
                "pathname  ->  \"/\" [^ ?]*;\n" +
                "search    ->  (\"?\" [^ #]*)?;\n" +
                "hash      ->  \"#\" [^ ]*;";

        RuleProcessor rp = new RuleProcessor(SpegParser.createAndExec(grammar));

        System.out.println(rp.check("https://simplepeg.github.io/"));
        System.out.println(rp.check("https://google.com/"));
        System.out.println(rp.check("https://abcdssss.....com/"));

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
