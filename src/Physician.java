public class Physician extends MedicalPersonnel
{
    private static Skill nativeSkills[];
    public Physician()
    {
        //設定最大體力與最大恢復力
        super("內科醫生", 100, 25);
        for(Skill nativeSkill:nativeSkills)
            addSkill(nativeSkill);
    }
    static
    {
        nativeSkills = new Skill[3];
        nativeSkills[0] = new Skill("內科治療", -20, 1);
        nativeSkills[1] = new Skill("看診", -10, 1);
        nativeSkills[2] = new Skill("急救治療", -30, 1);
    }
}
