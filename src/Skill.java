public class Skill
{
    private String skillName;
    private int skillStaminaCost, skillNeededTurn;
    public Skill(String skillName, int skillStaminaCost, int skillNeededTurn)
    {
        this.skillName = skillName;
        this.skillStaminaCost = skillStaminaCost;
        this.skillNeededTurn = skillNeededTurn;
    }
    public int getStaminaCost()
    {
        return skillStaminaCost;
    }
    public int getNeededTurn()
    {
        return skillNeededTurn;
    }
}
