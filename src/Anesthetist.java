public class Anesthetist extends MedicalPersonnel
{
    public Anesthetist()
    {
        //設定最大體力與最大恢復力
        super(80, 20);
        job = "麻醉師";
        isIdle = false;
        stamina = 80;
    }
}
