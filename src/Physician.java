public class Physician extends MedicalPersonnel
{
    public Physician()
    {
        //設定最大體力與最大恢復力
        super("內科醫生", 100, 25, 3);
        isIdle = true;
        skills.addSkill("內科治療", -20, 1);
        skills.addSkill("看診", -10, 1);
        skills.addSkill("急救治療", -30, 1);
    }
}
