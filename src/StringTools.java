import java.nio.charset.Charset;

public class StringTools
{
    public static String stringPadding(String s, int space)
    {
        int spaceNeeded = space - s.getBytes(Charset.forName("Big5")).length;
        if(spaceNeeded <= 0)
            return s;
        for(int i=0; i < spaceNeeded; i++)
            s += " ";
        return s;
    }
}
