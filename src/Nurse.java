public class Nurse extends MedicalPersonnel
{
    public Nurse()
    {
        //設定最大體力與最大恢復力
        super("護理師", 90, 20);
        idle = true;
        addSkill("一般照護", -10, 1);
        addSkill("手術照護", -30, 3);
    }
}
