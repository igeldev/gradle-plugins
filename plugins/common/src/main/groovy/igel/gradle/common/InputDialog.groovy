package igel.gradle.common

import javax.swing.*
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class InputDialog extends JDialog {

    private final Object lock = new Object()

    private JPanel contentPane

    InputDialog(Input input) {
        this.title = input.title

        this.contentPane = new JPanel()
        contentPane.setBackground(Color.LIGHT_GRAY)


        setContentPane(contentPane)

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
