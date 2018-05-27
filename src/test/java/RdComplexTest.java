import org.junit.Assert;
import org.junit.Test;
import ru.dude.simplepeg.Executable;
import ru.dude.simplepeg.RdParser;
import ru.dude.simplepeg.entity.PegNode;
import ru.dude.simplepeg.entity.ResultType;
import ru.dude.simplepeg.entity.State;

/**
 * Created by dude on 27.05.2018.
 *
 * Сложные тесты для RdParser
 */
public class RdComplexTest extends Assert {

    private void assertProcess(String grammar, String input, Executable executable, ResultType resultType){
        PegNode result = executable.exec(new State(grammar));
        String message = "GRAMMAR:\n" + grammar + "\nINPUT:\n"+input+"\nERROR:"+result.getError();
        assertEquals(message, resultType,result.getResultType());
    }


    @Test
    public void sequencesAndOneZeroMore(){
        RdParser rd = new RdParser();
        Executable executable =  rd.sequence("test_sequence",
                rd.oneOrMore("",rd.parseString("a")),
                rd.oneOrMore("",rd.parseString("b")),
                rd.zeroOrMore(rd.parseString("cc")),
                rd.zeroOrMore(rd.parseString("X")),
                rd.zeroOrMore(rd.parseString("d")),
                rd.oneOrMore("",rd.parseString("e"))
        );
        assertProcess("aabbccddee","abcdXde",executable,ResultType.OK);
        assertProcess("aabbccccddee","abcdXde",executable,ResultType.OK);
        assertProcess("aabbcccddee","abcdXde",executable,ResultType.ERROR);
    }

    @Test
    public void sequencesAndOptional(){
        RdParser rd = new RdParser();
        Executable executable =  rd.sequence("test_sequence",
                rd.parseString("aaaa"),
                rd.optional(rd.parseString("bbbb")),
                rd.optional(rd.parseString("bbbX")),
                rd.parseString("cccc")
        );
        assertProcess("aaaabbbbcccc","aaaabbbbXcccc",executable,ResultType.OK);
        assertProcess("aaaacccc","aaaabbbbXcccc",executable,ResultType.OK);
        assertProcess("aaaabbbXcccc","aaaabbbbXcccc",executable,ResultType.OK);
    }


}
