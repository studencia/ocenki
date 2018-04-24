package apka;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
