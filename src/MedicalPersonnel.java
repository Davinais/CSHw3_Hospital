public abstract class MedicalPersonnel
{
    protected String job;
    protected final int STAMINA_MAX, HEALING, SKILLS_NUM;
    protected int stamina;
    protected int busyTurn;
    protected boolean isIdle, isExhausted;
    protected Skills skills;
    public MedicalPersonnel(String job, int stamina_max, int healing, int skillsNum)
    {
        this.job = job;
        SKILLS_NUM = skillsNum;
        STAMINA_MAX = stamina_max;
        HEALING = healing;
        stamina = STAMINA_MAX;
        skills = new Skills(skillsNum);
    }
    public void executeSkills(String skillName)
    {
        int skillCostTurn[] = skills.getCostTurnByName(skillName);
        stamina -= skillCostTurn[0];
        busyTurn += skillCostTurn[1];
    }
    public int getMaxStamina()
    {
        return STAMINA_MAX;
    }
    public int getHealing()
    {
        return HEALING;
    }
    public int getStamina()
    {
        return stamina;
    }
}
