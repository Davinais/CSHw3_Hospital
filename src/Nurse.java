public class Nurse extends MedicalPersonnel
{
    public Nurse()
    {
        //設定最大體力與最大恢復力
        super("護理師", 90, 20);
        isIdle = true;
        addSkills("一般照護", -10, 1);
        addSkills("手術照護", -30, 3);
    }
}
