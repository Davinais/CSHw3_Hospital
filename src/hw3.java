import java.util.Scanner;

public class hw3
{
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        Hospital nckuHospital = new Hospital(3, 3, 10, 2);
        while(true)
        {
            nckuHospital.printStatus();
            System.out.print(">>> ");
            String therapy = input.nextLine();
            if(!nckuHospital.dealWithIllness(therapy))
                System.out.println("他轉學了");
            nckuHospital.turnOver();
        }
    }
}
