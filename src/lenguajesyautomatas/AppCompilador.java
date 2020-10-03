package lenguajesyautomatas;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AppCompilador extends JFrame implements ActionListener {
    // Componentes o Atributos

    private JMenuBar barraMenu;
    private JMenu menuArchivo;
    // Menu Archivo
    private JMenuItem itemNuevo, itemAbrir, itemGuardar, itemSalir, itemAnalisLexico;
    private JFileChooser ventanaArchivos;
    private File archivo;
    private JTextArea areaTexto;
    public NumeroLinea numLinea;
    private JScrollPane barrita;
    private JList<String> tokens;
    private JTabbedPane documentos, consola, tabla, tabla2, codobj;
    private String[] titulos = {"Tipo", "Nombre", "Valor", "Alcance", "Renglon"};
    DefaultTableModel modelo = new DefaultTableModel(new Object[0][0], titulos);
    public JTable mitabla = new JTable(modelo);
    private JButton btnAnalizar;
    public static ColorCeldas color = new ColorCeldas(4);

    public static void main(String[] args) {
        new AppCompilador();
    }

    public AppCompilador() {
        super("Compilador");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new GridLayout(2, 2));
        setSize(1000, 550);
        setLocationRelativeTo(null);
        creaInterFaz();
        setVisible(true);
    }

    private void creaInterFaz() {
        barraMenu = new JMenuBar();
        setJMenuBar(barraMenu);
        menuArchivo = new JMenu("Archivo");
        menuArchivo.setIcon(new ImageIcon("archivo.png"));
        ventanaArchivos = new JFileChooser();
        itemNuevo = new JMenuItem("Nuevo");
        itemAbrir = new JMenuItem("Abrir...");
        itemGuardar = new JMenuItem("Guardar...");
        itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(this);
        itemGuardar.addActionListener(this);
        itemAbrir.addActionListener(this);
        itemNuevo.addActionListener(this);
        itemAnalisLexico = new JMenuItem("Analizar codigo");
        itemAnalisLexico.addActionListener(this);
        btnAnalizar = new JButton("ANALIZAR");
        btnAnalizar.setFont(new Font("Dialog", Font.PLAIN, 40));
        btnAnalizar.addActionListener(this);

        ventanaArchivos = new JFileChooser();
        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        barraMenu.add(menuArchivo);
        areaTexto = new JTextArea();

        ventanaArchivos = new JFileChooser("Guardar");
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 12));

        NumeroLinea lineNumber = new NumeroLinea(areaTexto);

        barrita = new JScrollPane(areaTexto);
        barrita.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        barrita.setPreferredSize(new Dimension(870, 65));
        barrita.setRowHeaderView(lineNumber);

        documentos = new JTabbedPane();
        consola = new JTabbedPane();
        tabla = new JTabbedPane();

        documentos.addTab("Nuevo", barrita);
        documentos.setToolTipText("Aqui se muestra el codigo");
        add(documentos);
        tokens = new JList<String>();
        consola.addTab("Consola", new JScrollPane(tokens));
        tabla.addTab("Tabla de simbolos", new JScrollPane(mitabla));
        add(consola);
        consola.setToolTipText("Aqui se muestra el resultado del analisis");
        add(tabla);
        add(btnAnalizar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAnalizar) {
            if (guardar()) {
                System.out.println(archivo.getAbsolutePath());
                Analisis analisador = new Analisis(archivo.getAbsolutePath());
                tokens.setListData(analisador.getmistokens().toArray(new String[0]));
                modelo = new DefaultTableModel(new Object[0][0], titulos);

                mitabla.setDefaultRenderer(Object.class, color);

                for (int i = 0; i < analisador.getTabla().size(); i++) {
                    TabladeSimbolos id = analisador.getTabla().get(i);
                    mitabla.setModel(modelo);
                    if (!id.tipo.equals("")) {
                        Object datostabla[] = {id.tipo, id.nombre, id.valor, id.alcance, id.renglon};

                        modelo.addRow(datostabla);
                    }
                }
            }
            return;
        }
        if (e.getSource() == itemSalir) {
            System.exit(0);
            return;
        }
        if (e.getSource() == itemNuevo) {
            documentos.setTitleAt(0, "Nuevo");
            areaTexto.setText("");
            archivo = null;
            tokens.setListData(new String[0]);
            return;
        }
        if (e.getSource() == itemAbrir) {
            ventanaArchivos.setDialogTitle("Abrir..");
            ventanaArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (ventanaArchivos.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
                return;
            }
            archivo = ventanaArchivos.getSelectedFile();
            documentos.setTitleAt(0, archivo.getName());
            abrir();
        }
        if (e.getSource() == itemGuardar) {
            guardar();
        }
    }

    public boolean guardar() {
        try {
            if (archivo == null) {
                ventanaArchivos.setDialogTitle("Guardando..");
                ventanaArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (ventanaArchivos.showSaveDialog(this) == JFileChooser.CANCEL_OPTION) {
                    return false;
                }
                archivo = ventanaArchivos.getSelectedFile();
                documentos.setTitleAt(0, archivo.getName());
            }
            FileWriter fw = new FileWriter(archivo);
            BufferedWriter bf = new BufferedWriter(fw);
            bf.write(areaTexto.getText());
            bf.close();
            fw.close();
        } catch (Exception e) {
            System.out.println("Houston tenemos un problema?");
            return false;
        }
        return true;
    }

    public boolean abrir() {
        String texto = "", linea;
        try {
            FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);
            while ((linea = br.readLine()) != null) {
                texto += linea + "\n";
            }
            areaTexto.setText(texto);
            return true;
        } catch (Exception e) {
            archivo = null;
            JOptionPane.showMessageDialog(null, "Tipo de archivo incompatible", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    public static void enviarErrorSintactico(int error) {
        color.erroresSintacticos.add(error);
    }

    public static void eliminarErrorSintactico() {
        color.erroresSintacticos.clear();
    }
}
