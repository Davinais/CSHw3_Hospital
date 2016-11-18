public class Hospital
{
    Surgeon surgeon[];
    Physician physician[];
    Nurse nurse[];
    Anesthetist anesthetist[];
    //醫院建構函數，順序為外科醫生數量->內科醫生數量->護士數量->麻醉師數量
    public Hospital(int surgeon_num, int physician_num, int nurse_num, int anesthetist_num)
    {
        surgeon = new Surgeon[surgeon_num];
        for(int i=0; i < surgeon_num; i++)
            surgeon[i] = new Surgeon();
        physician = new Physician[physician_num];
        for(int i=0; i < physician_num; i++)
            physician[i] = new Physician();
        nurse = new Nurse[nurse_num];
        for(int i=0; i < nurse_num; i++)
            nurse[i] = new Nurse();
        anesthetist = new Anesthetist[anesthetist_num];
        for(int i=0; i < anesthetist_num; i++)
            anesthetist[i] = new Anesthetist();
    }
    public MedicalPersonnel[] getAvaliable(MedicalPersonnel[] medic, int num, String skillName)
    {
        MedicalPersonnel avaliableMedic[] = new MedicalPersonnel[num];
        int medicBeAvaliable = 0;
        for(int i=0; i < medic.length; i++)
        {
            if(medicBeAvaliable < num)
            {
                //當醫護人員為閒置且體力足以執行動作
                if(medic[i].isIdle() && medic[i].enoughStamina(skillName))
                {
                    avaliableMedic[medicBeAvaliable] = medic[i];
                    medicBeAvaliable++;
                }
            }
            else
            {
                for(int j=0; j < num; j++)
                {
                    //當醫護人員為閒置且體力足以執行動作且體力值比於任一被選擇人員的體力值多時
                    if(medic[i].isIdle() && medic[i].enoughStamina(skillName) && (medic[i].getStamina() > avaliableMedic[j].getStamina()))
                    {
                        avaliableMedic[j] = medic[i];
                        break;
                    }
                }
            }
        }
        if(medicBeAvaliable < num)
            return null;
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
                MedicalPersonnel physicianexe[] = getAvaliable(physician, 1, "看診");
                MedicalPersonnel nurseexe[] = getAvaliable(nurse, 1, "一般照護");
                if(physicianexe != null && nurseexe != null)
                {
                    recursiveExecute(physicianexe, "看診");
                    recursiveExecute(nurse, "一般照護");
                    success = true;
                }
                break;
            }
            case "wrap":
            {
                MedicalPersonnel surgeonexe[] = getAvaliable(surgeon, 1, "看診");
                MedicalPersonnel nurseexe[] = getAvaliable(nurse, 1, "一般照護");
                if(surgeonexe != null && nurseexe != null)
                {
                    recursiveExecute(surgeonexe, "看診");
                    recursiveExecute(nurse, "一般照護");
                    success = true;
                }
                break;
            }
            case "surgery":
            {
                MedicalPersonnel surgeonexe[] = getAvaliable(surgeon, 1, "手術");
                MedicalPersonnel nurseexe[] = getAvaliable(nurse, 3, "開刀照護");
                MedicalPersonnel anesthetistexe[] = getAvaliable(anesthetist, 1, "麻醉");
                if(surgeonexe != null && nurseexe != null)
                {
                    recursiveExecute(surgeonexe, "手術");
                    recursiveExecute(nurse, "開刀照護");
                    recursiveExecute(anesthetist, "麻醉");
                    success = true;
                }
                break;
            }
            case "chemotherapy":
            {
                MedicalPersonnel physicianexe[] = getAvaliable(physician, 1, "內科治療");
                MedicalPersonnel nurseexe[] = getAvaliable(nurse, 1, "一般照護");
                if(physicianexe != null && nurseexe != null)
                {
                    recursiveExecute(physicianexe, "內科治療");
                    recursiveExecute(nurse, "一般照護");
                    success = true;
                }
                break;
            }
            case "emergency-surgery":
            case "first-aid":
            default:
        }
        return success;
    }
    private void recursiveExecute(MedicalPersonnel[] medic, String skillName)
    {
        for(MedicalPersonnel exe:medic)
            exe.executeSkill(skillName);
    }
}
