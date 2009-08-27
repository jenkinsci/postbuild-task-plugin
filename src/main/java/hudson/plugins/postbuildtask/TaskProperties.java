package hudson.plugins.postbuildtask;

import hudson.util.Iterators;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;



/**
 * A task properties.
 * 
 * @author Shinod Mohandas
 */
public final class TaskProperties {
    /**
     * The text string which shoud be searched in the build log.
     */
    public final String logText;
    /**
     * Shell script to be executed.
     */
    public final String script;

   
    @DataBoundConstructor
    public TaskProperties(String logText, String script) {
        this.logText = logText;
        this.script = script;
    }

    public String getLogText() {
        return logText;
    }

    

                  
}
