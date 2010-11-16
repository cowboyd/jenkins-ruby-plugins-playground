package thefrontside.ci;

import hudson.model.*;
import hudson.Launcher;
import hudson.FilePath;
import hudson.Proc;
import hudson.remoting.Channel;
import hudson.remoting.VirtualChannel;
import hudson.remoting.Callable;
import hudson.slaves.NodeDescriptor;
import hudson.slaves.RetentionStrategy;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.DescribableList;
import hudson.util.ClockDifference;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.LogRecord;
import java.util.concurrent.*;
import java.nio.charset.Charset;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;

public class Fake {

	public static class FakeComputer extends Computer {

		public FakeComputer(Node node) {
			super(node);
		}

		public VirtualChannel getChannel() {
			System.out.println("Fake$FakeComputer.getChannel");
			return new FakeVirtualChannel();
		}

		public Charset getDefaultCharset() {
			return Charset.defaultCharset();
		}

		public List<LogRecord> getLogRecords() throws IOException, InterruptedException {
			System.out.println("Fake$FakeComputer.getLogRecords");
			return new ArrayList<LogRecord>();
		}

		public void doLaunchSlaveAgent(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
			System.out.println("Fake$FakeComputer.doLaunchSlaveAgent");
		}

		protected Future<?> _connect(boolean forceReconnect) {
			System.out.println("Fake$FakeComputer._connect");
			return new TheFutureIsNow(new Callable<Object,RuntimeException>() {
				public Object call() throws RuntimeException {
					System.out.println("Fake$FakeComputer.call");
					return null;
				}
			});
		}

		public boolean isConnecting() {
			System.out.println("Fake$FakeComputer.isConnecting");
			return false;
		}

		public RetentionStrategy getRetentionStrategy() {
			return new RetentionStrategy.Demand(1,1);
		}
	}

	public static class TheFutureIsNow<V, T extends Throwable> implements hudson.remoting.Future<V> {
		private Throwable error;
		private V value;


		public TheFutureIsNow(Callable<V, T> callable) {
			try {
				this.value = callable.call();
			} catch (Throwable t) {
				this.error = t;
			}
		}

		public TheFutureIsNow(final V returnValue) {
			this(new Callable<V, T>() {
				public V call() throws T {
					return returnValue;
				}
			});
		}

		public boolean cancel(boolean mayInterruptIfRunning) {
			System.out.println("Fake$TheFutureIsNow.cancel(" + mayInterruptIfRunning + ")");
			return false;
		}

		public boolean isCancelled() {
			System.out.println("Fake$TheFutureIsNow.isCancelled");
			return false;
		}

		public boolean isDone() {
			System.out.println("Fake$TheFutureIsNow.isDone");
			return true;
		}

		public V get() throws InterruptedException, ExecutionException {
			if (this.error != null) {
				throw new ExecutionException(this.error);
			} else {
				return this.value;
			}
		}

		public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			return get();
		}
	}

	public static class FakeVirtualChannel implements VirtualChannel {
		public <V, T extends Throwable> V call(Callable<V, T> callable) throws IOException, T, InterruptedException {
			System.out.println("call: " + callable);
			return callable.call();
		}

		public <V, T extends Throwable> hudson.remoting.Future<V> callAsync(final Callable<V, T> callable) throws IOException {
			System.out.println("callAsync: " + callable);
			return new TheFutureIsNow(callable);
		}

		public void close() throws IOException {
			System.out.println("VirtualChannel#close");
		}

		public void join() throws InterruptedException {
			System.out.println("Fake$FakeVirtualChannel.join");
		}

		public void join(long timeout) throws InterruptedException {
			System.out.println("Fake$FakeVirtualChannel.join(" + timeout + ")");
		}

		public <T> T export(Class<T> type, T instance) {
			System.out.println("Fake$FakeVirtualChannel.export(");
			return instance;
		}
	}


	public static class FakeLauncher extends Launcher {
		public FakeLauncher(TaskListener listener, VirtualChannel channel) {
			super(listener, channel);
		}


		public Proc launch(ProcStarter starter) throws IOException {
			return new FakeProc(starter);
		}

		public Channel launchChannel(String[] cmd, OutputStream out, FilePath workDir, Map<String, String> envVars) throws IOException, InterruptedException {
			System.out.println("Fake$FakeLauncher.launchChannel");
			return null;
		}

		public void kill(Map<String, String> modelEnvVars) throws IOException, InterruptedException {
			System.out.println("Fake$FakeLauncher.kill");
		}
	}

	public static class FakeProc extends Proc {
		private Launcher.ProcStarter starter;
		private boolean alive;

		public FakeProc(Launcher.ProcStarter starter) {
			this.starter = starter;
			this.alive = true;
		}

		public boolean isAlive() throws IOException, InterruptedException {
			return this.alive;
		}

		public void kill() throws IOException, InterruptedException {
			this.alive = false;
		}

		public int join() throws IOException, InterruptedException {
			return 0;
		}
	}

	public static class FakeNode extends Node {
		private String description;
		private String nodeName;

		public FakeNode(String nodeName, String description) {
			this.nodeName = nodeName;
			this.description = description;
		}

		public String getNodeName() {
			return this.nodeName;
		}

		public void setNodeName(String name) {
			this.nodeName = name;
		}

		public String getNodeDescription() {
			return this.getNodeDescription();
		}

		public Launcher createLauncher(TaskListener listener) {
			return new FakeLauncher(listener, null);
		}

		public int getNumExecutors() {
			return 1;
		}

		public Mode getMode() {
			return Mode.NORMAL;
		}

		protected Computer createComputer() {
			System.out.println("Fake$FakeNode.createComputer");
			return new FakeComputer(this);
		}

		public String getLabelString() {
			return "Fake Node Label String";
		}

		public FilePath getWorkspaceFor(TopLevelItem item) {
			return new FilePath((VirtualChannel)null, "/tmp/fakehudson/stuff");
		}

		public FilePath getRootPath() {
			return new FilePath((VirtualChannel)null, "/tmp/fakehudson");
		}

		public DescribableList<NodeProperty<?>, NodePropertyDescriptor> getNodeProperties() {
			System.out.println("Fake$FakeNode.getNodeProperties");
			return new DescribableList<NodeProperty<?>, NodePropertyDescriptor>(new Saveable() {
				public void save() throws IOException {
					System.out.println("Fake$FakeNode.save");
					//do nothing
				}
			});
		}

		public NodeDescriptor getDescriptor() {
			return new NodeDescriptor() {
				@Override
				public String getDisplayName() {
					return "Fake Node Descriptor";
				}
			};
		}

		public ClockDifference getClockDifference() throws IOException, InterruptedException {
			return ClockDifference.ZERO;
		}
	}
}
