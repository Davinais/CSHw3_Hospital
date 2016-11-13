public class Physician extends MedicalPersonnel
{
    public Physician()
    {
        //設定最大體力與最大恢復力
        super(100, 25);
        job = "內科醫生";
        isIdle = false;
        stamina = 100;
    }
}
