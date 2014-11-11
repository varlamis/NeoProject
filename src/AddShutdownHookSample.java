


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author herc
 */
public class AddShutdownHookSample {

    public void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Inside Add Shutdown Hook");
                for (SourceThread st : NeoProject.threads) {
                    st.flush();
                }
            }
        });
        System.out.println("Shut Down Hook Attached.");
    }
}