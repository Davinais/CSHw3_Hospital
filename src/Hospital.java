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
    public MedicalPersonnel getMostEnergetic(MedicalPersonnel[] medic)
    {
        int mostEnergeticIndex = 0;
        for(int i=0; i < medic.length; i++)
        {
            if(medic[i].getStamina() >= medic[mostEnergeticIndex].getStamina())
                mostEnergeticIndex = i;
        }
        return medic[mostEnergeticIndex];
    }
}
