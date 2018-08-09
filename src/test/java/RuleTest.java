import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ru.dude.simplepeg.RuleProcessor;
import ru.dude.simplepeg.SpegParser;
import ru.dude.simplepeg.entity.CheckResult;
import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.ResultType;

/**
 * Created by dude on 27.05.2018.
 *
 * Тесты выполнения rule
 */
public class RuleTest extends Assert {

    @Test
    public void ruleString(){
        String grammar = "GRAMMAR simple url-> \"aaaa\";";
        String text = "aaaa";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }


    @Test
    public void ruleStringError(){
        String grammar = "GRAMMAR simple url-> \"aaaa\";";
        String text = "aabb";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }

    @Test
    public void ruleStringMany(){
        String grammar = "GRAMMAR simple url-> \"aaaa\" \"bb\" \"cc\" \"dd\" \"ee\";";
        String text = "aaaabbccddee";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }


    @Test
    public void ruleRegex(){
        String grammar = "GRAMMAR simple url-> [a-z];";
        String text = "a";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }


    @Test
    public void ruleRegexMany(){
        String grammar = "GRAMMAR simple url-> [a-z] [A-Z] [0-9];";
        String text = "aZ2";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }


    @Test
    public void ruleRegexError(){
        String grammar = "GRAMMAR simple url-> [a-z];";
        String text = "X";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }


    @Test
    public void ruleOneOrMore(){
        String grammar = "GRAMMAR simple url-> \"a\"+;";
        String text = "aaa";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleOneOrMore2(){
        String grammar = "GRAMMAR simple url-> \"ab\"+;";
        String text = "ababab";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleOneOrMoreError(){
        String grammar = "GRAMMAR simple url-> \"abc\"+;";
        String text = "ababab";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }

    @Test
    public void ruleZeroOrMore(){
        String grammar = "GRAMMAR simple url-> \"a\"*;";
        String text = "aaa";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleZeroOrMore2(){
        String grammar = "GRAMMAR simple url-> \"ab\"*;";
        String text = "ababX";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleOrderedChoise(){
        String grammar = "GRAMMAR simple url-> \"a\"/\"b\";";
        String text = "b";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleOrderedChoiseError(){
        String grammar = "GRAMMAR simple url-> \"a\"/\"b\";";
        String text = "c";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }


    @Test
    public void ruleGroup(){
        String grammar = "GRAMMAR simple url-> (\"a\"/\"b\")+;";
        String text = "ab";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleGroupError(){
        String grammar = "GRAMMAR simple url-> (\"a\"/\"b\");";
        String text = "c";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }


    @Test
    public void ruleNot(){
        String grammar = "GRAMMAR simple url-> !\"a\";";
        String text = "b";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleNotError(){
        String grammar = "GRAMMAR simple url-> !\"a\";";
        String text = "a";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }

    /**
     * осатвлю пока также как в js версии.
     * по моему то неправильное поведение предиката not
     */
    @Test
    public void ruleNot2(){
        String grammar = "GRAMMAR simple url-> (!\"a\") \"v\";";
        String text = "v";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }


    @Test
    @Ignore
    public void ruleAnd(){

        /*
        ???????

        не понятно что это

        String grammar = "GRAMMAR simple url-> (!\"a\") \"v\";";
        String text = "v";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
        */
    }

    @Test
    public void ruleOptional(){
        String grammar = "GRAMMAR simple url-> \"a\"? \"b\";";
        String text = "ab";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleOptional2(){
        String grammar = "GRAMMAR simple url-> \"a\"? \"b\";";
        String text = "b";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleOptionalError(){
        String grammar = "GRAMMAR simple url-> \"a\"? \"b\";";
        String text = "xb";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }


    @Test
    public void ruleSubExpression(){
        String grammar = "GRAMMAR url url -> shema; shema -> \"ab\";";
        String text = "ab";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void ruleSubExpressionError(){
        String grammar = "GRAMMAR url url -> shema; shema -> \"ab\";";
        String text = "ac";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }

    @Test
    public void ruleCError(){
        String grammar = "GRAMMAR url url -> shema; shema -> \"ab\";";
        String text = "ac";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }


    @Test
    public void complicateTest_URL(){
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

        String text = "https://simplepeg.github.io/";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

    @Test
    public void complicateTest_URL_Error(){
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

        String text = "https://////simplepeg.github.io/";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.ERROR);
    }

}
