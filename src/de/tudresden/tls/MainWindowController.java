/* 
#    Copyright 2017 Willi Schmidt
# 	 Icon made by Freepik from www.flaticon.com
# 
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.

#################################################################################################### 
*/
package de.tudresden.tls;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.FloatStringConverter;


public class MainWindowController implements Initializable { 
	private Kreuzung kr = new Kreuzung();
	private static Signalgeber[] s = new Signalgeber[16];		//Maximum 16 da 4x4 Signalgeber
	private static Phase[] p = new Phase[10];	//Maximum 10 angenommen
	private static SumoExport[] se = new SumoExport[50];	//Maximum von 50 angenommen
	int anz_phasen=0;
	private Verriegelungsmatrix vm = new Verriegelungsmatrix();
	private Zwischenzeiten zz;
	private static MenuItem[] menuitem = new MenuItem[3];
	private LinkedList<String> kats;
	StackPane spane;
	StackPane spane2;
	@FXML Tab tab_vm;
	@FXML Tab tab_zz;
	@FXML Tab tab_ph;
	@FXML Tab tab_pp;
	@FXML Tab tab_exp;
	@FXML ImageView image_icon;
	
	//Views
	@FXML TableView<Option> table_options1;
	ObservableList<Option> data1;
	@FXML Label label_info;
	//----
	@FXML private Pane gui_zufahrt1;	//Panes f�r Tabellen
	@FXML private Pane gui_zufahrt2;
	@FXML private Pane gui_zufahrt3;
	@FXML private Pane gui_zufahrt4;
	@FXML private VBox gui_vbox_z1;		//VBox f�r Signalgeber
	@FXML private VBox gui_vbox_z2;
	@FXML private VBox gui_vbox_z3;
	@FXML private VBox gui_vbox_z4;
	@FXML private ContextMenu gui_contextmenu;
	@FXML private ImageView imageview_crossing;
	//----
	@FXML private VBox vm_vbox;
	@FXML private VBox zz_vbox;
	//----
	@FXML private TreeView<String> tree_phasen;
	@FXML private VBox vbox_phase;
	@FXML private HBox hbox_phasen;
	@FXML private ComboBox<String> comboBox;
	@FXML private Pane anchor_left;
	@FXML private Pane anchor_right;
	HashMap<String, Signalgeber> signalgeberbezeichnung = new HashMap<String, Signalgeber>(); 
	//----
	Phasenplan pp = new Phasenplan();
	@FXML private VBox pp_vbox;
	@FXML private Slider slider_g;
	@FXML private Slider slider_tp;
	private double g=0.0d;
	private double tp=0;
	StackPane spane_pp;
	// -----
	private Festzeitsteuerung fezest = new Festzeitsteuerung();
	StackPane spane_fs;
	// -----
	private Export export = new Export();
		
	
	
	public Main main;
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public static void main(String[] args) {
	
	}
	

	// ---------------------- Signalgeber Initialisierung ----------------------------
	public void initialize(URL location, ResourceBundle resources) {
		
		gui_zufahrt1.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() 
		{public void handle(MouseEvent e){contextMenu(gui_zufahrt1,e.getScreenX(), e.getScreenY());}});
		
		gui_zufahrt2.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() 
		{public void handle(MouseEvent e){contextMenu(gui_zufahrt2,e.getScreenX(), e.getScreenY());}});
		
		gui_zufahrt3.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() 
		{public void handle(MouseEvent e){contextMenu(gui_zufahrt3,e.getScreenX(), e.getScreenY());}});
		
		gui_zufahrt4.addEventHandler(MouseEvent.MOUSE_CLICKED,new EventHandler<MouseEvent>() 
		{public void handle(MouseEvent e){contextMenu(gui_zufahrt4,e.getScreenX(), e.getScreenY());}});
		
		
		new Zufahrt(kr,gui_zufahrt1, gui_vbox_z1);
		new Zufahrt(kr,gui_zufahrt2, gui_vbox_z2);
		new Zufahrt(kr, gui_zufahrt3, gui_vbox_z3);
		new Zufahrt(kr,gui_zufahrt4, gui_vbox_z4);
		kats = new LinkedList<String>();
		kats.add("Standard");						//0
		kats.add("Rechts-Pfeil");					//1
		kats.add("Links-Pfeil");					//2
		tab_vm.setDisable(false);
		tab_zz.setDisable(false);
		slider_g.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,Number old_val, Number g_val) {setG(g_val.doubleValue());}});
		slider_tp.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,Number old_val, Number tp_val) {setTp(tp_val.doubleValue());}});
		label_info.setText("Signalgeber einf�gen jetzt m�glich!!");
		
		Image kreuzung = new Image(Main.class.getResourceAsStream("kreuzung.png"));
		imageview_crossing.setImage(kreuzung);
		Image ico_gross = new Image(Main.class.getResourceAsStream("ico_gross.png"));
		image_icon.setImage(ico_gross);
		
		data1 = FXCollections.observableArrayList(
		new Option(1, "Angleichsfaktor f1", 0.90f),
		new Option(2, "Angleichsfaktor f2", 0.85f),
		new Option(3, "Dauer Signalbild Gelb [s]", 3f),
		new Option(4, "Dauer Sigalbild Rot-Gelb [s]", 2f)
	/*	Vorerst nicht erforderlich, da keine Berechnung der Zwischenzeit
  		new Option("R�umgeschwindigkeit KFZ gerade [m/s]", 10f),
    	new Option("R�umgeschwindigkeit KFZ abb. [m/s]", 7f),
    	new Option("R�umgeschwindigkeit Radfahrer [m/s]", 4f),
    	new Option("Einfahrgeschwindigkeit KFZ gerade [m/s]", 11.1f),
    	new Option("Einfahrgeschwindigkeit KFZ abb. [m/s]", 11.1f),
    	new Option("Einfahrgeschwindigkeit Radfahrer [m/s]", 5f),
    	new Option("�berfahrzeit KFZ gerade [s]", 3f),
    	new Option("�berfahrzeit KFZ abb. [s]", 2f),
    	new Option("�berfahrzeit Rad [s]", 1f)
	*/           
		);
		for (int i=0;i<data1.size();i++)
		{
			kr.putOption(data1.get(i));
		}
	}
	
	public static Signalgeber[] getS() {
		return s;
	}

	public void contextMenu(Pane p, double x, double y) 
	{
		Zufahrt zf = kr.get_zufahrt(p);
		gui_contextmenu.getItems().clear();
		
		for (int i=0;i<kats.size();i++)
		{
			if (kr.checksignalgeber(zf, i)==1)
			{
				menuitem[i] = new MenuItem(kats.get(i));
				menuitem[i].setOnAction(new EventHandler<ActionEvent>() 
				{
					int j;
					public void handle(ActionEvent e) 
					{
						
						//System.out.println("Debug: "+kats.get(j)+" Spur wird erzeugt!");
						zf.erzeugeSignalgeber(j);				        						
					}

					private EventHandler<ActionEvent> init(int i)
					{
				        j = i;
				        return this;
					}
				}
				.init(i));
				gui_contextmenu.getItems().add(menuitem[i]);
			}
			else
			{
				//System.out.println("Debug: Spur nicht m�glich!");
			}
		}
		gui_contextmenu.show(p, x, y);
	
	}
	// ---------------------- Verriegelungsmatrix anzeigen ----------------------------
	@FXML
	public void tab_vm_clicked() {
	if (kr.get_signalgeberlist().size()>0) {
		this.spane = new StackPane(vm);
		this.vm_vbox.getChildren().clear();
		this.vm_vbox.getChildren().add(this.spane);
		AnchorPane.setTopAnchor(this.spane, 0.0);
		AnchorPane.setLeftAnchor(this.spane, 0.0);
		AnchorPane.setRightAnchor(this.spane, 0.0);
		label_info.setText("Bitte �berpr�fen Sie die Verriegelungsmatrix - f�hren ggf. �nderungen durch und speichern.");
		vm.create_matrix(kr);	
		Button button_vm = new Button("ver�nderte Verriegelungsmatrix speichern");
        button_vm.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                vm.SaveChanges(kr);
            }
        });
        this.vm_vbox.getChildren().add(button_vm);
	}
	else {
		System.out.println("Keine Signalgeber angelegt!");
	}
	}
	// ---------------------- Zwischenzeiten eingeben ----------------------------
	@FXML
	public void tab_zz_clicked() {
	if (kr.get_signalgeberlist().size()>0) {
		zz=new Zwischenzeiten();
		this.spane2 = new StackPane(zz);
		this.zz_vbox.getChildren().clear();
		this.zz_vbox.getChildren().add(this.spane2);
		AnchorPane.setTopAnchor(this.spane2, 0.0);
		AnchorPane.setLeftAnchor(this.spane2, 0.0);
		AnchorPane.setRightAnchor(this.spane2, 0.0);
		AnchorPane.setBottomAnchor(this.spane2, 0.0);
		label_info.setText("Geben Sie bitte alle Zwischenzeiten in die Felder \"Wert\" ein und speichern anschlie�end!");
		zz.pruef_zz(kr, vm);
		tab_ph.setDisable(false);
		Button button_zz = new Button("ver�nderte Zwischenzeitmatrix speichern");
        button_zz.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                zz.SaveChanges(kr);
                zz.pruef_zz(kr, vm);
            }
        });
        this.zz_vbox.getChildren().add(button_zz);
	}
	else {
		System.out.println("Keine Signalgeber angelegt!");
	}
	}
	// ---------------------- Phasenerstellung ----------------------------
	@FXML
	public void button_phase_add(){		
		label_info.setText("F�gen Sie der Phase bitte Signalgeber hinzu.");
		anchor_left.getChildren().clear();
		p[anz_phasen]=new Phase();
		anz_phasen++;
		ObservableList<String> options = FXCollections.observableArrayList();
		
		for (int i=0;i<kr.get_signalgeberlist().size();i++) {
			options.add(kr.get_signalgeberlist().get(i).getBezeichnung());
			signalgeberbezeichnung.put(kr.get_signalgeberlist().get(i).getBezeichnung(), kr.get_signalgeberlist().get(i));
		}
		
		comboBox = new ComboBox<String>(options);
		comboBox.setValue(options.get(0));
		anchor_left.getChildren().addAll(comboBox);
		update_tree_phase();
	}
	
	private void update_tree_phase() {
		anchor_right.getChildren().clear();
		TreeItem<String> rootItem = new TreeItem<String> ("Phasen");
        rootItem.setExpanded(true);
        for (int i = 0; i < anz_phasen; i++) {
            TreeItem<String> pitem = new TreeItem<String> ("Phase" + i);
            pitem.setExpanded(true);
            for (int j=0;j<p[i].sg.size();j++) {
            	TreeItem<String> sitem = new TreeItem<String> (p[i].sg.get(j).getBezeichnung());
            	sitem.setExpanded(true);
            	pitem.getChildren().add(sitem);
            }
            rootItem.getChildren().add(pitem);
        }        
        TreeView<String> tree = new TreeView<String> (rootItem);        
        StackPane root = new StackPane();
        root.getChildren().add(tree);
        anchor_right.getChildren().addAll(root);
	}
	
	
	
	@FXML
	public void button_spur_phase_add(){
		Signalgeber s = signalgeberbezeichnung.get(comboBox.getValue());
		if (p[anz_phasen-1].sg_in_phase_vorhanden(s)==0) {
			p[anz_phasen-1].putSignalgeber(s);
		}
		else {
			System.out.println("Signalgeber in Phase bereits vorhanden!");
		}
		update_tree_phase();
		tab_pp.setDisable(false);
	}
	
	// ---------------------- Phasenplan ----------------------------
	@FXML
	public void tab_pp_clicked() {
		label_info.setText("Kontrollieren Sie die Freigabezeiten!");
		this.pp_vbox.getChildren().clear();
        g=slider_g.getValue();
		tp=slider_tp.getValue();
		vb_calc_Signalgeber(g, tp);
		this.spane_pp = new StackPane(pp);
		this.pp_vbox.getChildren().add(this.spane_pp);
		AnchorPane.setTopAnchor(this.spane_pp, 0.0);
		AnchorPane.setLeftAnchor(this.spane_pp, 0.0);
		AnchorPane.setRightAnchor(this.spane_pp, 0.0);
		AnchorPane.setBottomAnchor(this.spane_pp, 0.0);
		pp.create_fz_table(kr);
		Button button_fzs = new Button("Festzeitsteuerung generieren");
        button_fzs.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                create_fsz();
            }
        });
        this.pp_vbox.getChildren().add(button_fzs);
		Button button_sumoexpo = new Button("Export f�r Sumo generieren");
        button_sumoexpo.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                create_sumoexport();
            }
        });
        this.pp_vbox.getChildren().add(button_sumoexpo);
	}
	public void create_fsz() {
		label_info.setText("Die Festzeitsteuerung wurde erstellt!");
        this.spane_fs = new StackPane(fezest);
		this.pp_vbox.getChildren().add(this.spane_fs);
		AnchorPane.setTopAnchor(this.spane_fs, 0.0);
		AnchorPane.setLeftAnchor(this.spane_fs, 0.0);
		AnchorPane.setRightAnchor(this.spane_fs, 0.0);
		AnchorPane.setBottomAnchor(this.spane_fs, 0.0);
		fezest.create_festzeitplan(kr, p, anz_phasen, vm, zz, tp);
		tab_exp.setDisable(false);
	}
	public void create_sumoexport() {
		fezest.create_export2sumo(signalgeberbezeichnung, se, tp);
	}
	public void setG(double g) {
		this.g=g;
		vb_calc_Signalgeber(g, tp);
		pp.create_fz_table(kr);
	}
	public void setTp(double tp) {
		this.tp=tp;
		vb_calc_Signalgeber(g, tp);
		pp.create_fz_table(kr);
	}
	
	public void vb_calc_Signalgeber(double g, double tp) {
	
		for (int i=0;i<kr.get_signalgeberlist().size();i++) {
			kr.get_signalgeberlist().get(i).calc_TfUmlauf(g, tp);
		}
		
	}
	// ---------------------- Export ----------------------------
	@FXML
	public void tab_exp_clicked() {
		label_info.setText("Export jetzt m�glich.");
	}
	@FXML
	public void button_export_clicked(){
		export.do_export(kr, p, anz_phasen, vm, zz);
	}
	// ---------------------- Grundeinstellungen ----------------------------
	@SuppressWarnings("unchecked")
	@FXML
	public void tab_ge_clicked() {
		label_info.setText("Grundeinstellungen");
			table_options1.getColumns().clear();

		table_options1.setEditable(true);
		TableColumn<Option, String> NameCol = new TableColumn<Option, String>("Name");
		NameCol.setCellValueFactory(new PropertyValueFactory<Option,String>("name"));
		NameCol.setCellFactory(TextFieldTableCell.<Option>forTableColumn());
		NameCol.setOnEditCommit(
				new EventHandler<CellEditEvent<Option, String>>() {
	        public void handle(CellEditEvent<Option, String> t) {
	            ((Option) t.getTableView().getItems().get(
	                t.getTablePosition().getRow())
	                ).setName(t.getNewValue());
	        	}
				}
		);
		TableColumn<Option, Float> WertCol = new TableColumn<Option, Float>("Wert");
		WertCol.setCellValueFactory(new PropertyValueFactory<Option,Float>("wert"));
		WertCol.setCellFactory(TextFieldTableCell.<Option, Float>forTableColumn(new FloatStringConverter()));
		WertCol.setOnEditCommit(
	    new EventHandler<CellEditEvent<Option, Float>>() {
	        public void handle(CellEditEvent<Option, Float> t) {
	            ((Option) t.getTableView().getItems().get(
	                t.getTablePosition().getRow())
	                ).setWert(t.getNewValue());
	        }
	    }
		);
		table_options1.getColumns().addAll(NameCol, WertCol);
		table_options1.setItems(data1);
		}
	
	@FXML
	public void button_ge_save_clicked() {
		for (int i=0;i<data1.size();i++)
		{
			kr.putOption(data1.get(i));
		}
	}
	
	@FXML
	public void do_menu_beenden()
	{
		System.exit(0);		
	}
}
