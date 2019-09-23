package sample;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Controller
{
    @FXML private TableView table;
    @FXML private TableColumn<Instruction, Boolean> breakPoint;
    @FXML private TableColumn<Instruction, String> insLabel;
    @FXML private TableColumn<Instruction, String> insOpCode;
    @FXML private TableColumn<Instruction, String> insAddress;

    @FXML private TableView varTable;
    @FXML private TableColumn<Memory, String> varName;
    @FXML private TableColumn<Memory, String> varValue;
    @FXML private TableColumn<Memory, String> varAddress;

    @FXML private Label aReg;
    @FXML private Label tReg;
    @FXML private Label pcReg;
    @FXML private Label c;
    @FXML private Label z;

    @FXML private Tooltip aTooltip;
    @FXML private Tooltip tTooltip;

    private int pc = 0;
    private int a,t;

    Stage stage;

    public void initialize()
    {
        breakPoint.setResizable(false);
        insLabel.setResizable(false);
        insOpCode.setResizable(false);
        insAddress.setResizable(false);

        breakPoint.setCellValueFactory(param ->
        {
            Instruction instruction = param.getValue();

            BooleanProperty booleanProperty = new SimpleBooleanProperty(instruction.isFlag());
            booleanProperty.addListener((observable, oldValue, newValue) -> instruction.setFlag(newValue));

            return booleanProperty;
        });

        breakPoint.setCellFactory(param -> new CheckBoxTableCell<>());

        insLabel.setCellValueFactory(new PropertyValueFactory<>("label"));
        insLabel.setCellFactory(TextFieldTableCell.forTableColumn());

        insOpCode.setCellValueFactory(new PropertyValueFactory<>("opCode"));
        insOpCode.setCellFactory(TextFieldTableCell.forTableColumn());

        insAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        insAddress.setCellFactory(TextFieldTableCell.forTableColumn());


        varName.setCellValueFactory(new PropertyValueFactory<>("name"));
        varName.setCellFactory(TextFieldTableCell.forTableColumn());

        varValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        varValue.setCellFactory(TextFieldTableCell.forTableColumn());
        varValue.setOnEditCommit((TableColumn.CellEditEvent<Memory, String> event) ->
        {
            TablePosition<Memory, String> pos = event.getTablePosition();
            String newValue = event.getNewValue();
            Memory memory = Main.memoryArrayList.get(pos.getRow());
            memory.setValue(newValue);
        });

        varAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        varAddress.setCellFactory(TextFieldTableCell.forTableColumn());

    }

    @FXML
    private void load()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TEXT", "*.txt"));
        File file = fileChooser.showOpenDialog(stage);

        new Thread(() ->
        {
            ObservableList<Instruction> list = FXCollections.observableArrayList();
            ObservableList<Memory> list2 = FXCollections.observableArrayList();

            try
            {
                FileInputStream fileInputStream = new FileInputStream(file);
                Scanner scanner = new Scanner(fileInputStream);

                while (scanner.hasNext())
                {
                    String ins = scanner.nextLine();
                    detect(ins);
                }

                list.addAll(Main.instructionArrayList);
                list2.addAll(Main.memoryArrayList);
            }
            catch (IOException e)
            {

            }
            finally
            {
                Platform.runLater(() ->
                {
                    table.setItems(list);
                    varTable.setItems(list2);

                });
            }
        }).start();


    }

    private void detect(String ins)
    {
        String opCode = ins.split(" ")[0];
        String address = null;
        String label;

        if (!opCode.contains("TAT") && !opCode.contains("ROR"))
        {
            address = ins.split(" ")[1];

            if (!opCode.contains(".ORG") && !opCode.contains(".DATA"))
            {
                Instruction instruction = new Instruction("", opCode, address, false);
                Main.instructionArrayList.add(instruction);
            }
        }

        if (opCode.contains("TAT") || opCode.contains("ROR"))
        {
            Instruction instruction = new Instruction("", opCode, "", false);
            Main.instructionArrayList.add(instruction);
        }

        if (opCode.contains(":"))
        {
            label = opCode.split(":")[0];
            Main.labelsMap.put(label, Main.currentInsAddress);
            Main.labels.add(label);
            Main.currentInsAddress++;

            opCode = ins.split(" ")[1];

            if (!opCode.contains("TAT") && !opCode.contains("ROR"))
                address = ins.split(" ")[2];

            Instruction instruction = new Instruction(label, opCode, address, false);
            Main.instructionArrayList.add(instruction);
        }

        if (opCode.contains(".ORG"))
        {
            Main.currentMemAddress = Integer.parseInt(ins.split(" ")[1]);
        }

        if (opCode.contains(".DATA"))
        {
            String var = ins.split(" ")[1];
            try
            {
                String value = ins.split(" ")[2];
                Memory memory = new Memory(var, value, String.valueOf(Main.currentMemAddress));
                Main.memoryArrayList.add(memory);
                Main.currentMemAddress++;
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                Memory memory = new Memory(var, "0", String.valueOf(Main.currentMemAddress));
                Main.memoryArrayList.add(memory);
                Main.currentMemAddress++;
            }
        }
    }

    @FXML
    private void start()
    {
        while (true)
        {
            try
            {
                Instruction ins = Main.instructionArrayList.get(pc);
                if (!ins.isFlag())
                {
                    table.requestFocus();
                    table.getSelectionModel().select(pc);
                    table.getFocusModel().focus(pc);
                    runIns(ins);
                    aReg.setText(String.valueOf(a));
                    tReg.setText(String.valueOf(t));
                    pcReg.setText(String.valueOf(pc));
                    c.setText(String.valueOf(Main.C));
                    z.setText(String.valueOf(Main.Z));
                    varTable.refresh();
                }
                else
                {
                    table.requestFocus();
                    table.getSelectionModel().select(pc);
                    table.getFocusModel().focus(pc);
                    runIns(ins);
                    aReg.setText(String.valueOf(a));
                    tReg.setText(String.valueOf(t));
                    pcReg.setText(String.valueOf(pc));
                    c.setText(String.valueOf(Main.C));
                    z.setText(String.valueOf(Main.Z));
                    varTable.refresh();
                    break;
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                System.out.println("End");
                break;
            }
            table.refresh();
        }
    }

    @FXML
    private void next()
    {
        try
        {
            Instruction ins = Main.instructionArrayList.get(pc);
            table.requestFocus();
            table.getSelectionModel().select(pc);
            table.getFocusModel().focus(pc);            runIns(ins);
            aReg.setText(String.valueOf(a));
            tReg.setText(String.valueOf(t));
            pcReg.setText(String.valueOf(pc));
            c.setText(String.valueOf(Main.C));
            z.setText(String.valueOf(Main.Z));
            varTable.refresh();
        }
        catch (IndexOutOfBoundsException e)
        {
            System.out.println("End");
        }
    }

    private void runIns(Instruction instruction)
    {
        switch (instruction.getOpCode())
        {
            case "JMP":
                pc = Main.labelsMap.get(instruction.getLabel());
                break;

            case "ADC":
                a = a + findValue(instruction.getAddress());
                if (a == 0)
                    Main.Z = 1;
                else
                    Main.Z = 0;
                break;

            case "XOR":
                a = a ^ findValue(instruction.getAddress());
                if (a == 0)
                    Main.Z = 1;
                else
                    Main.Z = 0;
                break;

            case "SBC":
                a = a - findValue(instruction.getAddress());
                if (a == 0)
                    Main.Z = 1;
                else
                    Main.Z = 0;
                break;

            case "ROR":
                a = rotateA();
                break;

            case "TAT":
                t = a;
                break;

            case "OR":
                a = a | findValue(instruction.getAddress());
                if (a == 0)
                    Main.Z = 1;
                else
                    Main.Z = 0;
                break;

            case "AND":
                a = a & findValue(instruction.getAddress());
                if (a == 0)
                    Main.Z = 1;
                else
                    Main.Z = 0;
                break;

            case "LDC":
                a = findValue(instruction.getAddress());
                Main.C = 0;
                break;

            case "BCC":
                if (Main.C == 1)
                {
                    pc = findValue(instruction.getAddress());
                }
                else pc++;
                break;

            case "BNE":
                if (Main.Z == 0)
                {
                    pc = findValue(instruction.getAddress());
                }
                else pc++;
                break;

            case "LDI":
                a = findValue(String.valueOf(a));
                break;

            case "STT":
                storeValue(String.valueOf(a), String.valueOf(t));
                break;

            case "LDA":
                a = findValue(instruction.getAddress());
                break;

            case "STA":
                storeValue(instruction.getAddress(), String.valueOf(a));
                break;
        }

        if (!instruction.getOpCode().equals("JMP") && !instruction.getOpCode().equals("BNE") && !instruction.getOpCode().equals("BCC"))
            pc++;
    }

    private int findValue(String name)
    {
        for (Memory memory : Main.memoryArrayList)
        {
            if (memory.getName().equals(name))
                return Integer.parseInt(memory.getValue());
        }

        return 0;
    }

    private void storeValue(String name, String value)
    {
        for (Memory memory : Main.memoryArrayList)
        {
            if (memory.getName().equals(name))
                memory.setValue(value);
        }

    }

    @FXML private void aTool()
    {
        aTooltip.setText(tonBit(a));
    }

    @FXML private void tTool()
    {
        tTooltip.setText(tonBit(t));
    }

    private int rotateA()
    {
        String A = tonBit(a);
        System.out.println(A);
        StringBuilder builder = new StringBuilder();

        builder.append(Main.C);


        for (int i = 0; i < 15; i++)
        {
            builder.append(A.charAt(i));
        }

        Main.C = Integer.parseInt(String.valueOf(A.charAt(15)));
        System.out.println(builder.toString()+ "  g "+Main.C);


        return Integer.parseInt(builder.toString(), 2);
    }

    private static String tonBit(int integer)
    {
        String temp = Integer.toBinaryString(integer);
        StringBuilder tempBuilder = new StringBuilder();

        for (int i = 0; i < 16 - temp.length(); i++)
        {
            tempBuilder.append("0");
        }
        tempBuilder.append(temp);

        return tempBuilder.toString();
    }
}
