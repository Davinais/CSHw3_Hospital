import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
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

class StaminaBar extends ProgressBar
{
    private static final String hpBarColors[] = {"low-hp", "medium-hp", "high-hp"};
    public StaminaBar()
    {
        super();
        getStylesheets().add(getClass().getResource("StaminaBar.css").toExternalForm());
        this.progressProperty().addListener((observable, oldValue, newValue) -> {
            double stamina = (newValue == null)?-1.0:newValue.doubleValue();
            if(stamina <= 0.25)
                setBarStyle(hpBarColors[0]);
            else if(stamina <= 0.5)
                setBarStyle(hpBarColors[1]);
            else
                setBarStyle(hpBarColors[2]);
        });
    }
    public void setBarStyle(String style)
    {
        getStyleClass().removeAll(hpBarColors);
        getStyleClass().add(style);
    }
}
class MedicStatusBox extends HBox
{
    private int index;
    private MedicalPersonnel medic;
    private Image medicImage;
    private Text jobNameText, staminaText, statusText, waitTurnText;
    private StaminaBar staminaBar;
    private Timeline staminaTimeline;
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
        waitTurnText = new Text();
        staminaBar = new StaminaBar();
        staminaTimeline = new Timeline();
        statusUpdate();
        VBox textBox = new VBox(jobNameText, staminaText, staminaBar, statusText, waitTurnText);
        getChildren().addAll(medicImageView, textBox);
    }
    public void statusUpdate()
    {
        String stamina = null;
        if(medic.isExhausted())
            stamina = "透支";
        else
            stamina = Integer.toString(medic.getStamina()) + "/" + Integer.toString(medic.getMaxStamina());
        staminaText.textProperty().setValue("體力：" + stamina);
        double nowStaminaPercent = medic.getStamina()/(double)(medic.getMaxStamina());
        staminaTimeline.getKeyFrames().setAll(
            new KeyFrame(Duration.millis(0.0), new KeyValue(staminaBar.progressProperty(), staminaBar.getProgress())),
            new KeyFrame(Duration.millis(100.0), new KeyValue(staminaBar.progressProperty(), nowStaminaPercent))
        );
        statusText.textProperty().setValue("狀態：" + medic.getStatusString());
        int waitTurn = medic.getWaitTurn();
        if(waitTurn == 0)
            waitTurnText.textProperty().setValue(null);
        else
            waitTurnText.textProperty().setValue("[尚需" + waitTurn + "回合]");
        staminaTimeline.playFromStart();
    }
}
class MedicPane extends TilePane
{
    private MedicalPersonnel[] medics;
    private MedicStatusBox[] medicStats;
    private double prefStatusWidth = 140.0;
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
        setPrefTileWidth(prefStatusWidth);
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
        ScrollPane scroll = new ScrollPane(medicPane);
        scroll.setPrefViewportWidth(medicPane.getTileWidth()*medicPane.getPrefColumns());
        scroll.setFitToWidth(true);
        setContent(scroll);
        setClosable(false);
    }
    public void tabUpdate()
    {
        medicPane.paneUpdate();
    }
}
public class HospitalGUI extends Application
{
    private final double therapySpace = 20.0, buttonSpace = 5.0, marginSpace = 10.0;
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
        Text dealInfo = new Text("這裡正上演的是醫院悠閒喜劇……才怪");
        dealInfo.setStyle("-fx-font-size: 14pt;");
        Text therapyTip = new Text("治療方法：");
        ObservableList<String> therapy = FXCollections.observableArrayList("medical", "wrap", "surgery", "chemotherapy", "emergency-surgery", "first-aid");
        ComboBox<String> therapyComboBox = new ComboBox<String>(therapy);
        Button therapyConfirmButton = new Button("確定執行");
        therapyConfirmButton.setOnMouseClicked(event -> {
            String inputTherapy = therapyComboBox.getValue();
            if(inputTherapy != null)
            {
                if(hospital.dealWithIllness(inputTherapy))
                    dealInfo.setText("成功的進行了[" + inputTherapy + "]，醫師又再度的暴肝了");
                else
                    dealInfo.setText("因為人手不足，無法進行[" + inputTherapy + "]，可能得請轉院治療了");
                hospital.turnOver();
                for(MedicTab medictab:medictabs)
                    medictab.tabUpdate();
            }
        });
        HBox therapyBox = new HBox(20.0, therapyTip, therapyComboBox, therapyConfirmButton);
        therapyBox.setAlignment(Pos.CENTER);
        Button saveButton = new Button("儲存狀態");
        Button loadButton = new Button("讀取狀態");
        setSLButton(saveButton, loadButton, stage);
        HBox functionalButtonBox = new HBox(buttonSpace, saveButton, loadButton);
        VBox hospitalPageBox = new VBox(hospitalPane, dealInfo, therapyBox, functionalButtonBox);
        VBox.setMargin(functionalButtonBox, new Insets(marginSpace));
        hospitalPageBox.setAlignment(Pos.TOP_CENTER);
        Scene scene = new Scene(hospitalPageBox);
        stage.setScene(scene);
        stage.setTitle("果然我的醫院悠閒喜劇搞錯了。");
        stage.getIcons().add(new Image("img/Hospital.png"));
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
                start(stage);
                slCompleteAlert.setTitle("讀取遊戲");
                slCompleteAlert.setContentText("讀取完成！");
                slCompleteAlert.showAndWait();
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
