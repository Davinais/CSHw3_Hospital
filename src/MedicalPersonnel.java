import java.util.HashMap;

public abstract class MedicalPersonnel
{
    protected String job;
    protected final int STAMINA_MAX, HEALING;
    protected int stamina;
    protected int busyTurn;
    protected boolean idle, exhausted, haveExhausted;
    protected HashMap<String, Skill> skills = new HashMap<String, Skill>();
    public MedicalPersonnel(String job, int stamina_max, int healing)
    {
        this.job = job;
        STAMINA_MAX = stamina_max;
        HEALING = healing;
        stamina = STAMINA_MAX;
        idle = true;
        exhausted = false;
        haveExhausted = false;
    }
    protected void addSkill(String skillName, int skillStaminaCost, int skillNeededTurn)
    {
        skills.put(skillName, new Skill(skillName, skillStaminaCost, skillNeededTurn));
    }
    public boolean enoughStamina(String skillName)
    {
        Skill exe = skills.get(skillName);
        if(exhausted)
            return false;
        else if(stamina + exe.getStaminaCost() < 0)
            return false;
        else
            return true;
    }
    public void executeSkill(String skillName)
    {
        Skill exe = skills.get(skillName);
        stamina += exe.getStaminaCost();
        busyTurn += exe.getNeededTurn();
        idle = false;
    }
    public void staminaRecover()
    {
        stamina += HEALING;
        if(stamina > STAMINA_MAX)
            stamina = STAMINA_MAX;
    }
    public boolean isIdle()
    {
        return idle;
    }
    public boolean isExhausted()
    {
        return exhausted;
    }
    public boolean haveBeenExhausted()
    {
        return haveExhausted;
    }
    public String getStatusString()
    {
        if(exhausted)
            return "透支";
        else if(idle)
            return "閒置";
        else
            return "忙碌";
    }
    public String getJobName()
    {
        return job;
    }
    public void setToExhaust()
    {
        if(!haveExhausted)
        {
            haveExhausted = true;
            exhausted = true;
        }
    }
    public void turnOver()
    {
        if(idle)
            staminaRecover();
        else
        {
            busyTurn--;
            if(busyTurn == 0)
                idle = true;
        }
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
