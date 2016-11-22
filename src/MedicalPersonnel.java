import java.util.HashMap;

public abstract class MedicalPersonnel
{
    protected String job;
    protected final int STAMINA_MAX, HEALING, EXHAUSTED_TURN_MAX;
    protected int stamina;
    protected int busyTurn, exhaustedTurn;
    protected boolean idle, exhausted, haveExhausted;
    protected HashMap<String, Skill> skills = new HashMap<String, Skill>();
    public MedicalPersonnel(String job, int stamina_max, int healing)
    {
        this.job = job;
        STAMINA_MAX = stamina_max;
        HEALING = healing;
        EXHAUSTED_TURN_MAX = 5;
        stamina = STAMINA_MAX;
        busyTurn = 0;
        exhaustedTurn = 0;
        idle = true;
        exhausted = false;
        haveExhausted = false;
    }
    protected void addSkill(String skillName, int skillStaminaCost, int skillNeededTurn)
    {
        skills.put(skillName, new Skill(skillName, skillStaminaCost, skillNeededTurn));
    }
    protected void readToStatus(int stamina, int busyTurn, int exhaustedTurn, boolean idle, boolean exhausted, boolean haveExhausted)
    {
        this.stamina = stamina;
        this.busyTurn = busyTurn;
        this.exhaustedTurn = exhaustedTurn;
        this.idle = idle;
        this.exhausted = exhausted;
        this.haveExhausted = haveExhausted;
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
            return String.format("透支[%d回合]", (busyTurn + exhaustedTurn));
        else if(idle)
            return "閒置";
        else
            return String.format("忙碌[%d回合]", busyTurn);
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
            exhaustedTurn = EXHAUSTED_TURN_MAX;
        }
    }
    public void turnOver()
    {
        if(idle && exhausted)
        {
            exhaustedTurn--;
            if(exhaustedTurn == 0)
            {
                exhausted = false;
                stamina = STAMINA_MAX;
            }
        }
        else if(idle)
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
