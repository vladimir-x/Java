import org.junit.Assert;
import org.junit.Test;
import ru.dude.simplepeg.Executable;
import ru.dude.simplepeg.RdExecutor;
import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.ResultType;
import ru.dude.simplepeg.entity.State;


/**
 * Created by dude on 27.05.2018.
 *
 * Тесты конструкций PEG
 */
public class RdExecutorTest extends Assert {


    private void assertProcess(String grammar,String input,Executable executable,ResultType resultType){
        PegNode result = executable.exec(new State(grammar));
        String message = "GRAMMAR:\n" + grammar + "\nINPUT:\n"+input+"\nERROR:"+result.getError();
        assertEquals(message, resultType,result.getResultType());
    }

    @Test
    public void simpleString(){
        String grammar = "aaaazz;";
        String input = "aaaazz";
        Executable executable =  new RdExecutor().parseString(input);
        assertProcess(grammar,input,executable,ResultType.OK);
    }


    @Test
    public void simpleRegexp(){
        String grammar = "aaaazz;";
        String input = "[a-z]+";
        Executable executable =  new RdExecutor().parseRegexp(input);
        assertProcess(grammar,input,executable,ResultType.OK);
    }

    @Test
    public void eofTest(){
        String grammar = "";
        String input = "[___ANY___]";
        Executable executable =  new RdExecutor().parseEndOfFile();
        assertProcess(grammar,input,executable,ResultType.OK);
    }

    @Test
    public void simpleSequenceOk(){
        String grammar = "aaaazzxxXx";
        String s1 = "aaaa";
        String s2 = "zz";
        String r3 = "[xX]+";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.sequence("test_sequence",
        rd.parseString(s1),
                rd.parseString(s2),
                rd.parseRegexp(r3)
        );
        assertProcess(grammar,s1+","+s2+","+r3,executable,ResultType.OK);
    }

    @Test
    public void simpleSequenceEmpty(){
        String grammar = "_____XXXX___";
        String s1 = "aaaa";
        String s2 = "zz";
        String r3 = "[0-9]+";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.sequence("test_sequence",
                rd.optional(rd.parseString(s1)),
                rd.optional(rd.parseString(s2)),
                rd.optional(rd.parseRegexp(r3))
        );
        assertProcess(grammar,s1+","+s2+","+r3,executable,ResultType.EMPTY);
    }

    @Test
    public void simpleSequenceError(){
        String grammar = "aaaazzxxXx";
        String s1 = "aaaa";
        String s2 = "zz";
        String r3 = "[0-9]+";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.sequence("test_sequence",
                rd.parseString(s1),
                rd.parseString(s2),
                rd.parseRegexp(r3)
        );
        assertProcess(grammar,s1+","+s2+","+r3,executable,ResultType.ERROR);
    }

    @Test
    public void simpleOrderedChoiceOk(){
        String s1 = "aaaa";
        String s2 = "zz";
        String r3 = "[xX]+";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.orderedChoice(
                "", rd.parseString(s1),
                rd.parseString(s2),
                rd.parseRegexp(r3)
        );
        assertProcess("aaaazzxxXx_",s1+","+s2+","+r3,executable,ResultType.OK);
        assertProcess("zz____xxXx_",s1+","+s2+","+r3,executable,ResultType.OK);
        assertProcess("xxXx_______",s1+","+s2+","+r3,executable,ResultType.OK);
    }

    @Test
    public void simpleOrderedChoiceEmpty(){
        String s1 = "aaaa";
        String s2 = "zz";
        String r3 = "[0-9]+";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.orderedChoice(
                "", rd.optional(rd.parseString(s1)),
                rd.optional(rd.parseString(s2)),
                rd.optional(rd.parseRegexp(r3))
        );
        assertProcess("xxXx_______",s1+","+s2+","+r3,executable,ResultType.EMPTY);
    }

    @Test
    public void simpleOrderedChoiceError(){
        String s1 = "aaaa";
        String s2 = "zz";
        String r3 = "[0-9]+";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.orderedChoice(
                "", rd.parseString(s1),
                rd.parseString(s2),
                rd.parseRegexp(r3)
        );
        assertProcess("xxXx_______",s1+","+s2+","+r3,executable,ResultType.ERROR);
    }

    @Test
    public void simpleOneOrMoreOk(){
        String s1 = "a";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.oneOrMore("simpleOneOrMoreOK_test",
                rd.parseString(s1)
        );
        assertProcess("aaaazzxxXx_",s1,executable,ResultType.OK);
    }

    @Test
    public void simpleOneOrMoreError(){
        String s1 = "a";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.oneOrMore("simpleOneOrMoreError_test",
                rd.parseString(s1)
        );
        assertProcess("bbaaaazzxxXx_",s1,executable,ResultType.ERROR);
    }

    @Test
    public void simplZeroOrMoreOk(){
        String s1 = "a";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.zeroOrMore(
                "", rd.parseString(s1)
        );
        assertProcess("aaaazzxxXx_",s1,executable,ResultType.OK);
    }

    @Test
    public void simpleZeroOrMoreEmpty(){
        String s1 = "a";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.zeroOrMore(
                "", rd.parseString(s1)
        );
        assertProcess("bbaaaazzxxXx_",s1,executable,ResultType.EMPTY);
    }

    @Test
    public void simpleOptionalOk(){
        String s1 = "a";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.optional(
                rd.parseString(s1)
        );
        assertProcess("aaaazzxxXx_",s1,executable,ResultType.OK);
    }

    @Test
    public void simpleOptionalEmpty(){
        String s1 = "a";
        RdExecutor rd = new RdExecutor();
        Executable executable =  rd.optional(
                rd.parseString(s1)
        );
        assertProcess("bbaaaazzxxXx_",s1,executable,ResultType.EMPTY);
    }
}
