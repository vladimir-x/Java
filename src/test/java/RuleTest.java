import org.junit.Assert;
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

    //@Test
    public void simpleRule(){
        String grammar = "GRAMMAR simple url-> \"aaaa\";";
        String text = "aaaa";

        PegNode grammarTree = SpegParser.createAndExec(grammar);
        RuleProcessor rp = new RuleProcessor(grammarTree);
        CheckResult cr = rp.check(text);

        assertEquals(cr.getErrorText(), cr.getResultType(), ResultType.OK);
    }

}
