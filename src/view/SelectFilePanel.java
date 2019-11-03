package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SelectFilePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private JComboBox<String> fileDisponibili;
	private String[] files;
	private JButton ok;
	
	public SelectFilePanel()
	{
		files= new String[2];
		files[0]="ABCD123";
		files[1]="Patient";
		
		JLabel label = new JLabel("Selezionare il nome del paziente per accedere alla cartella clinica:");
	//	GridLayout commandPanelLayout = new GridLayout(3, 1);
		//commandPanelLayout.setHgap(5);
	//	commandPanelLayout.setVgap(5);
		//panel.setLayout(commandPanelLayout);
		
		//label.setAlignmentX(CENTER_ALIGNMENT);
		//this.add(label, BorderLayout.NORTH);
		
		JPanel commandPanel= new JPanel();
		GridLayout commandPanelLayout = new GridLayout(3, 1);
		commandPanelLayout.setHgap(20);
		commandPanelLayout.setVgap(15);
		commandPanel.setLayout(commandPanelLayout);
		
		fileDisponibili= new JComboBox<String>(files);
		commandPanel.add(label);
		commandPanel.add(fileDisponibili);
		
		ok= new JButton("ok");
		
		commandPanel.add(ok);
		
		this.add(commandPanel,BorderLayout.SOUTH);
	}

	public JComboBox<String> getFileDisponibili() {
		return fileDisponibili;
	}

	public String[] getFiles() {
		return files;
	}

	public JButton getOk() {
		return ok;
	}

}
