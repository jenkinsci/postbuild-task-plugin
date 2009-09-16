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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
		this((TaskProperties[]) tasks.toArray(new TaskProperties[tasks.size()]));
	}

	/**
	 * This method will return the taskProperties foe the specified logText
	 * 
	 * @return TaskProperties[]
	 */
	// TODO need to finish later
	public TaskProperties[] getAllTasks() {
		return tasks;
	}

	/**
	 * This method will return all the tasks
	 * 
	 * @return List<TaskProperties>
	 */
	public List<TaskProperties> getTasks() {
		if (tasks == null)
			return new ArrayList<TaskProperties>();
		else
			return Collections.unmodifiableList(Arrays.asList(tasks));
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		String buildLog = build.getLog();
		listener.getLogger().println("Performing Post build task...");
		try {
			for (int i = 0; i < tasks.length; i++) {
				TaskProperties taskProperties = tasks[i];
				String script = taskProperties.script;
				if (checkLogTextMatch(taskProperties.getLogProperties(),
						buildLog, listener)) {
					listener.getLogger().println(
							"Logical operation result is TRUE");
					/*script = getGroupedScript(taskProperties
							.getLogProperties(), taskProperties.script, buildLog);*/
					listener.getLogger().println("Running script  : " + script);
					CommandInterpreter runner = getCommandInterpreter(launcher,
							script);
					Result result = runner.perform(build, launcher, listener) ? Result.SUCCESS
							: Result.FAILURE;
					listener.getLogger().println(
							"POST BUILD TASK : " + result.toString());
					listener.getLogger().println(
							"END OF POST BUILD TASK : " + i);
				} else {
					listener.getLogger().println(
							"Logical operation result is FALSE");
					listener.getLogger()
							.println("Skipping script  : " + script);
					listener.getLogger().println(
							"END OF POST BUILD TASK 	: " + i);
				}
			}
		} catch (Exception e) {
			listener.getLogger().println(
					"Exception when executing the batch command : "
							+ e.getMessage());
			return false;
		}
		return true;
	}

	private String getGroupedScript(LogProperties[] logTexts, String script,
			String buildLog) {
		StringBuilder appendedLogs = new StringBuilder();
		for (int i = 0; i < logTexts.length; i++) {
			LogProperties logInfo = logTexts[i];
			Pattern pattern = Pattern.compile(logInfo.getLogText());
			Matcher matcher = pattern.matcher(buildLog);
			for (int k = 0; k < matcher.groupCount(); k++) {
				script = script.replace("%" + k, matcher.group(k));
			}
			
		}
		
		return script;
	}

	private boolean checkLogTextMatch(LogProperties[] logTexts,
			String buildLog, BuildListener listener) {
		boolean logmatch = false;

		boolean match1 = false;
		boolean match2 = false;
		String operator1 = "";

		for (int i = 0; i < logTexts.length; i++) {
			LogProperties logInfo = logTexts[i];
			String logText = logInfo.getLogText();
			String operator = logInfo.getOperator();
			match1 = isMatching(buildLog, logText, listener);
			if (i != 0) {
				match1 = doOperation(logmatch, match1, operator1);
			} else {
				logmatch = match1;
			}

			i++;
			if (i < logTexts.length) {
				LogProperties logInfo1 = logTexts[i];
				String logText1 = logInfo1.getLogText();
				operator1 = logInfo1.getOperator();
				match2 = isMatching(buildLog, logText1, listener);
				logmatch = doOperation(match1, match2, operator);
			} else {
				logmatch = match1;
			}

		}

		return logmatch;

	}

	private boolean doOperation(boolean match1, boolean match2, String operation) {
		if (operation.equals("AND"))
			return (match1 & match2);
		else
			return (match1 | match2);
	}

	private boolean isMatching(String buildLog, String logText,
			BuildListener listener) {
		Pattern pattern = Pattern.compile(logText);
		Matcher matcher = pattern.matcher(buildLog);
		boolean match = matcher.find();
		if (match) {
			listener.getLogger().println(
					"Match found for :" + logText + " : True");
		} else {
			listener.getLogger().println(
					"Could not match :" + logText + "  : False");
		}
		return match;
	}

	/**
	 * This method will return the command intercepter as per the node OS
	 * 
	 * @param launcher
	 * @param script
	 * @return CommandInterpreter
	 */
	private CommandInterpreter getCommandInterpreter(Launcher launcher,
			String script) {
		if (launcher.isUnix())
			return new Shell(script);
		else
			return new BatchFile(script);
	}

	/**
	 * This method will return the descriptorobject.
	 * 
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

		public PostbuildTask newInstance(StaplerRequest req)
				throws FormException {
			// if(req.getParameter("postbuild-task.")!=null)
			List<LogProperties> logprops = req.bindParametersToList(
					LogProperties.class, "postbuild-task.logProperties.");
			List<TaskProperties> tasksprops = req.bindParametersToList(
					TaskProperties.class, "postbuild-task.taskpropertes.");
			for (Iterator iterator = tasksprops.iterator(); iterator.hasNext();) {
				TaskProperties taskProperties = (TaskProperties) iterator
						.next();
				List<LogProperties> logPropsList = new ArrayList<LogProperties>();
				for (Iterator iterator2 = logprops.iterator(); iterator2
						.hasNext();) {
					LogProperties logProperties = (LogProperties) iterator2
							.next();
					if (!logProperties.getLogText().equals("@$#endofblock")) {
						logPropsList.add(logProperties);
					} else {
						logprops.remove(logProperties);
						logprops.removeAll(logPropsList);
						break;
					}
				}
				taskProperties.setLogTexts((LogProperties[]) logPropsList
						.toArray(new LogProperties[logPropsList.size()]));
			}
			return new PostbuildTask(tasksprops);
		}
	}
}
