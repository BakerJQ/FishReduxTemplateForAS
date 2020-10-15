package generator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JRadioButton mutable, immutable;
    private ButtonGroup templateGroup, sourceAdapterGroup;
    private JCheckBox actionBox, effectBox, reducerBox, stateBox, viewBox;
    private JCheckBox prefixBox;

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
        jFrame.setMinimumSize(new Dimension(500, 400));
        Container container = jFrame.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        JPanel template = new JPanel();
        template.setLayout(new GridLayout(3, 3));
        template.setBorder(BorderFactory.createTitledBorder("Select Template"));
        JRadioButton page = new JRadioButton("Page", true);
        page.setActionCommand("Page");
        page.addActionListener(radioActionListener);
        JRadioButton component = new JRadioButton("Component");
        component.setActionCommand("Component");
        component.addActionListener(radioActionListener);
        JRadioButton sourceFlowAdapter = new JRadioButton("SourceFlowAdapter");
        sourceFlowAdapter.setActionCommand("SourceFlowAdapter");
        sourceFlowAdapter.addActionListener(radioActionListener);
        mutable = new JRadioButton("mutable");
        mutable.setActionCommand("mutable");
        mutable.setEnabled(false);
        mutable.setSelected(true);
        immutable = new JRadioButton("immutable");
        immutable.setActionCommand("immutable");
        immutable.setEnabled(false);
//        JRadioButton dynamicFlowAdapter = new JRadioButton("DynamicFlowAdapter(deprecated)");
//        dynamicFlowAdapter.setActionCommand("DynamicFlowAdapter");
        JRadioButton staticFlowAdapter = new JRadioButton("StaticFlowAdapter");
        staticFlowAdapter.setActionCommand("StaticFlowAdapter");
        staticFlowAdapter.addActionListener(radioActionListener);
        JRadioButton customAdapter = new JRadioButton("CustomAdapter");
        customAdapter.setActionCommand("CustomAdapter");
        customAdapter.addActionListener(radioActionListener);
        template.add(page);
        template.add(component);
        template.add(new Label());
        template.add(sourceFlowAdapter);
        template.add(mutable);
        template.add(immutable);
//        template.add(dynamicFlowAdapter);
        template.add(staticFlowAdapter);
        template.add(customAdapter);
        templateGroup = new ButtonGroup();
        templateGroup.add(page);
        templateGroup.add(component);
        templateGroup.add(sourceFlowAdapter);
//        templateGroup.add(dynamicFlowAdapter);
        templateGroup.add(staticFlowAdapter);
        templateGroup.add(customAdapter);
        sourceAdapterGroup = new ButtonGroup();
        sourceAdapterGroup.add(mutable);
        sourceAdapterGroup.add(immutable);
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
        nameField.add(nameLabel);
        nameField.add(nameTextField);
        prefixBox = new JCheckBox("make name as filename prefix");
        nameField.add(prefixBox);
        container.add(nameField);

        JPanel menu = new JPanel();
        menu.setLayout(new FlowLayout());

        JButton cancel = new JButton("Cancel");
        cancel.setForeground(Color.RED);
        cancel.addActionListener(actionListener);

        JButton ok = new JButton("OK");
        ok.setForeground(Color.GREEN);
        ok.addActionListener(actionListener);
        menu.add(cancel);
        menu.add(ok);
        container.add(menu);

        jFrame.setSize(500, 300);
        jFrame.setLocationRelativeTo(null);

        jFrame.setVisible(true);
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Cancel")) {
                jFrame.dispose();
            } else {
                if (nameTextField.getText() == null || "".equals(nameTextField.getText().trim())) {
                    Messages.showInfoMessage(project, "Please enter the module name", "Info");
                    return;
                }
                jFrame.dispose();
                clickCreateFile();
                project.getProjectFile().refresh(false, true);
                Messages.showInfoMessage(project, "Enjoy yourself", "Info");
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
                case "SourceFlowAdapter":
                case "StaticFlowAdapter":
                case "CustomAdapter":
                    actionBox.setSelected(true);
                    effectBox.setSelected(false);
                    reducerBox.setSelected(true);
                    stateBox.setSelected(true);
                    viewBox.setSelected(false);
                    break;
            }
            if (e.getActionCommand().equals("SourceFlowAdapter")) {
                mutable.setEnabled(true);
                immutable.setEnabled(true);
            } else {
                mutable.setEnabled(false);
                immutable.setEnabled(false);
            }
        }
    };

    private void clickCreateFile() {
        switch (templateGroup.getSelection().getActionCommand()) {
            case "Page":
                generatePage();
                break;
            case "Component":
                generateComponent();
                break;
            case "SourceFlowAdapter":
                generateSourceFlowAdapter();
                break;
//            case "DynamicFlowAdapter":
//                generateDynamicAdapter();
//                break;
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

    private void generateSourceFlowAdapter() {
        generateFile("adapter/source/adapter.dart", psiPath, "adapter.dart");
        if (stateBox.isSelected()) {
            if (sourceAdapterGroup.getSelection().getActionCommand().equals("mutable")) {
                generateFile("adapter/source/state_mutable.dart", psiPath, "state.dart");
            } else {
                generateFile("adapter/source/state_immutable.dart", psiPath, "state.dart");
            }
        }
        generateCommonFiles(false);
    }

    private void generateStaticAdapter() {
        generateFile("adapter/static/adapter.dart", psiPath, "adapter.dart");
        generateCommonFiles();
    }

    private void generateCustomAdapter() {
        generateFile("adapter/custom/adapter.dart", psiPath, "adapter.dart");
        generateCommonFiles();
    }

    private void generateCommonFiles(boolean withState) {
        if (actionBox.isSelected())
            generateFile("action.dart", psiPath, "action.dart");
        if (effectBox.isSelected())
            generateFile("effect.dart", psiPath, "effect.dart");
        if (reducerBox.isSelected())
            generateFile("reducer.dart", psiPath, "reducer.dart");
        if (withState && stateBox.isSelected())
            generateFile("state.dart", psiPath, "state.dart");
        if (viewBox.isSelected())
            generateFile("view.dart", psiPath, "view.dart");
    }

    private void generateCommonFiles() {
        generateCommonFiles(true);
    }

    private void generateFile(String srcFile, String filePath, String fileName) {
        String content = readFile(srcFile);
        String prefix = "";
        if (prefixBox.isSelected()) {
            prefix = nameTextField.getText().replaceAll("[A-Z]", "_$0").toLowerCase();
            if(prefix.startsWith("_")){
                prefix = prefix.substring(1);
            }
            prefix = prefix + "_";
        }
        content = dealFile(content, prefix);
        writetoFile(content, filePath, prefix + fileName);
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

    private String dealFile(String content, String prefix) {
        content = content.replaceAll("\\$name", nameTextField.getText()).replaceAll("\\$prefix_", prefix);
        if (!actionBox.isSelected()) {
            content = content.replaceAll("import\\s'"+prefix+"action.dart';\\s+", "");
        }
        if (!effectBox.isSelected()) {
            content = content.replaceAll("effect:\\s+buildEffect\\(\\),\\s+", "");
            content = content.replaceAll("import\\s'"+prefix+"effect.dart';\\s+", "");
        }
        if (!reducerBox.isSelected()) {
            content = content.replaceAll("reducer:\\s+buildReducer\\(\\),\\s+", "");
            content = content.replaceAll("import\\s+'"+prefix+"reducer.dart';\\s+", "");
        }
        if (!stateBox.isSelected()) {
            content = content.replaceAll("import\\s+'"+prefix+"state.dart';\\s+", "");
        }
        if (!viewBox.isSelected()) {
            content = content.replaceAll("import\\s+'"+prefix+"view.dart';\\s+", "");
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












