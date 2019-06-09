package generator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;

import org.jetbrains.annotations.NotNull;

import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class GenerateFishReduxTemplateAction extends AnAction {
    private Project project;
    private String psiPath;
    private JDialog jFrame;
    private JTextField nameTextField;
    private ButtonGroup templateGroup;
    private JCheckBox actionBox, effectBox, reducerBox, stateBox, viewBox;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        psiPath = e.getData(PlatformDataKeys.PSI_ELEMENT).toString();
        psiPath = psiPath.substring(psiPath.indexOf(":") + 1);
        initView();
    }

    private void initView() {
        jFrame = new JDialog();
        jFrame.setModal(true);
        Container container = jFrame.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        JPanel template = new JPanel();
        template.setLayout(new GridLayout(2, 3));
        template.setBorder(BorderFactory.createTitledBorder("Select Template"));
        JRadioButton page = new JRadioButton("Page", true);
        page.setActionCommand("Page");
        page.addActionListener(radioActionListener);
        JRadioButton component = new JRadioButton("Component");
        component.setActionCommand("Component");
        JRadioButton dynamicFlowAdapter = new JRadioButton("DynamicFlowAdapter");
        dynamicFlowAdapter.setActionCommand("DynamicFlowAdapter");
        JRadioButton staticFlowAdapter = new JRadioButton("StaticFlowAdapter");
        staticFlowAdapter.setActionCommand("StaticFlowAdapter");
        JRadioButton customAdapter = new JRadioButton("CustomAdapter");
        customAdapter.setActionCommand("CustomAdapter");
        template.add(page);
        template.add(component);
        template.add(new Label());
        template.add(dynamicFlowAdapter);
        template.add(staticFlowAdapter);
        template.add(customAdapter);
        templateGroup = new ButtonGroup();
        templateGroup.add(page);
        templateGroup.add(component);
        templateGroup.add(dynamicFlowAdapter);
        templateGroup.add(staticFlowAdapter);
        templateGroup.add(customAdapter);
        container.add(template);

        JPanel file = new JPanel();
        file.setLayout(new GridLayout(2, 3));
        file.setBorder(BorderFactory.createTitledBorder("Select Files"));
        actionBox = new JCheckBox("action", true);
        effectBox = new JCheckBox("effect", true);
        reducerBox = new JCheckBox("reducer", true);
        stateBox = new JCheckBox("state", true);
        stateBox.addActionListener(e -> {
            if (!stateBox.isSelected()) {
                effectBox.setSelected(false);
                effectBox.setEnabled(false);
                reducerBox.setSelected(false);
                reducerBox.setEnabled(false);
            } else {
                effectBox.setEnabled(true);
                reducerBox.setEnabled(true);
            }
        });
        viewBox = new JCheckBox("view", true);
        file.add(actionBox);
        file.add(effectBox);
        file.add(reducerBox);
        file.add(stateBox);
        file.add(viewBox);
        container.add(file);

        JPanel nameField = new JPanel();
        nameField.setLayout(new FlowLayout());
        nameField.setBorder(BorderFactory.createTitledBorder("Naming"));
        JLabel nameLabel = new JLabel("Module Name：");
        nameTextField = new JTextField(30);
        nameTextField.addKeyListener(keyListener);
        nameField.add(nameLabel);
        nameField.add(nameTextField);
        container.add(nameField);

        JPanel menu = new JPanel();
        menu.setLayout(new FlowLayout());

        JButton cancel = new JButton("Cancel");
        cancel.setForeground(JBColor.RED);
        cancel.addActionListener(actionListener);

        JButton ok = new JButton("OK");
        ok.setForeground(JBColor.GREEN);
        ok.addActionListener(actionListener);
        menu.add(cancel);
        menu.add(ok);
        container.add(menu);

        jFrame.setSize(500, 300);
        jFrame.setLocationRelativeTo(null);

        jFrame.setVisible(true);
    }
    private KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                save();
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };


    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Cancel")) {
                dispose();
            } else {
                save();
            }
        }
    };

    private ActionListener radioActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "Page":
                    actionBox.setSelected(true);
                    effectBox.setSelected(true);
                    reducerBox.setSelected(true);
                    stateBox.setSelected(true);
                    viewBox.setSelected(true);
                    break;
                case "Component":
                    actionBox.setSelected(true);
                    effectBox.setSelected(false);
                    reducerBox.setSelected(true);
                    stateBox.setSelected(true);
                    viewBox.setSelected(true);
                    break;
                case "Adapter":
                    actionBox.setSelected(true);
                    effectBox.setSelected(false);
                    reducerBox.setSelected(true);
                    stateBox.setSelected(false);
                    viewBox.setSelected(false);
                    break;
            }
        }
    };

    private void dispose() {
        jFrame.dispose();
    }
    private void save() {
        if (nameTextField.getText() == null || "".equals(nameTextField.getText().trim())) {
            Messages.showInfoMessage(project, "Please enter the module name", "Info");
            return;
        }
        dispose();
        clickCreateFile();
        project.getProjectFile().refresh(false, true);
        Messages.showInfoMessage(project, "Enjoy yourself", "Info");
    }

    private void clickCreateFile() {
        switch (templateGroup.getSelection().getActionCommand()) {
            case "Page":
                generatePage();
                break;
            case "Component":
                generateComponent();
                break;
            case "DynamicFlowAdapter":
                generateDynamicAdapter();
                break;
            case "StaticFlowAdapter":
                generateStaticAdapter();
                break;
            case "CustomAdapter":
                generateCustomAdapter();
                break;
        }
    }

    private void generatePage() {
        generateFile("page/page.dart", psiPath, "page.dart");
        generateCommonFiles();
    }

    private void generateComponent() {
        generateFile("component/component.dart", psiPath, "component.dart");
        generateCommonFiles();
    }

    private void generateDynamicAdapter() {
        generateFile("adapter/dynamic/adapter.dart", psiPath, "adapter.dart");
        generateCommonFiles();
    }

    private void generateStaticAdapter() {
        generateFile("adapter/static/adapter.dart", psiPath, "adapter.dart");
        generateCommonFiles();
    }

    private void generateCustomAdapter() {
        generateFile("adapter/custom/adapter.dart", psiPath, "adapter.dart");
        generateCommonFiles();
    }

    private void generateCommonFiles() {
        if (actionBox.isSelected())
            generateFile("action.dart", psiPath, "action.dart");
        if (effectBox.isSelected())
            generateFile("effect.dart", psiPath, "effect.dart");
        if (reducerBox.isSelected())
            generateFile("reducer.dart", psiPath, "reducer.dart");
        if (stateBox.isSelected())
            generateFile("state.dart", psiPath, "state.dart");
        if (viewBox.isSelected())
            generateFile("view.dart", psiPath, "view.dart");
    }

    private void generateFile(String srcFile, String filePath, String fileName) {
        String content = readFile(srcFile);
        content = dealFile(content);
        writetoFile(content, filePath, fileName);
    }

    private String readFile(String filename) {
        InputStream in = null;
        in = this.getClass().getResourceAsStream("/templates/" + filename);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (Exception e) {
        }
        return content;
    }

    private byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }

    private String dealFile(String content) {
        content = content.replaceAll("\\$name", nameTextField.getText());
        if (!actionBox.isSelected()) {
            content = content.replaceAll("import\\s'action.dart';\\s+", "");
        }
        if (!effectBox.isSelected()) {
            content = content.replaceAll("effect:\\s+buildEffect\\(\\),\\s+", "");
            content = content.replaceAll("import\\s'effect.dart';\\s+", "");
        }
        if (!reducerBox.isSelected()) {
            content = content.replaceAll("reducer:\\s+buildReducer\\(\\),\\s+", "");
            content = content.replaceAll("import\\s+'reducer.dart';\\s+", "");
        }
        if (!stateBox.isSelected()) {
            content = content.replaceAll("import\\s+'state.dart';\\s+", "");
        }
        if (!viewBox.isSelected()) {
            content = content.replaceAll("import\\s+'view.dart';\\s+", "");
        }
        return content;
    }

    private void writetoFile(String content, String filepath, String filename) {
        try {
            File floder = new File(filepath);
            // if file doesnt exists, then create it
            if (!floder.exists()) {
                floder.mkdirs();
            }
            File file = new File(filepath + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
