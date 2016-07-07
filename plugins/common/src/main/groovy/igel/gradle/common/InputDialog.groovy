package igel.gradle.common

import javax.swing.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

class InputDialog extends JDialog {

    private final Object lock = new Object()

    InputDialog(Input input) {
        this.title = input.title

        addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent windowEvent) {
                hideUI()
            }

            @Override
            void windowClosed(WindowEvent windowEvent) {
                synchronized (lock) {
                    lock.notifyAll()
                }
            }
        })
    }

    void showUI() {
        // set dialog size
        Dimension preferredSize = this.preferredSize
        preferredSize.width = Math.max(300, preferredSize.width);
        preferredSize.height = Math.max(400, preferredSize.height);
        this.size = preferredSize

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
