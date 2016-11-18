public class Anesthetist extends MedicalPersonnel
{
    public Anesthetist()
    {
        //設定最大體力與最大恢復力
        super("麻醉師", 80, 20);
        addSkill("麻醉", -30, 2);
    }
}
