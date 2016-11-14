import java.util.Arrays;

public class Skills
{
    private String skillsName[];
    private int skillsStaminaCost[], skillsNeededTurn[];
    private int skillsBeSet;
    private final int SKILLS_AMOUNT;
    public Skills(int skillsAmount)
    {
        SKILLS_AMOUNT = skillsAmount;
        skillsName = new String[skillsAmount];
        skillsStaminaCost = new int[skillsAmount];
        skillsNeededTurn = new int[skillsAmount];
        skillsBeSet = 0;
    }
    public void addSkill(String skillName, int skillCost, int skillTurn)
    {
        if(skillsBeSet < SKILLS_AMOUNT)
        {
            skillsName[skillsBeSet] = skillName;
            skillsStaminaCost[skillsBeSet] = skillCost;
            skillsNeededTurn[skillsBeSet] = skillTurn;
            skillsBeSet++;
        }
    }
    public int[] getCostTurnByName(String skillName)
    {
        int index = Arrays.asList(skillsName).indexOf(skillName);
        if(index >= 0)
            return new int[] {skillsStaminaCost[index], skillsNeededTurn[index]};
        else
            return new int[] {-1, -1};
    }
}
