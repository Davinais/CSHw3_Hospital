import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.FileNotFoundException;

class MedicStatusBox extends HBox
{
    private int index;
    private MedicalPersonnel medic;
    private Image medicImage;
    private Text jobNameText, staminaText, statusText;
    public MedicStatusBox(MedicalPersonnel medic, int index)
    {
        super();
        this.medic = medic;
        this.index = index;
        medicImage = new Image("img/" + medic.getJobName() + ".png");
        ImageView medicImageView = new ImageView(medicImage);
        jobNameText = new Text(medic.getJobName() + index);
        staminaText = new Text();
        statusText = new Text();
        statusUpdate();
        VBox textBox = new VBox(jobNameText, staminaText, statusText);
        getChildren().addAll(medicImageView, textBox);
    }
    public void statusUpdate()
    {
        String staminaContent = "體力：%s";
        String statusContent = "狀態：%s";
        String stamina = null;
        if(medic.isExhausted())
            stamina = "透支";
        else
            stamina = Integer.toString(medic.getStamina());
        staminaText.textProperty().setValue(String.format(staminaContent, stamina));
        statusText.textProperty().setValue(String.format(statusContent, medic.getStatusString()));
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
        {
            medicStats[i] = new MedicStatusBox(medics[i], (i+1));
            getChildren().add(medicStats[i]);
        }
    }
    public void paneUpdate()
    {
        for(MedicStatusBox medicStatus:medicStats)
            medicStatus.statusUpdate();
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
        setContent(medicPane);
        setClosable(false);
    }
    public void tabUpdate()
    {
        medicPane.paneUpdate();
    }
}
public class HospitalGUI extends Application
{
    private final double therapySpace = 20.0, buttonSpace = 5.0;
    private static Hospital hospitalObj;
    private Hospital hospital;

    @Override
    public void start(Stage stage)
    {
        hospital = hospitalObj;
        String medicJobNames[] = {"Surgeon", "Physician", "Nurse", "Anesthetist"};
        MedicTab medictabs[] = new MedicTab[4];
        for(int i=0; i < medictabs.length; i++)
            medictabs[i] = new MedicTab(hospital, medicJobNames[i]);
        TabPane hospitalPane = new TabPane(medictabs[0], medictabs[1], medictabs[2], medictabs[3]);
        ObservableList<String> therapy = FXCollections.observableArrayList("medical", "wrap", "surgery", "chemotherapy", "emergency-surgery", "first-aid");
        ComboBox<String> therapyComboBox = new ComboBox<String>(therapy);
        Button therapyConfirmButton = new Button("確定執行");
        therapyConfirmButton.setOnMouseClicked(event -> {
            hospital.dealWithIllness(therapyComboBox.getValue());
            hospital.turnOver();
            for(MedicTab medictab:medictabs)
                medictab.tabUpdate();
        });
        HBox therapyBox = new HBox(20.0, therapyComboBox, therapyConfirmButton);
        therapyBox.setAlignment(Pos.CENTER);
        Button saveButton = new Button("儲存狀態");
        Button loadButton = new Button("讀取狀態");
        setSLButton(saveButton, loadButton, stage);
        HBox functionalButtonBox = new HBox(buttonSpace, saveButton, loadButton);
        VBox hospitalPageBox = new VBox(hospitalPane, therapyBox, functionalButtonBox);
        Scene scene = new Scene(hospitalPageBox);
        stage.setScene(scene);
        stage.setTitle("果然我的醫院悠閒喜劇搞錯了。");
        stage.show();
    }
    private void setSLButton(Button saveButton, Button loadButton, Stage stage)
    {
        Alert slCompleteAlert = new Alert(Alert.AlertType.INFORMATION);
        slCompleteAlert.setHeaderText(null);
        Alert slErrorAlert = new Alert(Alert.AlertType.ERROR);
        slErrorAlert.setHeaderText(null);
        saveButton.setOnMouseClicked(event -> {
            loadButton.setDisable(true);
            try
            {
                hospital.saveHospital();
                slCompleteAlert.setTitle("儲存狀態");
                slCompleteAlert.setContentText("已儲存至Hospital.sav！");
                slCompleteAlert.showAndWait();
            }
            catch(IOException e)
            {
                slErrorAlert.setTitle("儲存狀態");
                slErrorAlert.setContentText("發生IO例外，儲存失敗！");
                slErrorAlert.showAndWait();
            }
            finally
            {
                loadButton.setDisable(false);
            }
        });
        loadButton.setOnMouseClicked(event -> {
            saveButton.setDisable(true);
            try
            {
                hospitalObj = Hospital.loadHospital();
                slCompleteAlert.setTitle("讀取遊戲");
                slCompleteAlert.setContentText("讀取完成！");
                slCompleteAlert.showAndWait();
                start(stage);
            }
            catch(FileNotFoundException e)
            {
                slErrorAlert.setTitle("讀取狀態");
                slErrorAlert.setContentText(e.getMessage());
                slErrorAlert.showAndWait();
            }
            catch(IOException e)
            {
                slErrorAlert.setTitle("讀取狀態");
                slErrorAlert.setContentText("發生IO例外，讀取失敗！");
                slErrorAlert.showAndWait();
            }
            finally
            {
                saveButton.setDisable(false);
            }
        });
    }
    public static void setHospital(Hospital hospital)
    {
        HospitalGUI.hospitalObj = hospital;
    }
    public static void main(String[] args)
    {
        setHospital(new Hospital(3, 3, 10, 2));
        launch();
    }
}