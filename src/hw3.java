import java.io.IOException;
import java.util.Scanner;

public class hw3
{
    public static void main(String[] args) throws IOException
    {
        Scanner input = new Scanner(System.in);
        Hospital nckuHospital = new Hospital(3, 3, 10, 2);
        while(true)
        {
            nckuHospital.printStatus();
            System.out.print(">>> ");
            String therapy = input.nextLine();
            if(therapy.equals("save"))
                nckuHospital.saveHospital();
            else if(therapy.equals("load"))
                nckuHospital = Hospital.loadHospital();
            else if(!nckuHospital.dealWithIllness(therapy))
                System.out.println("他轉學了");
            nckuHospital.turnOver();
        }
    }
}
