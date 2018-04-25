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
    
    @FXML
    private void kstart(ActionEvent event) {
        tdodaj.setVisible(false);
        twyniki.setVisible(false);
        aktualizujWykresy();
    }
    
    @FXML
    private void kdodaj(ActionEvent event) {
        tdodaj.setVisible(true);
        twyniki.setVisible(false);
        aktualizuj();
    }

    @FXML
    private void kwyniki(ActionEvent event) {
        twyniki.setVisible(true);
        tdodaj.setVisible(false);
    }

    @FXML
    private void kwybierz(ActionEvent event) {
        try {
            Connection polacznie = polacz();
            Statement st = polacznie.createStatement();
            int wybranaOpcja = wybor.getSelectionModel().getSelectedIndex();
            String SQL = "SELECT nrIndeksu,nazwisko,wynik1,wynik2,wynik3,ocenaKoncowa FROM dane;";
            ResultSet rs = st.executeQuery(SQL);
            ObservableList<String> listaInfo = FXCollections.observableArrayList();
            rs.next();
            System.out.println("Wybrana opcja: " + String.valueOf(wybranaOpcja));
            if (wybranaOpcja == 0) {
                while (!rs.isAfterLast()) {
                    String tekst = rs.getString("nrIndeksu") + "  |  " + rs.getString("nazwisko") + " : " + rs.getString("wynik1") + "%";
                    listaInfo.add(tekst);
                    rs.next();
                }
                lista.setItems(listaInfo);
            }
            if (wybranaOpcja == 1) {
                while (!rs.isAfterLast()) {
                    String tekst = rs.getString("nrIndeksu") + "  |  " + rs.getString("nazwisko") + " : " + rs.getString("wynik1") + "% , " + rs.getString("wynik2") + "%";
                    listaInfo.add(tekst);
                    rs.next();
                }
                lista.setItems(listaInfo);
            }
            if (wybranaOpcja == 2) {
                while (!rs.isAfterLast()) {
                    String tekst = rs.getString("nrIndeksu") + "  |  " + rs.getString("nazwisko") + " : " + rs.getString("wynik1") + "% , " + rs.getString("wynik2") + "% , " + rs.getString("wynik3") + "% |  Ocena końcowa: " + rs.getString("ocenaKoncowa");
                    listaInfo.add(tekst);
                    rs.next();
                }
                lista.setItems(listaInfo);
            }
            rozlacz(polacznie);
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void kdodajdobazy(ActionEvent event) {
        String nrIndeksu = indeks.getText();
        String nazwiskoStudenta = nazwisko.getText();
        String wynik1 = wyn1.getText();
        String wynik2 = wyn2.getText();
        String wynik3 = wyn3.getText();
        try {
            int testNrIndeksu = Integer.parseInt(nrIndeksu);
            int testWynik1 = Integer.parseInt(wynik1);
            int testWynik2 = Integer.parseInt(wynik2);
            int testWynik3 = Integer.parseInt(wynik3);
            if ((testWynik1 >= 0) && (testWynik1 <= 100) && (testWynik2 >= 0) && (testWynik2 <= 100) && (testWynik3 >= 0) && (testWynik3 <= 100)) {
                double srednia = ((double) testWynik1 + (double) testWynik2 + (double) testWynik3) / 3;
                int ocena = wystawOcene(srednia);
                String SQL = "INSERT INTO dane VALUES(" + indeks.getText() + ",\"" + nazwiskoStudenta + "\"," + wynik1 + "," + wynik2 + "," + wynik3 + "," + String.valueOf(ocena) + ");";
                Connection polaczenie = polacz();
                Statement st;
                st = polaczenie.createStatement();
                st.execute(SQL);
                rozlacz(polaczenie);
                aktualizuj();
                JOptionPane.showMessageDialog(null, "Dodano:\n" + nrIndeksu + " - " + nazwiskoStudenta + "\nWyniki kolokwiów: " + wynik1 + "% , " + wynik2 + "% , " + wynik3 + "%\nOcena końcowa: " + String.valueOf(ocena));
            } else {
                System.out.println("Źle wprowadzone wyniki");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Niepoprawny numer indeksu lub wyniku");
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int wystawOcene(double srednia) {
        if (srednia < 50) {
            return 2;
        } else if ((srednia >= 50) && (srednia < 70)) {
            return 3;
        } else if ((srednia >= 70) && (srednia < 90)) {
            return 4;
        } else {
            return 5;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        aktualizuj();
        wybor.setItems(FXCollections.observableArrayList(
                "1 termin", "2 termin ", "wszystkie terminy")
        );
        aktualizujWykresy();
    }    
    
}
