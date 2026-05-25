package bulletinBoardService;

import java.lang.reflect.*;
import javax.swing.SwingUtilities;

public class EDTInvocationHandler implements InvocationHandler {
    private Object invocationResult = null;
    private UITasks ui;

    public EDTInvocationHandler(UITasks ui) {
        this.ui = ui;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        if (SwingUtilities.isEventDispatchThread()) {
            invocationResult = method.invoke(ui, args);
        } else {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    invocationResult = method.invoke(ui, args);
                } catch (Exception ex) { ex.printStackTrace(); }
            });
        }
        return invocationResult;
    }
}