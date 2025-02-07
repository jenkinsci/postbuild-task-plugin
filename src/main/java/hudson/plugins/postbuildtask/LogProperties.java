/*
 * The MIT License
 * 
 * Copyright (c) 2009, Ushus Technologies LTD.,Shinod K Mohandas
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.postbuildtask;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author shinod.mohandas
 * 
 */
public final class LogProperties {

	public String logText;

	public String operator;

	@DataBoundConstructor
	@SuppressFBWarnings(value = "PA_PUBLIC_PRIMITIVE_ATTRIBUTE", justification = "Backwards compatibility")
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
