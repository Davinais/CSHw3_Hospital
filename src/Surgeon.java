public class Surgeon extends MedicalPersonnel
{
    public Surgeon()
    {
        //設定最大體力與最大恢復力
        super("外科醫生", 100, 30);
        isIdle = true;
        addSkills("手術", -50, 3);
        addSkills("看診", -10, 1);
    }
}
