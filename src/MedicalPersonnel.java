public abstract class MedicalPersonnel
{
    protected String job;
    protected final int STAMINA_MAX, HEALING;
    protected int stamina;
    protected boolean isIdle, isExhausted;
    public MedicalPersonnel(int stamina_max, int healing)
    {
        STAMINA_MAX = stamina_max;
        HEALING = healing;
    }
    public int getMaxStamina()
    {
        return STAMINA_MAX;
    }
    public int getHealing()
    {
        return HEALING;
    }
}
