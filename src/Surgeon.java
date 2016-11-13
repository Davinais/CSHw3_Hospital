public class Surgeon extends MedicalPersonnel
{
    public Surgeon()
    {
        //設定最大體力與最大恢復力
        super(100, 30);
        job = "外科醫生";
        isIdle = false;
        stamina = 100;
    }
}
