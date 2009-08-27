package hudson.plugins.postbuildtask;

import hudson.Plugin;
import hudson.tasks.BuildStep;
import hudson.plugins.postbuildtask.PostbuildTask;

/**
 * Entry point of a plugin.
 *
 * <p>
 * There must be one {@link Plugin} class in each plugin.
 * See javadoc of {@link Plugin} for more about what can be done on this class.
 *
 * @author Shinod Mohandas
 */
public class PostbuildTaskPluginImpl extends Plugin {
    
	
	public void start() throws Exception {
       BuildStep.PUBLISHERS.add(PostbuildTask.DESCRIPTOR);
    }
}
