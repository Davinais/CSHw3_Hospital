public class Surgeon extends MedicalPersonnel
{
    public Surgeon()
    {
        //設定最大體力與最大恢復力
        super("外科醫生", 100, 30, 2);
        isIdle = true;
        skills.addSkill("手術", -50, 3);
        skills.addSkill("看診", -10, 1);
    }
}
