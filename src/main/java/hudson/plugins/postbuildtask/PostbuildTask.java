package hudson.plugins.postbuildtask;

import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Result;
import hudson.util.EditDistance;
import org.kohsuke.stapler.StaplerRequest;
import hudson.tasks.BatchFile;
import hudson.tasks.CommandInterpreter;
import hudson.tasks.Shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;


import hudson.tasks.Publisher;

/**
 * Post build tasks added as {@link Publisher}.
 *
 *
 * @author Shinod Mohandas
 */
public class PostbuildTask extends Publisher {

    private volatile TaskProperties[] tasks;

    public PostbuildTask(TaskProperties... tasks) {
        this.tasks = tasks;
    }

    public PostbuildTask(Collection<TaskProperties> tasks) {
        this((TaskProperties[])tasks.toArray(new TaskProperties[tasks.size()]));
    }
    /**
     * This method will return the taskProperties foe the specified logText
     * @param logText
     * @return
     */
    public TaskProperties getTask(String logText) {
        for (TaskProperties t : tasks)
            if(t.logText.equals(logText))
                return t;
        return null;
    }
    /**
     * This method will return all the tasks
     * @return
     */
    public List<TaskProperties> getTasks() {
        return Collections.unmodifiableList(Arrays.asList(tasks));
    }

        
    @Override
	public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    	String buildLog = build.getLog();
    	try {
    		for (int i = 0; i < tasks.length; i++) {
        		TaskProperties taskProperties = tasks[i];
        		if(buildLog.indexOf(taskProperties.logText) != -1 ){
            		listener.getLogger().println("Performing Post build task...");
            		listener.getLogger().println("Found Matching text in the log : "+taskProperties.logText);
            		listener.getLogger().println("Executing the batch command : "+taskProperties.script);
            		CommandInterpreter runner = getCommandInterpreter(launcher,taskProperties.script);
            		Result result = runner.perform(build,launcher,listener) ? Result.SUCCESS : Result.FAILURE;
            		listener.getLogger().println("POST BUILD TASK : "+result.toString());
            		
            	}
    			
    		}
			
		} catch (Exception e) {
			listener.getLogger().println("Exception when executing the batch command : "+e.getMessage());
			return false;
		}
    	return true;
	}
    
    /**
     * This method will return the command intercepter as per the node OS
     * @param launcher
     * @param script
     * @return CommandInterpreter
     */
    private CommandInterpreter getCommandInterpreter(Launcher launcher,String script){
    	if (launcher.isUnix())
            return new Shell(script);
        else
            return new BatchFile(script);
    }
    /**
     * This method will return the descriptorobject.
     * @return DESCRIPTOR
     */
    public DescriptorImpl getDescriptor() {
        return DESCRIPTOR;
    }

    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<Publisher> {
        public DescriptorImpl() {
            super(PostbuildTask.class);
            load();
        }

        public boolean isApplicable(Class<? extends Job> jobType) {
            return AbstractProject.class.isAssignableFrom(jobType);
        }
        @Override
        public String getDisplayName() {
        	return "Post build task";
        }
        @Override
		public String getHelpFile() {
			return "/plugin/postbuild-task/help/main.html";
		}
        

        public PostbuildTask newInstance(StaplerRequest req) throws FormException {
           // if(req.getParameter("postbuild-task.")!=null)
                return new PostbuildTask(req.bindParametersToList(TaskProperties.class, "postbuild-task."));
           // else
           //     return null;
        }
    }
}
