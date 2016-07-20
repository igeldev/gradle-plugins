/*
 * Copyright 2016 Pavel Stepanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package igel.gradle.common

import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.text.JTextComponent
import java.awt.*
import java.awt.event.*

class InputDialog extends JDialog {

    private static JPanel createSectionUI(Input.Section section) {
        JPanel sectionPane = new JPanel()

        sectionPane.border = new TitledBorder(section.name)
        sectionPane.toolTipText = section.description

        sectionPane.layout = new GridBagLayout()

        section.sections.each { Input.Section nestedSection ->
            sectionPane.add(createSectionUI(nestedSection), new GridBagConstraints(
                    gridwidth: GridBagConstraints.REMAINDER,
                    fill: GridBagConstraints.HORIZONTAL, weightx: 1))
        }

        section.properties.each { Input.Property nestedProperty ->
            // property label

            JLabel labelComponent = new JLabel()
            labelComponent.text = nestedProperty.name
            labelComponent.toolTipText = nestedProperty.description

            sectionPane.add(labelComponent, new GridBagConstraints(
                    gridwidth: 1, weightx: 0,
                    fill: GridBagConstraints.HORIZONTAL,
                    insets: new Insets(4, 8, 4, 8)))

            // property field

            JTextComponent fieldComponent
            JComponent fieldContainer
            switch (nestedProperty.type) {
                case Input.Type.SINGLE_LINE:
                    JTextField singleLineComponent = new JTextField()
                    fieldComponent = fieldContainer = singleLineComponent
                    break
                case Input.Type.PASSWORD:
                    JPasswordField passwordComponent = new JPasswordField()
                    fieldComponent = fieldContainer = passwordComponent
                    break
                case Input.Type.MULTILINE:
                    JTextArea multilineComponent = new JTextArea(5, 10)
                    fieldComponent = multilineComponent
                    fieldContainer = new JScrollPane(multilineComponent)
                    break
                default:
                    throw new IllegalArgumentException("Unknown property type: $nestedProperty.type")
            }
            fieldComponent.text = nestedProperty.value ?: nestedProperty.defaultValue
            fieldComponent.toolTipText = nestedProperty.description

            sectionPane.add(fieldContainer, new GridBagConstraints(
                    gridwidth: GridBagConstraints.REMAINDER, weightx: 1,
                    fill: GridBagConstraints.HORIZONTAL,
                    insets: new Insets(4, 8, 4, 8)))
        }

        return sectionPane
    }

    private final Object lock = new Object()

    private JPanel contentPane
    private JButton buttonOK
    private JButton buttonInterrupt

    private void setup(Input input) {
        this.contentPane = new JPanel()
        contentPane.layout = new GridBagLayout()

        // input pane

        JScrollPane scrollPane = new JScrollPane(createSectionUI(input.rootSection))

        contentPane.add(scrollPane, new GridBagConstraints(
                gridwidth: GridBagConstraints.REMAINDER,
                fill: GridBagConstraints.BOTH,
                insets: new Insets(4, 4, 4, 4),
                weightx: 1.0, weighty: 1.0))

        // button space

        JLabel buttonSpace = new JLabel()

        contentPane.add(buttonSpace, new GridBagConstraints(
                gridwidth: 1, weightx: 1.0))

        // button ok

        this.buttonOK = new JButton()
        buttonOK.text = 'OK'

        contentPane.add(buttonOK, new GridBagConstraints(
                gridwidth: 1, insets: new Insets(4, 8, 4, 8)))

        // button interrupt

        this.buttonInterrupt = new JButton()
        buttonInterrupt.text = 'Interrupt'

        contentPane.add(buttonInterrupt, new GridBagConstraints(
                gridwidth: 1, insets: new Insets(4, 8, 4, 8)))
    }

    InputDialog(Input input) {
        this.title = input.title

        setup(input)

        setContentPane(contentPane)
        getRootPane().setDefaultButton(buttonOK)

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // todo save input values here
                hideUI()
            }
        })

        buttonInterrupt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // todo fail build
                hideUI()
            }
        })

        setDefaultCloseOperation(DISPOSE_ON_CLOSE)
        addWindowListener(new WindowAdapter() {
            @Override
            void windowClosed(WindowEvent windowEvent) {
                synchronized (lock) {
                    lock.notifyAll()
                }
            }
        })

        contentPane.registerKeyboardAction(
                { dispose() },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW)
    }

    void showUI() {
        // set dialog size
        Dimension preferredSize = this.preferredSize
        preferredSize.width = Math.max(300, preferredSize.width)
        preferredSize.height = Math.max(400, preferredSize.height)
        this.size = preferredSize
        this.minimumSize = new Dimension(300, 200)

        // show dialog
        this.alwaysOnTop = true
        this.locationRelativeTo = null
        this.visible = true
    }

    void hideUI() {
        dispose()
    }

    void joinUI() throws InterruptedException {
        synchronized (lock) {
            while (isVisible()) {
                lock.wait()
            }
        }
    }

}
