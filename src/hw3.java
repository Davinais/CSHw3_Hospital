import java.io.IOException;
import java.io.FileNotFoundException;
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
            System.out.print("請輸入新病患需要的治療方法\n>>> ");
            String therapy = input.nextLine();
            if(therapy.equals("save"))
            {
                try
                {
                    nckuHospital.saveHospital();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            else if(therapy.equals("load"))
            {
                try
                {
                    nckuHospital = Hospital.loadHospital();
                }
                catch(FileNotFoundException e)
                {
                    System.out.println(e.getMessage());
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            else if(therapy.equals("game over"))
                break;
            else 
            {
                if(!nckuHospital.dealWithIllness(therapy))
                    System.out.println("不好意思，本醫院目前人手不足，可能要麻煩您轉院囉！");
                nckuHospital.turnOver();
            }
        }
        System.out.println("本院祝您不用暴肝，身體健康～");
    }
}
