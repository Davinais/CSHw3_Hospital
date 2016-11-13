public class Nurse extends MedicalPersonnel
{
    public Nurse()
    {
        //設定最大體力與最大恢復力
        super(90, 20);
        job = "護理師";
        isIdle = false;
        stamina = 90;
    }
}
