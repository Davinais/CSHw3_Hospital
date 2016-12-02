import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Interpolator;
import javafx.beans.property.*;
import javafx.util.Duration;
import javafx.scene.paint.Color;
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
    private ObjectProperty<Color> barColor;
    private StringProperty colorString = new SimpleStringProperty();
    public StaminaBar(double staminaRate)
    {
        super(staminaRate);
        barColor = new SimpleObjectProperty<>(getBarColor(staminaRate));
        setColorStringFromColor(colorString, barColor);
        //參考自stackoverflow
        //設立一Listener使得顏色屬性產生變化時，字串屬性同樣會變化
        barColor.addListener((observable, oldColor, newColor) -> setColorStringFromColor(colorString, barColor));
        //將此體力條的樣式屬性綁定字串屬性，則其顏色即會跟著字串屬性變化時變化
        this.styleProperty().bind(new SimpleStringProperty("-fx-accent: ").concat(colorString));
    }
    private void setColorStringFromColor(StringProperty colorS, ObjectProperty<Color> color)
    {
        //將顏色屬性轉變成rgba屬性並放入字串屬性中
        colorS.set(
            "rgba(" +
            (int)(color.get().getRed()*255) + "," +
            (int)(color.get().getGreen()*255) + "," +
            (int)(color.get().getBlue()*255) + "," +
            color.get().getOpacity() +
            ");"
        );
    }
    public ObjectProperty<Color> barColorProperty()
    {
        return barColor;
    }
    public Color getBarColor(double staminaRate)
    {
        //分別設立每個區段時體力條的顏色
        if(staminaRate <= 0.25)
            return Color.RED;
        else if(staminaRate <= 0.5)
            return Color.ORANGE;
        else if(staminaRate <= 0.75)
            return Color.rgb(255, 234, 93); //#FFEA5D，其實用0xFF, 0xEA, 0x5D做輸入也是可以的
        else
            return Color.LIGHTGREEN;
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
        //獲得職業圖像
        medicImage = new Image("img/" + medic.getJobName() + ".png");
        ImageView medicImageView = new ImageView(medicImage);
        jobNameText = new Text(medic.getJobName() + index);
        staminaText = new Text();
        statusText = new Text();
        waitTurnText = new Text();
        staminaBar = new StaminaBar(1.0);
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
        //設立動畫事件，關鍵影格的關鍵值分別是其體力比值以及其顏色，其中顏色採線性變化
        staminaTimeline.getKeyFrames().setAll(
            new KeyFrame(Duration.millis(0.0), new KeyValue(staminaBar.progressProperty(), staminaBar.getProgress()),
                new KeyValue(staminaBar.barColorProperty(), staminaBar.getBarColor(staminaBar.getProgress()), Interpolator.LINEAR)),
            new KeyFrame(Duration.millis(250.0), new KeyValue(staminaBar.progressProperty(), nowStaminaPercent),
                new KeyValue(staminaBar.barColorProperty(), staminaBar.getBarColor(nowStaminaPercent), Interpolator.LINEAR))
        );
        statusText.textProperty().setValue("狀態：" + medic.getStatusString());
        int waitTurn = medic.getWaitTurn();
        //若等待回合為0，不顯示
        if(waitTurn == 0)
            waitTurnText.textProperty().setValue(null);
        else
            waitTurnText.textProperty().setValue("[尚需" + waitTurn + "回合]");
        //播放動畫
        staminaTimeline.playFromStart();
    }
}
class MedicPane extends TilePane
{
    private MedicStatusBox[] medicStats;
    private double prefStatusWidth = 140.0;
    public MedicPane(MedicalPersonnel[] medics)
    {
        super();
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
    private MedicPane medicPane;
    public MedicTab(Hospital hospital, String jobName)
    {
        super();
        switch(jobName)
        {
            case "Surgeon":
                setText("外科醫生");
                break;
            case "Physician":
                setText("內科醫生");
                break;
            case "Nurse":
                setText("護理師");
                break;
            case "Anesthetist":
                setText("麻醉師");
                break;
            default:
        }
        medicPane = new MedicPane(hospital.getMedicList(jobName));
        //建立捲動面板，會在視窗大小小於TilePane時出現捲動條
        ScrollPane scroll = new ScrollPane(medicPane);
        //使初始捲動面板的Viewport寬度與TilePane寬度吻合，如此便不會出現水平捲動軸
        scroll.setPrefViewportWidth(medicPane.getTileWidth()*medicPane.getPrefColumns());
        //使TilePane寬度會跟著捲動面板的Viewport寬度做變化，如此便不會出現水平捲動軸
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
