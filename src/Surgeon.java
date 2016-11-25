public class Surgeon extends MedicalPersonnel
{
    private static Skill nativeSkills[];
    public Surgeon()
    {
        //設定最大體力與最大恢復力
        super("外科醫生", 100, 30);
        for(Skill nativeSkill:nativeSkills)
            addSkill(nativeSkill);
    }
    static
    {
        nativeSkills = new Skill[2];
        nativeSkills[0] = new Skill("手術", -50, 3);
        nativeSkills[1] = new Skill("看診", -10, 1);
    }
}
