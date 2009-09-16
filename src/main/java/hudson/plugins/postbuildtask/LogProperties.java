/**
 * 
 */
package hudson.plugins.postbuildtask;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * @author shinod.mohandas
 * 
 */
public final class LogProperties {

	public String logText;

	public String operator;

	@DataBoundConstructor
	public LogProperties(String logText, String operator) {
		this.logText = logText;
		this.operator = operator;
	}

	private void setLogText(String logText) {
		this.logText = logText;
	}

	public String getLogText() {
		return logText;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

}
