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
            System.out.print("請輸入新病患需要的治療方法\n>>> ");
            String therapy = input.nextLine();
            if(therapy.equals("save"))
                nckuHospital.saveHospital();
            else if(therapy.equals("load"))
                nckuHospital = Hospital.loadHospital();
            else 
            {
                if(!nckuHospital.dealWithIllness(therapy))
                    System.out.println("不好意思，本醫院目前人手不足，可能要麻煩您轉院囉！");
                nckuHospital.turnOver();
            }
        }
    }
}
