package fr.texteditor.com;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.apache.commons.lang3.StringUtils;

public class TextEditor extends JFrame{

	private static final long serialVersionUID = -7991122480002023729L;

	public static JTextArea textArea;
	public JScrollPane scTextArea;
	private static File file;
	static JFileChooser fileChooser;
	
	public TextEditor() {
		super("Editeur de texte");
		this.setSize(720,480);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setJMenuBar(createJMenuBar());
		
		JPanel contentPane = (JPanel) this.getContentPane();
		
		scTextArea = new JScrollPane(textArea=new JTextArea());
		textArea.setTabSize(2);
		contentPane.add(scTextArea);
		
		JToolBar toolBar = createJToolBar();
		contentPane.add(toolBar,BorderLayout.NORTH);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("Au revoir !");
			}
		});
	}
	
	private void closeWindow() {
		if(!confirmClearTextArea()) {
			return;
		}
		dispose();
	}
	
	private JToolBar createJToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.add(newEmptyFileAction);
		toolBar.add(newJavaFileAction);
		toolBar.add(openFileAction);
		toolBar.add(saveAction);
		toolBar.add(saveAsAction);
		toolBar.add(quitAction);
		return toolBar;
	}
	
	private boolean saveAsFunction() {
		JFileChooser chooser = new JFileChooser();
		if(chooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			return true;
		}
		JOptionPane.showMessageDialog(null, "Opération annulée");
		return false;
	}
	
	private boolean saveFunction() {
		if(file==null) {
			if(!saveAsFunction()) {
				return false;
			}
		}
		try {
			DataOutputStream osw = new DataOutputStream(new FileOutputStream(file));
			osw.writeUTF(textArea.getText());
			osw.flush();
			osw.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement");
			return false;
		}
		return true;
	}
	
	private boolean confirmClearTextArea() {
		int response = JOptionPane.showConfirmDialog(null, "Voulez-vous sauvegarder le fichier ouvert ?","Sauvegarder ?",JOptionPane.YES_NO_CANCEL_OPTION);
		if(response==JOptionPane.CANCEL_OPTION) {
			return false;
		} else if(response==JOptionPane.NO_OPTION) {
			return true;
		} else if(response==JOptionPane.YES_OPTION) {
			return saveFunction();
		}
		return false;
	}
	
	private JMenuBar createJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Fichier");
		menuBar.add(fileMenu);
		JMenu newMenu = new JMenu("New...");
		fileMenu.add(newMenu);
		newMenu.add(newEmptyFileAction);
		newMenu.add(newJavaFileAction);
		fileMenu.add(openFileAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAsAction);
		fileMenu.add(quitAction);
		return menuBar;
	}
	
	private AbstractAction newEmptyFileAction = new AbstractAction() {
		{
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.CTRL_DOWN_MASK));
			putValue(MNEMONIC_KEY, KeyEvent.VK_N);
			putValue(NAME, "New Empty File");
			putValue(SHORT_DESCRIPTION, "Create a new empty file");
			putValue(SMALL_ICON, new ImageIcon(getClass().getResource("icons/new.png")));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!textArea.getText().replace("\n", "").trim().equalsIgnoreCase("")) {
				if(!confirmClearTextArea()) {
					JOptionPane.showMessageDialog(null, "Opération annulée...");
					return;
				}
			}
			textArea.setText("");
			JOptionPane.showMessageDialog(null, "Nouveau document vide créé avec succès !");
		}
	};
	
	
	private AbstractAction newJavaFileAction = new AbstractAction() {
		{
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_J,KeyEvent.CTRL_DOWN_MASK));
			putValue(MNEMONIC_KEY, KeyEvent.VK_J);
			putValue(NAME, "New Java File");
			putValue(SHORT_DESCRIPTION, "Create a new basic Java file");
			putValue(SMALL_ICON, new ImageIcon(getClass().getResource("icons/java.png")));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!textArea.getText().replace("\n", "").trim().equalsIgnoreCase("")) {
				if(!confirmClearTextArea()) {
					JOptionPane.showMessageDialog(null, "Opération annulée...");
					return;
				}
			}
			textArea.setText("public class Main{\n"
					+ "\tpublic static void main(String[] args){\n"
					+ "\t\tSystem.out.println(\"Hello World\");\n"
					+ "\t}\n"
					+ "}");
			JOptionPane.showMessageDialog(null, "Nouveau document Java basique créé avec succès !");
		}
	};
	
	
	
	private AbstractAction openFileAction = new AbstractAction() {
		{
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O,KeyEvent.CTRL_DOWN_MASK));
			putValue(MNEMONIC_KEY, KeyEvent.VK_O);
			putValue(NAME, "Open File");
			putValue(SHORT_DESCRIPTION, "Open a file from your computer");
			putValue(SMALL_ICON, new ImageIcon(getClass().getResource("icons/open.png")));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			fileChooser = new JFileChooser();
			if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				if(!textArea.getText().replace("\n", "").trim().equalsIgnoreCase("")) {
					if(!confirmClearTextArea()) {
						JOptionPane.showMessageDialog(null, "Opération annulée...");
						return;
					}
				}
				try {
//					DataInputStream br = new DataInputStream(new FileInputStream(fileChooser.getSelectedFile()));

//					byte[] content;
//					content = br.readAllBytes();
//					String StringContent = new String(content,StandardCharsets.UTF_8);
//					String content = new String(br.readAllBytes(),StandardCharsets.ISO_8859_1);
//					BufferedReader br = new  BufferedReader(new InputStreamReader(new FileInputStream(fileChooser.getSelectedFile())));
//					String tempContent;
//					String content = "";
//					while((tempContent=br.readLine())!=null) {
//						content+=tempContent+"\n";
//					}
//					br.close();
					
					DataInputStream dis = new DataInputStream(new FileInputStream(fileChooser.getSelectedFile()));
					String content = "";
					while(dis.available()>0) {
						content+=dis.readUTF();
					}
					dis.close();
					textArea.setText(content);
					file = fileChooser.getSelectedFile();
					JOptionPane.showMessageDialog(null, file.getAbsolutePath()+" ouvert avec succès !");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	};
	
	
	
	private AbstractAction saveAction = new AbstractAction() {
		{
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_DOWN_MASK));
			putValue(MNEMONIC_KEY, KeyEvent.VK_S);
			putValue(NAME, "Save File");
			putValue(SHORT_DESCRIPTION, "Save file to your computer");
			putValue(SMALL_ICON, new ImageIcon(getClass().getResource("icons/save.png")));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			saveFunction();
		}
	};
	
	private AbstractAction saveAsAction = new AbstractAction() {
		{
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_DOWN_MASK+KeyEvent.SHIFT_DOWN_MASK));
			putValue(MNEMONIC_KEY, KeyEvent.VK_S+KeyEvent.SHIFT_DOWN_MASK);
			putValue(NAME, "Save File As");
			putValue(SHORT_DESCRIPTION, "Save file on your computer to specific location");
			putValue(SMALL_ICON, new ImageIcon(getClass().getResource("icons/save_as.png")));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			saveAsFunction();
			saveFunction();
		}
	};
	
	private AbstractAction quitAction = new AbstractAction() {
		{
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q,KeyEvent.CTRL_DOWN_MASK));
			putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
			putValue(NAME, "Exit");
			putValue(SHORT_DESCRIPTION, "Destroy the Frame");
			putValue(SMALL_ICON, new ImageIcon(getClass().getResource("icons/exit.png")));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			closeWindow();
		}
	};
	
	private AbstractAction reportBugAction = new AbstractAction() {
		{
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B,KeyEvent.CTRL_DOWN_MASK));
			putValue(MNEMONIC_KEY, KeyEvent.VK_B);
			putValue(NAME, "Report Bug");
			putValue(SHORT_DESCRIPTION, "Click here to report a bug");
			putValue(SMALL_ICON, new ImageIcon(getClass().getResource("icons/about.png")));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI("https://<URL Github>"));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
	};

	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(new NimbusLookAndFeel());
		
		TextEditor textEditor = new TextEditor();
		textEditor.setVisible(true);
	}

}
