public class Hospital
{
    Surgeon surgeons[];
    Physician physicians[];
    Nurse nurses[];
    Anesthetist anesthetists[];
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
                {
                    //使需要透支的人員透支
                    for(int e=exhaustedaIndexStart; e < num; e++)
                        avaliableMedic[e].setToExhaust();
                    return avaliableMedic;
                }
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
                    recursiveExecute(nurses, "一般照護");
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
                    recursiveExecute(nurses, "一般照護");
                    success = true;
                }
                break;
            }
            case "surgery":
            {
                MedicalPersonnel surgeonexe[] = getAvaliable(surgeons, 1, "手術", false);
                MedicalPersonnel nurseexe[] = getAvaliable(nurses, 3, "開刀照護", false);
                MedicalPersonnel anesthetistexe[] = getAvaliable(anesthetists, 1, "麻醉", false);
                if(surgeonexe != null && nurseexe != null)
                {
                    recursiveExecute(surgeonexe, "手術");
                    recursiveExecute(nurses, "開刀照護");
                    recursiveExecute(anesthetists, "麻醉");
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
                    recursiveExecute(nurses, "一般照護");
                    success = true;
                }
                break;
            }
            case "emergency-surgery":
            {
                MedicalPersonnel surgeonexe[] = getAvaliable(surgeons, 1, "手術", true);
                MedicalPersonnel nurseexe[] = getAvaliable(nurses, 3, "開刀照護", true);
                MedicalPersonnel anesthetistexe[] = getAvaliable(anesthetists, 1, "麻醉", true);
                if(surgeonexe != null && nurseexe != null)
                {
                    recursiveExecute(surgeonexe, "手術");
                    recursiveExecute(nurses, "開刀照護");
                    recursiveExecute(anesthetists, "麻醉");
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
                    recursiveExecute(nurses, "一般照護");
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
            exe.executeSkill(skillName);
    }
}
