public class Nurse extends MedicalPersonnel
{
    private static Skill nativeSkills[];
    public Nurse()
    {
        //設定最大體力與最大恢復力
        super("護理師", 90, 20);
        for(Skill nativeSkill:nativeSkills)
            addSkill(nativeSkill);
    }
    static
    {
        nativeSkills = new Skill[2];
        nativeSkills[0] = new Skill("一般照護", -10, 1);
        nativeSkills[1] = new Skill("手術照護", -30, 3);
    }
}
