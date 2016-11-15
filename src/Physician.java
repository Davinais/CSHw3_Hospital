public class Physician extends MedicalPersonnel
{
    public Physician()
    {
        //設定最大體力與最大恢復力
        super("內科醫生", 100, 25);
        isIdle = true;
        addSkills("內科治療", -20, 1);
        addSkills("看診", -10, 1);
        addSkills("急救治療", -30, 1);
    }
}
