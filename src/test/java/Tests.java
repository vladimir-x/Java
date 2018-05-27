import org.junit.Assert;
import org.junit.Test;
import ru.dude.simplepeg.SpegParser;
import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.ResultType;

import java.io.ByteArrayInputStream;

/**
 * Created by dude on 29.10.2017.
 */
public class Tests extends Assert{

    private void assertProcess(String input,ResultType expected){
        PegNode result = SpegParser.createAndExec(new ByteArrayInputStream(input.getBytes()));
        String message = "INPUT:\n"+input+"\nERROR:"+result.getError();
        assertEquals(message,expected,result.getResultType());
    }

   // @Test
    public void all(){
        headerAndSimpleRule();
        noRuleError();
        noLastSemicolonError();

        oneRuleStrings();
        oneRuleRegexp();
        twoRulesSimple();
        twoRulesRegexp();
        manyRulesRegexp();

        complicateTest_URL();
    }

    @Test
    public void headerAndSimpleRule(){
        String input = "GRAMMAR simple rule-> \"aaaa\";";
        assertProcess(input,ResultType.OK);
    }

    @Test
    public void noRuleError(){
        String input = "GRAMMAR simple;";
        assertProcess(input,ResultType.ERROR);
    }

    @Test
    public void noLastSemicolonError(){
        String input = "GRAMMAR simple rule-> \"aaaa\"";
        assertProcess(input,ResultType.ERROR);
    }

    @Test
    public void oneRuleStrings(){
        String input = "GRAMMAR one rule-> \"aaaa\" \"bb\" \"cccc\" \"zz\";";
        assertProcess(input,ResultType.OK);
    }

    @Test
    public void oneRuleRegexp(){
        String input = "GRAMMAR one rule-> \"XX\" [a-zA-Z0-9-]+ [^ #]*;";
        assertProcess(input,ResultType.OK);
    }


    @Test
    public void twoRulesSimple(){
        String input = "GRAMMAR duo rule_one-> \"XX\" rule_two; rule_two-> \"YY\";";
        assertProcess(input,ResultType.OK);
    }

    @Test
    public void twoRulesRegexp(){
        String input = "GRAMMAR duo rule_one-> \"XX\" rule_two [^ #]*; rule_two-> \"YY\" [a-zA-Z0-9-]+;";
        assertProcess(input,ResultType.OK);
    }

    @Test
    public void manyRulesRegexp(){
        String input = "GRAMMAR many\n" +
                "rule-> r_one r_two r_three r_four; \n" +
                "r_one-> \"XX\";\n" +
                "r_two-> [^ #]*;\n" +
                "r_three-> \"YY\";\n" +
                "r_four-> \"ZZ\" [a-zA-Z0-9-]+;";
        assertProcess(input,ResultType.OK);
    }


    //TODO: test for SEQUENCE, ONE_OR_MANY and others


    @Test
    public void complicateTest_URL(){
        String input = "GRAMMAR url\n" +
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
        assertProcess(input,ResultType.OK);
    }
}
