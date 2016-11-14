public class Nurse extends MedicalPersonnel
{
    public Nurse()
    {
        //設定最大體力與最大恢復力
        super("護理師", 90, 20, 2);
        isIdle = true;
        skills.addSkill("一般照護", -10, 1);
        skills.addSkill("手術照護", -30, 3);
    }
}
