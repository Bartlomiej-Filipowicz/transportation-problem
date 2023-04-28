package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import controller.CPMAlgorithm;
import controller.CPMGraphGenerator;
import model.ImagePanel;
import model.Task;



public class MainUI implements ActionListener, MouseWheelListener/*, KeyListener */{

    ArrayList<Task> tasks = new ArrayList<>();
    CPMAlgorithm cpm = new CPMAlgorithm();

    int notch = 0;
    String criticalPath = "";

    final static int fontSize = 30;
    final static String fontFamily = "Arial";

    private static final String COMMAND_ADD_ROW = "add";
    private static final String COMMAND_GENERATE = "generate";
    private static final String COMMAND_OPEN_PAINT = "paint";

    private static int counter = 0;

    private JLabel tmpImg;

    private JPanel mainPanel;
    private ImagePanel imgPanel;
    private JPanel sidePanel;

    private JPanel tablePanel;
    private JTable inputTable;
    private JPanel buttonPanel;
    private JLabel nameLabel;
    private JLabel timeLabel;
    private JTextField delivererTextField;
    private JTextField receiverTextField;
    private JTextField cell1TextField;
    private JButton generateButton;
    private JButton addRowButton;

    private JPanel criticalPathPanel;
    private JCheckBox showCriticalPathCheckBox;
    private JLabel timesInfoLabel;
    private JButton openInPaintButton;

    private JFrame frame;

    private Integer rows;
    private Integer cols;

    public MainUI() throws IOException {
        frame = new JFrame("Transportation Problem");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        initImagePanel();
        frame.add(imgPanel, BorderLayout.WEST);
        initSidePanel();
        initTablePanel();
        initCriticalPathPanel();
        frame.add(sidePanel, BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);


    }

    private void initImagePanel() throws IOException {
        //final JLabel tmpImg = new JLabel(new ImageIcon(new FileUtil().openFromFile("img\\test_img.png")));
        //tmpImg.setVisible(false);
        imgPanel = new ImagePanel("img\\start_img.png");
        imgPanel.setPreferredSize(new Dimension(750, 700));
        imgPanel.addMouseWheelListener(this);
    }

    private void initSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(2,1));
        sidePanel.setPreferredSize(new Dimension(500, 700));
    }

    private void initTablePanel() {
        tablePanel = new JPanel();
        tablePanel.setLayout(new GridLayout(2,1)); //?
        tablePanel.setPreferredSize(new Dimension(500, 500));

        inputTable = new JTable();
        inputTable.setFont(new Font(fontFamily, Font.PLAIN, fontSize-5));
        (inputTable.getTableHeader()).setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        inputTable.setRowHeight(40);
//        inputTable.setModel(new javax.swing.table.DefaultTableModel(5,4)); // num of rows and cols
        /*inputTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"Nazwa", "Czas trwania", "Poprzednik"}));*/
        inputTable.setRowSelectionAllowed(true);
        inputTable.setCellSelectionEnabled(true);
        inputTable.setColumnSelectionAllowed(true);
        JScrollPane sp = new JScrollPane(inputTable);
        tablePanel.add(sp, BorderLayout.NORTH);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 2));
        nameLabel = new JLabel("Dostawcy:");
        timeLabel = new JLabel("Odbiorcy:");
        nameLabel.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        timeLabel.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        delivererTextField = new JTextField();
        delivererTextField.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        receiverTextField = new JTextField();
        receiverTextField.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        cell1TextField = new JTextField();
        buttonPanel.add(nameLabel);
        buttonPanel.add(delivererTextField);
        buttonPanel.add(timeLabel);
        buttonPanel.add(receiverTextField);
        generateButton = new JButton("Start alg");
        generateButton.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        generateButton.setActionCommand(COMMAND_GENERATE);
        generateButton.addActionListener(this);
        buttonPanel.add(generateButton);
        addRowButton = new JButton("Generuj tab");
        addRowButton.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        addRowButton.setActionCommand(COMMAND_ADD_ROW);
        addRowButton.addActionListener(this);
        buttonPanel.add(addRowButton);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        sidePanel.add(tablePanel, BorderLayout.NORTH);
    }

    private void initCriticalPathPanel() {
        criticalPathPanel = new JPanel(new GridLayout(3, 1));
        criticalPathPanel.setPreferredSize(new Dimension(500, 200));

        showCriticalPathCheckBox = new JCheckBox("Pokaz sciezke krytyczna na grafie");
        showCriticalPathCheckBox.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        showCriticalPathCheckBox.setEnabled(false);
        showCriticalPathCheckBox.setVisible(false);
        criticalPathPanel.add(showCriticalPathCheckBox);

        timesInfoLabel = new JLabel("<html>Czas:<br>Sciezka krytyczna::</html>");
        timesInfoLabel.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        criticalPathPanel.add(timesInfoLabel, BorderLayout.NORTH);
        timesInfoLabel.setVisible(false);

        openInPaintButton = new JButton("Otworz grafike w Paincie");
        openInPaintButton.setFont(new Font(fontFamily, Font.PLAIN, fontSize));
        openInPaintButton.setMaximumSize(new Dimension(500, 80));
        openInPaintButton.setActionCommand(COMMAND_OPEN_PAINT);
        openInPaintButton.addActionListener(this);
        criticalPathPanel.add(openInPaintButton, BorderLayout.SOUTH);
        openInPaintButton.setVisible(false);

        sidePanel.add(criticalPathPanel, BorderLayout.SOUTH);
    }


    public void actionPerformed(final ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case COMMAND_ADD_ROW:
                System.out.println("Dodaj wiersz");
                // https://www.youtube.com/watch?v=F0Zq2fAUpXg
                //DefaultTableModel model = (DefaultTableModel) inputTable.getModel();
                //model.addRow(new Object[]{nameTextField.getText(), timeTextField.getText(), prevTextField.getText()});

                // get value from a table cell
//                System.out.println(inputTable.getModel().getValueAt(1,0));
//                System.out.println("tranquillo");




                // generate a table based on input
                rows = Integer.parseInt(delivererTextField.getText())+ 1;
                cols = Integer.parseInt(receiverTextField.getText()) + 2;

                String[] columns = new String[cols];
                columns[0] = "-";
                for(int i = 2; i <= cols - 1; i++) columns[i-1] = "O"+(i-1);
                columns[cols-1] = "podaz";


                DefaultTableModel model = new javax.swing.table.DefaultTableModel(rows, cols);
                model.setColumnIdentifiers(columns);
                inputTable.setModel(model); // num of rows and cols

                for(int i = 0; i < rows - 1; i++) model.setValueAt("D"+(i+1), i,0);
                model.setValueAt("popyt", rows - 1, 0);

                model.setValueAt("-",rows- 1, cols - 1);

                // add new row
                // 1. must be comma separated
                // 2. suppose letters from ASCII, until the range
//                tasks.add(new Task(delivererTextField.getText(), Integer.parseInt(receiverTextField.getText())));
//                int intValueThis = delivererTextField.getText().charAt(0) - 65;
//                if (!(Objects.equals(prevTextField.getText(), "-"))) {
//                    String[] connections = prevTextField.getText().split(",");
//                    for (var value : connections) {
//                        int intValue = value.charAt(0) - 65;
//                        System.out.println(">>" + intValue + "<<");
//                        tasks.get(intValueThis).addDependency(intValue);
//                        tasks.get(intValue).addDependent(intValueThis);
//                    }
//                }
//                System.out.println("Added: " + tasks.get(tasks.size()-1).getName() + " " + tasks.get(tasks.size()-1).getTime());
                //System.out.println("tasks.get(" + Integer.parseInt(nameTextField.getText()) - 'A');// + ".addDependency(" + Integer.parseInt(value) - 'A'));

                delivererTextField.setText("");
                receiverTextField.setText("");
                break;
            case COMMAND_OPEN_PAINT:
                try {
                    Runtime.getRuntime().exec("C:\\WINDOWS\\system32\\mspaint.exe "+"img\\cpm-graph.png");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            case COMMAND_GENERATE:
                System.out.println("Generuj");
//                timesInfoLabel.setVisible(true);
//                openInPaintButton.setVisible(true);
//                showCriticalPathCheckBox.setEnabled(true);

                // get values from table cells
                //!!!! ALL VALUES IN A TABLE MUST APPEAR AS BIG
                ArrayList<ArrayList<Integer>> costs = new ArrayList<ArrayList<Integer>>();
                for(int i = 0; i < rows - 1; i++) costs.add(new ArrayList<Integer>());

                for(int i = 0; i < rows - 1; i++){
                    for(int j = 1; j < cols - 1; j++){
                        costs.get(i).add(Integer.parseInt((String) inputTable.getModel().getValueAt(i,j)));
                    }
                }

                // get popyt
                var popyt = new ArrayList<Integer>();
                for(int j = 1; j < cols - 1; j++) popyt.add(Integer.parseInt((String) inputTable.getModel().getValueAt(rows - 1,j)));

                // get podaz
                var podaz = new ArrayList<Integer>();
                for(int i = 0; i < rows - 1; i++) podaz.add(Integer.parseInt((String) inputTable.getModel().getValueAt(i,cols - 1)));

                // suma popyt
                int sum_popyt = 0;
                for(var it : popyt) sum_popyt += it;

                // suma podaz
                int sum_podaz = 0;
                for(var it : podaz) sum_podaz += it;

                // print 2D array of costs
                System.out.println(costs);
                System.out.println(popyt);
                System.out.println(podaz);
                System.out.println("podaz sum: " + sum_podaz);
                System.out.println("popyt sum: " + sum_popyt);
/*
                ArrayList<Task> tasks = new ArrayList<>();
                tasks.add(new Task("A", 4));
                tasks.add(new Task("B", 2));
                tasks.add(new Task("C", 3));
                tasks.add(new Task("D", 1));
                //tasks.get(1).addDependency(0); // B
                tasks.get(2).addDependency(1); // C
                tasks.get(3).addDependency(2); // D
                tasks.get(3).addDependency(0); // D
                tasks.get(1).addDependent(2); // B
                tasks.get(0).addDependent(3); // A
                tasks.get(2).addDependent(3); // A
 */
/*
                cpm = cpm.update(tasks.size(), tasks);
                criticalPath = cpm.runCPM();
                cpm.printResults();
                timesInfoLabel.setText("<html>Czas: " + cpm.getTime() + "<br>" +
                        "Sciezka krytyczna: " + cpm.getCriticalPath() + "</html>");

                var temp = new CPMGraphGenerator();
                temp.generate(tasks, criticalPath, notch * 0.05f);
*/
                //System.out.println(imgScrollPane.getGraphics());
//                if (counter == 0) {
//                    imgScrollPane.setVisible(false);
//                    counter++;
//                }
                //frame.remove(imgScrollPane);
                //imgScrollPane.setVisible(false);
                //tmpImg = new JLabel(new ImageIcon(new FileUtil().openFromFile("img\\cpm-graph.png")));
                //tmpImg.setVisible(true);
                //imgPanel = new JScrollPane(tmpImg);
                //imgPanel.setPreferredSize(new Dimension(750, 700));
                //frame.add(imgPanel, BorderLayout.WEST);
                //imgScrollPane.setVisible(true);
                //paintI
                //imgPanel.setImage("img\\cpm-graph.png");

                break;
            default:
                System.exit(-1);
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        String message;
        int notches = e.getWheelRotation();
        if (notches < 0) {
            message = "Mouse wheel moved UP "
                    + -notches + " notch(es)";
            notch--;
        } else {
            message = "Mouse wheel moved DOWN "
                    + notches + " notch(es)";
            notch++;
        }
        System.out.println(message);

        var temp = new CPMGraphGenerator();
        temp.generate(tasks, criticalPath, notch * 0.05f);
        imgPanel.setImage("img\\cpm-graph.png");

        //System.out.println(imgScrollPane.getGraphics());
//        if (counter == 1) {
//            imgScrollPane.setVisible(false);
//            counter++;
//        }
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }


//        imgScrollPane.remove(tmpImg);
//        tmpImg = new JLabel(new ImageIcon(new FileUtil().openFromFile("img\\cpm-graph.png")));
//        tmpImg.setVisible(true);
//        imgScrollPane = new JScrollPane(tmpImg);
//        imgScrollPane.setPreferredSize(new Dimension(750, 700));
//        imgScrollPane.setVisible(true);
//        frame.add(imgScrollPane, BorderLayout.WEST);



//        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
//            message += "    Scroll type: WHEEL_UNIT_SCROLL" + newline;
//            message += "    Scroll amount: " + e.getScrollAmount()
//                    + " unit increments per notch" + newline;
//            message += "    Units to scroll: " + e.getUnitsToScroll()
//                    + " unit increments" + newline;
//            message += "    Vertical unit increment: "
//                    + imgScrollPane.getVerticalScrollBar().getUnitIncrement(1)
//                    + " pixels" + newline;
//        } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
//            message += "    Scroll type: WHEEL_BLOCK_SCROLL" + newline;
//            message += "    Vertical block increment: "
//                    + imgScrollPane.getVerticalScrollBar().getBlockIncrement(1)
//                    + " pixels" + newline;
//        }
    }

//
//    @Override
//    public void keyTyped(KeyEvent e) {
//        System.out.println("keyPressed");
//    }
//
//    @Override
//    public void keyPressed(KeyEvent e) {
//        System.out.println("keyPressed");
//    }
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//        System.out.println("keyPressed");
//    }
}