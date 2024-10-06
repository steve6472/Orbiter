package steve6472.orbiter.debug;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by steve6472
 * Date: 10/6/2024
 * Project: Orbiter <br>
 */
class CommandInputListener extends KeyAdapter
{
    private final Runnable runnable;
    private final Runnable repaint;

    CommandInputListener(Runnable runnable, Runnable repaint)
    {
        this.runnable = runnable;
        this.repaint = repaint;
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            runnable.run();
        }

        repaint.run();
    }
}
