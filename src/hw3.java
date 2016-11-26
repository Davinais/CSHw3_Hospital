import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

public class hw3
{
    public static void printHelp(String cmdPrefix, String[] avaliableTherapy)
    {
        String avaliable = "";
        for(String s:avaliableTherapy)
            avaliable += "[" + s + "]";
        System.out.println("可進行療程:" + avaliable);
        System.out.println("    game over :結束本程式");
        System.out.println("    " + cmdPrefix + "save :儲存目前狀態");
        System.out.println("    " + cmdPrefix + "load :從先前存檔讀取狀態");
        System.out.println("    " + cmdPrefix + "gui :顯示GUI介面，關閉GUI後同時退出程式");
        System.out.println("    " + cmdPrefix + "help :顯示本說明");
    }
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        Hospital nckuHospital = new Hospital(3, 3, 10, 2);
        String cmdPrefix = ">";
        String avaliableTherapy[] = nckuHospital.getAvaliableTherapy();
        List<String> checkTherapy = Arrays.asList(avaliableTherapy);
        String notFoundTherapyMessage = "不好意思，無法進行您輸入的治療方法，可能是輸入錯誤囉！\n我們目前能夠進行以下方法：\n";
        {
            for(String s:avaliableTherapy)
                notFoundTherapyMessage += "[" + s + "] ";
            notFoundTherapyMessage += "\n若是想進行指令，請輸入【" + cmdPrefix + "help】確認想輸入的指令";
        }
        printHelp(cmdPrefix, avaliableTherapy);
        while(true)
        {
            nckuHospital.printStatus();
            System.out.print("請輸入新病患需要的治療方法\n>>> ");
            String therapy = input.nextLine();
            if(therapy.startsWith(cmdPrefix))
            {
                therapy = therapy.substring(cmdPrefix.length());
                if(therapy.equals("help"))
                    printHelp(cmdPrefix, avaliableTherapy);
                else if(therapy.equals("save"))
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
                else if(therapy.equals("gui"))
                {
                    HospitalGUI.setHospital(nckuHospital);
                    HospitalGUI.launch(HospitalGUI.class);
                    System.out.println("結束GUI介面，治療完成～");
                    break;
                }
            }
            else if(therapy.equals("game over"))
                break;
            else 
            {
                if(checkTherapy.contains(therapy))
                {
                    if(!nckuHospital.dealWithIllness(therapy))
                        System.out.println("不好意思，本醫院目前人手不足，可能要麻煩您轉院囉！");
                    nckuHospital.turnOver();
                }
                else
                {
                    System.out.println(notFoundTherapyMessage);
                }
            }
        }
        System.out.println("本院祝您不用暴肝，身體健康～");
    }
}
