package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

import model.ActualContext;
import controller.Controller;
import controller.MyController;

public class MyFrame extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private Controller controller;
	private SelectFilePanel panel;
	private JLabel label;
	
	public MyFrame(String name)//,Controller c)
	{
		super(name);
		//this.controller=c;
		controller= new MyController();
		setSize(500, 350);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initGUI();
	}
	
	
	private void initGUI() {
		label= new JLabel("Requisiti Soddisfatti");
		label.setForeground(Color.RED);
		//getContentPane().add(label);
		//label.setVisible(false);
		panel=new SelectFilePanel();
		panel.getFileDisponibili().addActionListener(this);
		panel.getOk().addActionListener(this);
		getContentPane().add(panel);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()== panel.getOk())
		{
			
			System.out.println((String)panel.getFileDisponibili().getSelectedItem());
			controller.setFileName((String)panel.getFileDisponibili().getSelectedItem());
			panel.remove(label);
			try{
				controller.checkPermission(ActualContext.getInstance().getContextPermission());
				label.setText("requisiti soddisfatti");
				
			}catch(Exception ex)
			{
				System.out.println("problema seguente ");
				ex.printStackTrace();
				label.setText("requisiti non soddisfatti");
			}
			//getContentPane().add(label,BorderLayout.CENTER);
			panel.add(label,BorderLayout.EAST);
			getContentPane().validate();
		}
	}


}
