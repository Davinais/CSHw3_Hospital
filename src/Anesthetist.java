public class Anesthetist extends MedicalPersonnel
{
    private static Skill nativeSkills[];
    public Anesthetist()
    {
        //設定最大體力與最大恢復力
        super("麻醉師", 80, 20);
        for(Skill nativeSkill:nativeSkills)
            addSkill(nativeSkill);
    }
    static
    {
        nativeSkills = new Skill[1];
        nativeSkills[0] = new Skill("麻醉", -30, 2);
    }
}
