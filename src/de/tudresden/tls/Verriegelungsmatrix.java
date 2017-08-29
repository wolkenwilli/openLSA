package de.tudresden.tls;

import java.util.HashMap;
import java.util.Stack;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;

class Verriegelungsmatrix extends SpreadsheetView {
	private int[][] array_ungerade = new int[7][7];
	private int[][] array_gerade = new int[7][7];	
	private Zwischenzeitbeziehungen[] zzb;
	private Zwischenzeitbeziehungen[][] vr_array;
	private int anz_zzb;

	public Verriegelungsmatrix()
	{
		//Initialisierung der Verriegelungsmatrix
		for (int i=0;i<7;i++) {
			for (int j=0;j<7;j++) {
				array_gerade[i][j]=3;
			}
		}
		for (int i=0;i<7;i++) {
			for (int j=0;j<7;j++) {
				array_ungerade[i][j]=3;
			}
		}
		
		//--- Array gerade ---
		//nicht verriegelt
		array_ungerade[0][0]=0;
		array_ungerade[1][1]=0;
		array_ungerade[2][2]=0;
		array_ungerade[3][3]=0;
		array_ungerade[0][1]=0;
		array_ungerade[0][2]=0;
		array_ungerade[1][2]=0;
		//bedingt verträglich
		array_ungerade[0][5]=1;
		array_ungerade[1][5]=1;
		array_ungerade[2][5]=1;
		array_ungerade[3][5]=1;
		array_ungerade[4][5]=1;
		array_ungerade[5][5]=1;
		array_ungerade[6][5]=1;
		array_ungerade[0][6]=1;
		array_ungerade[1][6]=1;
		array_ungerade[2][6]=1;
		array_ungerade[5][6]=1;
		array_ungerade[6][6]=1;
		
		//--- Array Ungerade ---
		
		array_gerade[2][2]=0;
		array_gerade[2][3]=0;
		array_gerade[2][4]=0;
		array_gerade[2][5]=0;
		array_gerade[2][6]=0;
	}
	int pruef_verriegelung(int id_spur1, int id_spur2, int kat_spur1, int kat_spur2) {
		int verriegelung=2;
		if (id_spur1==id_spur2)
		{
			verriegelung=9;
		}
		else if ((id_spur1+id_spur2)%2>0)
		{
			if (array_gerade[kat_spur1][kat_spur2]==3)
			{
				if (array_gerade[kat_spur2][kat_spur1]==3)
				{
					verriegelung=2;
				}
				else
				{
					verriegelung=array_gerade[kat_spur2][kat_spur1];
				}
			}
			else
			{
				verriegelung=array_gerade[kat_spur1][kat_spur2];
			}
		}
		else if ((id_spur1+id_spur2)%2==0)
		{
			if (array_ungerade[kat_spur1][kat_spur2]==3)
			{
				if (array_ungerade[kat_spur2][kat_spur1]==3)
				{
					verriegelung=2;
				}
				else
				{
					verriegelung=array_ungerade[kat_spur2][kat_spur1];
				}
			}
			else
			{
				verriegelung=array_ungerade[kat_spur1][kat_spur2];
			}
		}
		
		return verriegelung;
	}
	public int[][] getArray_gerade() {
		return array_gerade;
	}
	public int[][] getArray_ungerade() {
		return array_ungerade;
	}
	
	public void create_matrix(Kreuzung kr){
		
		HashMap <Zufahrt, Signalgeber> hm = kr.getAlleSignalgeber();
		int s=hm.size();
		int rowCount = s;
        int columnCount = s;
        vr_array = new Zwischenzeitbeziehungen[s][s];
        int i=0; int j=0; int x=0;
        zzb = new Zwischenzeitbeziehungen[s*s];
        anz_zzb=s*s;
        
        GridBase grid = new GridBase(rowCount, columnCount);
        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        ObservableList<String> rowsHeaders = FXCollections.observableArrayList();
        ObservableList<String> columnsHeaders = FXCollections.observableArrayList();
        
        for (Zufahrt z1 : hm.keySet()) {
        	final ObservableList<SpreadsheetCell> data = FXCollections.observableArrayList();
        	i=0;
			rowsHeaders.add(hm.get(z1).getBezeichnung());
			columnsHeaders.add(hm.get(z1).getBezeichnung());
        	for (Zufahrt z2 : hm.keySet()) {
        		int pruef=0;
        		pruef=pruef_verriegelung(z1.getNummer(), z2.getNummer(), hm.get(z1).getTyp(), hm.get(z2).getTyp());
				SpreadsheetCell cell = SpreadsheetCellType.INTEGER.createCell(i, j, 0, 0, pruef); 		 
				zzb[x] = new Zwischenzeitbeziehungen();
				zzb[x].setVerriegelung(pruef);
				zzb[x].setEinfahrend(hm.get(z1));
				zzb[x].setAusfahrend(hm.get(z2));
				vr_array[i][j]=zzb[x];
				cell.setEditable(true);
				data.add(cell);
				i++;
				x++;
        	}
        	rows.add(data);
        	j++;
        	System.out.println("Erzeugte Cols: "+data.size()+" geplante Cols: "+columnCount);
        	System.out.println("Erzeugte Rows: "+rows.size()+" geplante RowCount: "+rowCount);
        }
        
        
        grid.setRows(rows);
        setGrid(grid);
        grid.getRowHeaders().addAll(rowsHeaders);
	    grid.getColumnHeaders().addAll(columnsHeaders);

        getFixedRows().add(0);
        
        //getColumns().get(0).setFixed(true);
        //getColumns().get(1).setPrefWidth(250);
	}
	public Zwischenzeitbeziehungen[] getZzb() {
		return zzb;
	}
	public Zwischenzeitbeziehungen[][] getVr_array() {
		return vr_array;
	}
	public int getAnz_zzb() {
		return anz_zzb;
	}
}

