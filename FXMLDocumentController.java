package apka;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    public Pane tdodaj;
    public Pane tstart;
    public Pane twyniki;
    public Label pasekWszyscy;
    public Label pasekZdane;
    public Label pasekNiezdane;
    public Label pasekProcenty;
    public TextField indeks;
    public TextField nazwisko;
    public TextField wyn1;
    public TextField wyn2;
    public TextField wyn3;
    public ChoiceBox wybor;
    public ListView lista;
    public NumberAxis xAxis = new NumberAxis();
    public CategoryAxis yAxis = new CategoryAxis();
    public BarChart wykres1;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    public Connection polacz() {
        String DBDRIVER = "com.mysql.jdbc.Driver";
        String DBURL = "jdbc:mysql://127.0.0.1:3306/baza";
        String DBUSER = "root";
        String DBPASS = "";

        Connection polaczenie = null;

        try {
            Class.forName(DBDRIVER).newInstance();
            polaczenie = DriverManager.getConnection(DBURL, DBUSER, DBPASS);

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return polaczenie;
    }

    public void rozlacz(Connection polaczenie) {
        try {
            polaczenie.close();
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void aktualizuj() {
        Connection polaczenie = polacz();
        Statement st;
        try {
            st = polaczenie.createStatement();
            String SQL = "SELECT COUNT(nrIndeksu) as Ile FROM dane;";
            ResultSet rs = st.executeQuery(SQL);
            rs.next();
            String ilosc = rs.getString("Ile");
            int wszyscy = rs.getInt("Ile");
            pasekWszyscy.setText(ilosc);
            SQL = "SELECT COUNT(nrIndeksu) as ILE FROM dane WHERE ocenaKoncowa>2;";
            rs = st.executeQuery(SQL);
            rs.next();
            ilosc = rs.getString("Ile");
            int zdane = rs.getInt("Ile");
            pasekZdane.setText(ilosc);
            SQL = "SELECT COUNT(nrIndeksu) as ILE FROM dane WHERE ocenaKoncowa=2;";
            rs = st.executeQuery(SQL);
            rs.next();
            ilosc = rs.getString("Ile");
            pasekNiezdane.setText(ilosc);
            if (wszyscy != 0) {
                double stosunek = ((double) zdane / (double) wszyscy) * 100;
                DecimalFormat dec = new DecimalFormat("#0");
                pasekProcenty.setText(dec.format(stosunek) + "%");
            } else {
                pasekProcenty.setText("0%");
            }

        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        rozlacz(polaczenie);
    }
    
    public void aktualizujWykresy() {

        try {
            tstart.getChildren().clear();
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            wykres1 = new BarChart(xAxis, yAxis);
            XYChart.Series series = new XYChart.Series();
            series.setName("Ilość osób z wynikiem +50%");
            Connection polaczenie = polacz();
            Statement st;
            st = polaczenie.createStatement();
            String SQL = "SELECT COUNT(wynik1) as Ile FROM dane WHERE wynik1>=50;";
            ResultSet rs = st.executeQuery(SQL);
            rs.next();
            series.getData().add(new XYChart.Data<>("Termin 1", rs.getInt("Ile")));
            SQL = "SELECT COUNT(wynik2) as Ile FROM dane WHERE wynik2>=50;";
            rs = st.executeQuery(SQL);
            rs.next();
            series.getData().add(new XYChart.Data<>("Termin 2", rs.getInt("Ile")));
            SQL = "SELECT COUNT(wynik3) as Ile FROM dane WHERE wynik3>=50;";
            rs = st.executeQuery(SQL);
            rs.next();
            series.getData().add(new XYChart.Data<>("Termin 3", rs.getInt("Ile")));
            wykres1.getData().add(series);
            rozlacz(polaczenie);
            
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            tstart.getChildren().add(wykres1);
        }

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
