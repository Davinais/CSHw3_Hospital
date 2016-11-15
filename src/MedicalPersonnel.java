import java.util.HashMap<K,V>;

public abstract class MedicalPersonnel
{
    protected String job;
    protected final int STAMINA_MAX, HEALING;
    protected int stamina;
    protected int busyTurn;
    protected boolean isIdle, isExhausted;
    protected HashMap<String, Skill> skills = new HashMap<String, Skill>();
    public MedicalPersonnel(String job, int stamina_max, int healing)
    {
        this.job = job;
        STAMINA_MAX = stamina_max;
        HEALING = healing;
        stamina = STAMINA_MAX;
    }
    public void addSkills(String skillName, int skillStaminaCost, int skillNeededTurn)
    {
        skills.put(skillName, new Skill(skillName, skillStaminaCost, skillNeededTurn));
    }
    public void executeSkill(String skillName)
    {
        Skill exe = skills.get(skillName);
        stamina -= exe.getStaminaCost();
        busyTurn += exe.getNeededTurn();
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
