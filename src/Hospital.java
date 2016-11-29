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
    private String avaliableTherapy[];
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
        avaliableTherapy = new String[] {"medical", "wrap", "surgery", "chemotherapy", "emergency-surgery", "first-aid"};
    }
    public void printStatus()
    {
        final int statusPerRow = 6;
        //計算所需要的總行數
        int lineNeeded = (int)(Math.ceil((surgeons.length+physicians.length+nurses.length+anesthetists.length)/(statusPerRow*1.0)))*5+1;
        //做出分隔線字串，以取代char陣列中的null為"-"的方式實現
        String separateLine = new String(new char[79]).replace("\0", "-");
        StringBuilder statusBuf[] = new StringBuilder[lineNeeded];
        for(int i=0; i < lineNeeded; i++)
            statusBuf[i] = new StringBuilder();
        //將對應為分隔線行數的StringBuilder存入分隔線字串
        for(int i=0; i < lineNeeded; i+=5)
            statusBuf[i].append(separateLine);
        //將目前處理中醫師的座標初始化為(0, 0)
        int nowPos[] = {0, 0};
        //分別處理四類醫師中每位醫生的狀態字串
        formatStatusStringByMedics(surgeons, statusBuf, nowPos);
        formatStatusStringByMedics(physicians, statusBuf, nowPos);
        formatStatusStringByMedics(nurses, statusBuf, nowPos);
        formatStatusStringByMedics(anesthetists, statusBuf, nowPos);
        //輸出字串
        for(int line=0; line < statusBuf.length; line++)
            System.out.println(statusBuf[line].toString());
    }
    private void formatStatusStringByMedics(MedicalPersonnel[] medics, StringBuilder[] statusBuf, int[] nowPos)
    {
        int space = 13;
        for(int i=0; i < medics.length; i++)
        {
            //計算此醫師的字串要從第幾行開始
            int lineSkip = nowPos[0]*5;
            //以下皆使用stringPadding()進行排版，確保最後的字串長度都與space相同，值得注意的是中文字算雙字元長，而半形英文字母或數字則為單字元長
            statusBuf[lineSkip+1].append(StringTools.stringPadding(medics[i].getJobName() + (i+1), space));
            if(medics[i].isExhausted())
                statusBuf[lineSkip+2].append(StringTools.stringPadding("體力:透支", space));
            else
                statusBuf[lineSkip+2].append(StringTools.stringPadding("體力:" + medics[i].getStamina() + "/" + medics[i].getMaxStamina(), space));
            statusBuf[lineSkip+3].append(StringTools.stringPadding("狀態:" + medics[i].getStatusString(), space));
            int waitTurn = medics[i].getWaitTurn();
            //若等待的回合數>0，則顯示出來
            if(waitTurn > 0)
                statusBuf[lineSkip+4].append(StringTools.stringPadding("[尚需" + waitTurn + "回合]", space));
            else
                statusBuf[lineSkip+4].append(StringTools.stringPadding("", space));
            //座標欄位數+1
            nowPos[1]++;
            //當座標欄位超過每列應有欄位時，將座標設定為下一列的最初始欄位
            if(nowPos[1] >= 6)
            {
                nowPos[0]++;
                nowPos[1] = 0;
            }
        }
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
        //檢查檔案是否存在，若否，則建立新檔案
        if(!saveFile.exists())
            saveFile.createNewFile();
        //建立byte緩衝區，並且分配其大小與應儲存資料大小相等
        ByteBuffer saveByteBuf = ByteBuffer.allocate(savePrefix.length+medicsLengthSpace+(surgeons.length+physicians.length+nurses.length+anesthetists.length)*medicNeededSaveDataNum);
        //為保險，先清除緩衝區資料
        saveByteBuf.clear();
        //將存檔前綴放入緩衝區
        saveByteBuf.put(savePrefix);
        //取得各類醫師的人數並轉型為byte
        byte medicsLength[] = {(byte)surgeons.length, (byte)physicians.length, (byte)nurses.length, (byte)anesthetists.length};
        //將醫師人數放入緩衝區
        saveByteBuf.put(medicsLength);
        //以下分別將四類醫師中每位醫師個別的資料放入緩衝區
        saveToByteBuffer(surgeons, saveByteBuf);
        saveToByteBuffer(physicians, saveByteBuf);
        saveToByteBuffer(nurses, saveByteBuf);
        saveToByteBuffer(anesthetists, saveByteBuf);
        //將緩衝區指標歸為初始，並且將其limit設在有儲存資料的緩衝區結尾
        saveByteBuf.flip();
        //建立檔案輸出流
        try(FileOutputStream saveFO = new FileOutputStream(saveFile))
        {
            //建立檔案溝通頻道
            try(FileChannel saveChannel = saveFO.getChannel())
            {
                //當緩衝區還有資料未存入時，將其寫入檔案
                while(saveByteBuf.hasRemaining())
                    saveChannel.write(saveByteBuf);
            }
        }
        System.out.println("存檔完成！");
    }
    private void saveToByteBuffer(MedicalPersonnel[] medics, ByteBuffer saveByteBuf)
    {
        for(MedicalPersonnel medic:medics)
        {
            //以下三行將boolean值以 0=false, 1=true 的方式儲存
            byte idle = (byte)((medic.isIdle())?1:0);
            byte exhausted = (byte)((medic.isExhausted())?1:0);
            byte haveExhausted = (byte)((medic.haveBeenExhausted())?1:0);
            //取得醫師的所有資料欄位，並且強制轉型到byte並存放到陣列中，由於在本作業中的數字皆不會超過128，因此不用擔心overflow的問題
            byte saveData[] = {(byte)(medic.getStamina()), (byte)(medic.getBusyTurn()), (byte)(medic.getExhaustedTurn()), idle, exhausted, haveExhausted};
            //將資料放入緩衝區
            saveByteBuf.put(saveData);
        }
    }
    public static Hospital loadHospital() throws IOException, FileNotFoundException
    {
        //檢查檔案是否存在，若否，則拋出檔案不存在例外
        if(!saveFile.exists())
            throw new FileNotFoundException("找不到存檔，請確定同目錄下存在" + savePath + "檔案！");
        ByteBuffer loadByteBuf;
        int saveFileLength;
        //建立檔案輸入流
        try(FileInputStream saveFI = new FileInputStream(saveFile))
        {
            //將檔案的位元組大小存入變數中
            saveFileLength = (int)(saveFile.length());
            //若檔案位元組大小比存檔前綴的長度+儲存醫師人數的長度還小，即比8小時，此檔案必定為錯誤格式，此時拋出檔案不存在例外
            if(saveFileLength < savePrefix.length + medicsLengthSpace)
                throw new FileNotFoundException("非正確存檔格式，請確定" + savePath + "為本程式產生的存檔！");
            //建立byte緩衝區，並且分配其大小與存檔大小相等
            loadByteBuf = ByteBuffer.allocate(saveFileLength);
            loadByteBuf.clear();
            //建立檔案溝通頻道
            try(FileChannel loadChannel = saveFI.getChannel())
            {
                //一口氣將檔案讀完
                while(loadChannel.read(loadByteBuf) > 0);
            }
        }
        //將緩衝區指標歸為初始，並且將其limit設在有儲存資料的緩衝區結尾
        loadByteBuf.flip();
        //建立一byte陣列，用來讀取前四碼存檔前綴
        byte savePrefixCheck[] = new byte[savePrefix.length];
        loadByteBuf.get(savePrefixCheck);
        //若存檔前綴不相等，即為錯誤格式，拋出檔案不存在例外
        if(!(Arrays.equals(savePrefix, savePrefixCheck)))
            throw new FileNotFoundException("非正確存檔格式，請確定" + savePath + "為本程式產生的存檔！");
        //建立一byte陣列，用來讀取各類醫師人數
        byte medicsLength[] = new byte[4];
        loadByteBuf.get(medicsLength);
        //若是將存檔前綴的大小4+儲存醫師人數的大小4再加上總醫師人數乘上每位醫師應儲存資料大小的加總不等於檔案大小時，代表有多餘或缺失的訊息，為錯誤格式，拋出檔案不存在例外
        if(saveFileLength != (savePrefix.length+medicsLengthSpace+(medicsLength[0]+medicsLength[1]+medicsLength[2]+medicsLength[3])*medicNeededSaveDataNum))
            throw new FileNotFoundException("非正確存檔格式，請確定" + savePath + "為本程式產生的存檔！");
        //建立醫院物件，參數為各類醫師人數
        Hospital hospital = new Hospital(medicsLength[0], medicsLength[1], medicsLength[2], medicsLength[3]);
        //將每位醫師的資料分別讀回醫療人員物件中
        hospital.loadFromByteBuffer(hospital.surgeons, loadByteBuf);
        hospital.loadFromByteBuffer(hospital.physicians, loadByteBuf);
        hospital.loadFromByteBuffer(hospital.nurses, loadByteBuf);
        hospital.loadFromByteBuffer(hospital.anesthetists, loadByteBuf);
        System.out.println("讀檔完成！");
        return hospital;
    }
    private void loadFromByteBuffer(MedicalPersonnel[] medics, ByteBuffer loadByteBuf)
    {
        for(MedicalPersonnel medic:medics)
        {
            //建立一byte陣列，用來讀取醫師資料
            byte saveData[] = new byte[medicNeededSaveDataNum];
            loadByteBuf.get(saveData);
            //將被轉化為0與1的boolean值轉回boolean
            boolean idle = (saveData[3] == (byte)1);
            boolean exhausted = (saveData[4] == (byte)1);
            boolean haveExhausted = (saveData[5] == (byte)1);
            //將讀取的資料存放進醫師物件的資料欄位中
            medic.readToStatus(saveData[0], saveData[1], saveData[2], idle, exhausted, haveExhausted);
        }
    }
    public String[] getAvaliableTherapy()
    {
        return Arrays.copyOf(avaliableTherapy, avaliableTherapy.length);
    }
    private MedicalPersonnel[] getAvaliableMedics(MedicalPersonnel[] medic, int num, String skillName, boolean emergency)
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
                MedicalPersonnel physicianexe[] = getAvaliableMedics(physicians, 1, "看診", false);
                MedicalPersonnel nurseexe[] = getAvaliableMedics(nurses, 1, "一般照護", false);
                //若陣列物件皆非null，代表可執行此治療手段
                if(physicianexe != null && nurseexe != null)
                {
                    executeByMedics(physicianexe, "看診");
                    executeByMedics(nurseexe, "一般照護");
                    success = true;
                }
                break;
            }
            case "wrap":
            {
                MedicalPersonnel surgeonexe[] = getAvaliableMedics(surgeons, 1, "看診", false);
                MedicalPersonnel nurseexe[] = getAvaliableMedics(nurses, 1, "一般照護", false);
                //若陣列物件皆非null，代表可執行此治療手段
                if(surgeonexe != null && nurseexe != null)
                {
                    executeByMedics(surgeonexe, "看診");
                    executeByMedics(nurseexe, "一般照護");
                    success = true;
                }
                break;
            }
            case "surgery":
            {
                MedicalPersonnel surgeonexe[] = getAvaliableMedics(surgeons, 1, "手術", false);
                MedicalPersonnel nurseexe[] = getAvaliableMedics(nurses, 3, "手術照護", false);
                MedicalPersonnel anesthetistexe[] = getAvaliableMedics(anesthetists, 1, "麻醉", false);
                //若陣列物件皆非null，代表可執行此治療手段
                if(surgeonexe != null && nurseexe != null && anesthetistexe != null)
                {
                    executeByMedics(surgeonexe, "手術");
                    executeByMedics(nurseexe, "手術照護");
                    executeByMedics(anesthetistexe, "麻醉");
                    success = true;
                }
                break;
            }
            case "chemotherapy":
            {
                MedicalPersonnel physicianexe[] = getAvaliableMedics(physicians, 1, "內科治療", false);
                MedicalPersonnel nurseexe[] = getAvaliableMedics(nurses, 1, "一般照護", false);
                //若陣列物件皆非null，代表可執行此治療手段
                if(physicianexe != null && nurseexe != null)
                {
                    executeByMedics(physicianexe, "內科治療");
                    executeByMedics(nurseexe, "一般照護");
                    success = true;
                }
                break;
            }
            case "emergency-surgery":
            {
                MedicalPersonnel surgeonexe[] = getAvaliableMedics(surgeons, 1, "手術", true);
                MedicalPersonnel nurseexe[] = getAvaliableMedics(nurses, 3, "手術照護", true);
                MedicalPersonnel anesthetistexe[] = getAvaliableMedics(anesthetists, 1, "麻醉", true);
                //若陣列物件皆非null，代表可執行此治療手段
                if(surgeonexe != null && nurseexe != null && anesthetistexe != null)
                {
                    executeByMedics(surgeonexe, "手術");
                    executeByMedics(nurseexe, "手術照護");
                    executeByMedics(anesthetistexe, "麻醉");
                    success = true;
                }
                break;
            }
            case "first-aid":
            {
                MedicalPersonnel physicianexe[] = getAvaliableMedics(physicians, 1, "急救治療", true);
                MedicalPersonnel nurseexe[] = getAvaliableMedics(nurses, 2, "一般照護", true);
                //若陣列物件皆非null，代表可執行此治療手段
                if(physicianexe != null && nurseexe != null)
                {
                    executeByMedics(physicianexe, "急救治療");
                    executeByMedics(nurseexe, "一般照護");
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
    private void executeByMedics(MedicalPersonnel[] medic, String skillName)
    {
        for(MedicalPersonnel exe:medic)
        {
            //使需要透支的人員透支
            if(!exe.enoughStamina(skillName))
                exe.setToExhaust();
            exe.executeSkill(skillName);
        }
    }
}
