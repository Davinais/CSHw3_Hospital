import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class Hospital
{
    Surgeon surgeons[];
    Physician physicians[];
    Nurse nurses[];
    Anesthetist anesthetists[];
    private static final String savePath = "Hospital.sav";
    private static File saveFile = new File(savePath);
    private static byte savePrefix[] = {68, 65, 72, 79}; //'D' 'A' 'H' 'O'
    private static final int medicNeededSaveDataNum = 6, medicsLengthSpace = 4;
    //醫院建構函數，順序為外科醫生數量->內科醫生數量->護士數量->麻醉師數量
    public Hospital(int surgeon_num, int physician_num, int nurse_num, int anesthetist_num)
    {
        surgeons = new Surgeon[surgeon_num];
        for(int i=0; i < surgeon_num; i++)
            surgeons[i] = new Surgeon();
        physicians = new Physician[physician_num];
        for(int i=0; i < physician_num; i++)
            physicians[i] = new Physician();
        nurses = new Nurse[nurse_num];
        for(int i=0; i < nurse_num; i++)
            nurses[i] = new Nurse();
        anesthetists = new Anesthetist[anesthetist_num];
        for(int i=0; i < anesthetist_num; i++)
            anesthetists[i] = new Anesthetist();
    }
    public void printStatus()
    {
        String status = "";
        status = recursiveGetStatusString(surgeons, status);
        status = recursiveGetStatusString(physicians, status);
        status = recursiveGetStatusString(nurses, status);
        status = recursiveGetStatusString(anesthetists, status);
        System.out.println(status);
    }
    public int[] getMedicNumber()
    {
        return new int[]{surgeons.length, physicians.length, nurses.length, anesthetists.length};
    }
    public MedicalPersonnel[] getMedicList(String jobName)
    {
        switch(jobName)
        {
            case "Surgeon":
                return surgeons;
            case "Physician":
                return physicians;
            case "Nurse":
                return nurses;
            case "Anesthetist":
                return anesthetists;
            default:
                return null;
        }
    }
    public void saveHospital() throws IOException
    {
        if(!saveFile.exists())
            saveFile.createNewFile();
        FileOutputStream saveFO = new FileOutputStream(saveFile);
        ByteBuffer saveByteBuf = ByteBuffer.allocate(savePrefix.length+medicsLengthSpace+(surgeons.length+physicians.length+nurses.length+anesthetists.length)*medicNeededSaveDataNum);
        saveByteBuf.clear();
        saveByteBuf.put(savePrefix);
        byte medicsLength[] = {(byte)surgeons.length, (byte)physicians.length, (byte)nurses.length, (byte)anesthetists.length};
        saveByteBuf.put(medicsLength);
        recursiveSaveToByteBuffer(surgeons, saveByteBuf);
        recursiveSaveToByteBuffer(physicians, saveByteBuf);
        recursiveSaveToByteBuffer(nurses, saveByteBuf);
        recursiveSaveToByteBuffer(anesthetists, saveByteBuf);
        saveByteBuf.flip();
        FileChannel saveChannel = saveFO.getChannel();
        while(saveByteBuf.hasRemaining())
            saveChannel.write(saveByteBuf);
        saveChannel.close();
        saveFO.close();
        System.out.println("存檔完成！");
    }
    private void recursiveSaveToByteBuffer(MedicalPersonnel[] medics, ByteBuffer saveByteBuf)
    {
        for(MedicalPersonnel medic:medics)
        {
            byte idle = (byte)((medic.isIdle())?1:0);
            byte exhausted = (byte)((medic.isExhausted())?1:0);
            byte haveExhausted = (byte)((medic.haveBeenExhausted())?1:0);
            byte saveData[] = {(byte)(medic.getStamina()), (byte)(medic.getBusyTurn()), (byte)(medic.getExhaustedTurn()), idle, exhausted, haveExhausted};
            saveByteBuf.put(saveData);
        }
    }
    public static Hospital loadHospital() throws IOException, FileNotFoundException
    {
        if(!saveFile.exists())
            throw new FileNotFoundException("找不到存檔，請確定同目錄下存在" + savePath + "檔案！");
        FileInputStream saveFI = new FileInputStream(saveFile);
        ByteBuffer loadByteBuf = ByteBuffer.allocate((int)(saveFile.length()));
        loadByteBuf.clear();
        FileChannel loadChannel = saveFI.getChannel();
        while(loadChannel.read(loadByteBuf) > 0);
        loadChannel.close();
        saveFI.close();
        loadByteBuf.flip();
        byte savePrefixCheck[] = new byte[4];
        loadByteBuf.get(savePrefixCheck);
        if(!(Arrays.equals(savePrefix, savePrefixCheck)))
            throw new FileNotFoundException("非正確存檔格式，請確定" + savePath + "為本程式產生的存檔！");
        byte medicsLength[] = new byte[4];
        loadByteBuf.get(medicsLength);
        Hospital hospital = new Hospital(medicsLength[0], medicsLength[1], medicsLength[2], medicsLength[3]);
        hospital.recursiveLoadToByteBuffer(hospital.surgeons, loadByteBuf);
        hospital.recursiveLoadToByteBuffer(hospital.physicians, loadByteBuf);
        hospital.recursiveLoadToByteBuffer(hospital.nurses, loadByteBuf);
        hospital.recursiveLoadToByteBuffer(hospital.anesthetists, loadByteBuf);
        System.out.println("讀檔完成！");
        return hospital;
    }
    private void recursiveLoadToByteBuffer(MedicalPersonnel[] medics, ByteBuffer loadByteBuf)
    {
        for(MedicalPersonnel medic:medics)
        {
            byte saveData[] = new byte[medicNeededSaveDataNum];
            loadByteBuf.get(saveData);
            boolean idle = (saveData[3] == (byte)1);
            boolean exhausted = (saveData[4] == (byte)1);
            boolean haveExhausted = (saveData[5] == (byte)1);
            medic.readToStatus(saveData[0], saveData[1], saveData[2], idle, exhausted, haveExhausted);
        }
    }
    public MedicalPersonnel[] getAvaliable(MedicalPersonnel[] medic, int num, String skillName, boolean emergency)
    {
        MedicalPersonnel avaliableMedic[] = new MedicalPersonnel[num];
        int medicBeAvaliable = 0;
        for(int i=0; i < medic.length; i++)
        {
            //當醫護人員非閒置或體力不足以執行動作時，跳過
            if((!medic[i].isIdle()) || (!medic[i].enoughStamina(skillName)))
                continue;
            //若是目前尚未滿員，直接先選擇
            if(medicBeAvaliable < num)
            {
                avaliableMedic[medicBeAvaliable] = medic[i];
                medicBeAvaliable++;
            }
            //滿員後，開始篩選是否為體力值最大的人員
            else
            {
                for(int j=0; j < num; j++)
                {
                    //當醫護人員體力值比任一被選擇人員的體力值多時
                    if(medic[i].getStamina() > avaliableMedic[j].getStamina())
                    {
                        avaliableMedic[j] = medic[i];
                        break;
                    }
                }
            }
        }
        //檢測醫療人員是否不足
        if(medicBeAvaliable < num)
        {
            //當醫療人員不足時
            //若非緊急，返回空值，代表無法進行
            if(!emergency)
                return null;
            //當狀態為緊急時，檢查是否有人員可透支體力
            else
            {
                int exhaustedaIndexStart = medicBeAvaliable;
                for(int i=0; i < medic.length; i++)
                {
                    //忽略已經透支過、非閒置狀態或者體力值足以支付技能的人員，這三種人不會透支
                    if(medic[i].haveBeenExhausted() || medic[i].enoughStamina(skillName) || (!medic[i].isIdle()))
                        continue;
                    //若是目前尚未滿員，直接先選擇
                    if(medicBeAvaliable < num)
                    {
                        avaliableMedic[medicBeAvaliable] = medic[i];
                        medicBeAvaliable++;
                    }
                    //滿員後，開始篩選是否為體力值最大的可透支人員
                    else
                    {
                        for(int j=exhaustedaIndexStart; j < num; j++)
                        {
                            if(medic[i].getStamina() > avaliableMedic[j].getStamina())
                            {
                                avaliableMedic[j] = medic[i];
                                break;
                            }
                        }
                    }
                }
                //若經過尋找透支人員階段後仍舊不足，返回空值，代表無法進行
                if(medicBeAvaliable < num)
                    return null;
                else
                    return avaliableMedic;
            }
        }
        else
            return avaliableMedic;
    }
    public boolean dealWithIllness(String therapy)
    {
        boolean success = false;
        switch(therapy)
        {
            case "medical":
            {
                MedicalPersonnel physicianexe[] = getAvaliable(physicians, 1, "看診", false);
                MedicalPersonnel nurseexe[] = getAvaliable(nurses, 1, "一般照護", false);
                if(physicianexe != null && nurseexe != null)
                {
                    recursiveExecute(physicianexe, "看診");
                    recursiveExecute(nurseexe, "一般照護");
                    success = true;
                }
                break;
            }
            case "wrap":
            {
                MedicalPersonnel surgeonexe[] = getAvaliable(surgeons, 1, "看診", false);
                MedicalPersonnel nurseexe[] = getAvaliable(nurses, 1, "一般照護", false);
                if(surgeonexe != null && nurseexe != null)
                {
                    recursiveExecute(surgeonexe, "看診");
                    recursiveExecute(nurseexe, "一般照護");
                    success = true;
                }
                break;
            }
            case "surgery":
            {
                MedicalPersonnel surgeonexe[] = getAvaliable(surgeons, 1, "手術", false);
                MedicalPersonnel nurseexe[] = getAvaliable(nurses, 3, "手術照護", false);
                MedicalPersonnel anesthetistexe[] = getAvaliable(anesthetists, 1, "麻醉", false);
                if(surgeonexe != null && nurseexe != null && anesthetistexe != null)
                {
                    recursiveExecute(surgeonexe, "手術");
                    recursiveExecute(nurseexe, "手術照護");
                    recursiveExecute(anesthetistexe, "麻醉");
                    success = true;
                }
                break;
            }
            case "chemotherapy":
            {
                MedicalPersonnel physicianexe[] = getAvaliable(physicians, 1, "內科治療", false);
                MedicalPersonnel nurseexe[] = getAvaliable(nurses, 1, "一般照護", false);
                if(physicianexe != null && nurseexe != null)
                {
                    recursiveExecute(physicianexe, "內科治療");
                    recursiveExecute(nurseexe, "一般照護");
                    success = true;
                }
                break;
            }
            case "emergency-surgery":
            {
                MedicalPersonnel surgeonexe[] = getAvaliable(surgeons, 1, "手術", true);
                MedicalPersonnel nurseexe[] = getAvaliable(nurses, 3, "手術照護", true);
                MedicalPersonnel anesthetistexe[] = getAvaliable(anesthetists, 1, "麻醉", true);
                if(surgeonexe != null && nurseexe != null && anesthetistexe != null)
                {
                    recursiveExecute(surgeonexe, "手術");
                    recursiveExecute(nurseexe, "手術照護");
                    recursiveExecute(anesthetistexe, "麻醉");
                    success = true;
                }
                break;
            }
            case "first-aid":
            {
                MedicalPersonnel physicianexe[] = getAvaliable(physicians, 1, "急救治療", true);
                MedicalPersonnel nurseexe[] = getAvaliable(nurses, 2, "一般照護", true);
                if(physicianexe != null && nurseexe != null)
                {
                    recursiveExecute(physicianexe, "急救治療");
                    recursiveExecute(nurseexe, "一般照護");
                    success = true;
                }
                break;
            }
            default:
        }
        return success;
    }
    public void turnOver()
    {
        for(Surgeon surgeon:surgeons)
            surgeon.turnOver();
        for(Physician physician:physicians)
            physician.turnOver();
        for(Nurse nurse:nurses)
            nurse.turnOver();
        for(Anesthetist anesthetist:anesthetists)
            anesthetist.turnOver();
    }
    private void recursiveExecute(MedicalPersonnel[] medic, String skillName)
    {
        for(MedicalPersonnel exe:medic)
        {
            //使需要透支的人員透支
            if(!exe.enoughStamina(skillName))
                exe.setToExhaust();
            exe.executeSkill(skillName);
        }
    }
    private String recursiveGetStatusString(MedicalPersonnel[] medics, String status)
    {
        String statusSentence = "%s%d    體力：%s    狀態：%s%n";
        for(int i=0; i < medics.length; i++)
        {
            String stamina = null;
            if(medics[i].isExhausted())
                stamina = "透支";
            else
                stamina = Integer.toString(medics[i].getStamina());
            status += String.format(statusSentence, medics[i].getJobName(), (i+1), stamina, medics[i].getStatusString());
        }
        return status;
    }
}
