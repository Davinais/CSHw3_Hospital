import javafx.application.Application;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.TilePane;
import javafx.scene.Scene;
import javafx.stage.Stage;

class MedicStatusBox extends HBox
{
    private MedicalPersonnel medic;
    public MedicStatusBox(MedicalPersonnel medic)
    {
        super();
        this.medic = medic;
    }
}
class MedicPane extends TilePane
{
    private MedicalPersonnel[] medics;
    private MedicStatusBox[] medicStats;
    public MedicPane(MedicalPersonnel[] medics)
    {
        super();
        this.medics = medics;
        medicStats = new MedicStatusBox[medics.length];
        for(int i=0; i < medicStats.length; i++)
            medicStats[i] = new MedicStatusBox(medics[i]);
    }
}
class MedicTab extends Tab
{
    private Hospital nckuHospital;
    private MedicPane medicPane;
    private int job;
    public MedicTab(Hospital hospital, String jobName)
    {
        super();
        nckuHospital = hospital;
        switch(jobName)
        {
            case "Surgeon":
                setText("外科醫生");
                job = 0;
                break;
            case "Physician":
                setText("內科醫生");
                job = 1;
                break;
            case "Nurse":
                setText("護理師");
                job = 2;
                break;
            case "Anesthetist":
                setText("麻醉師");
                job = 3;
                break;
            default:
        }
        medicPane = new MedicPane(nckuHospital.getMedicList(jobName));
    }
}
public class HospitalGUI extends Application
{
    @Override
    public void start(Stage stage)
    {
        TabPane hospitalPane = new TabPane();
        Scene scene = new Scene(hospitalPane);
        stage.setScene(scene);
        stage.setTitle("果然我的醫院悠閒喜劇搞錯了。");
        stage.show();
    }
}