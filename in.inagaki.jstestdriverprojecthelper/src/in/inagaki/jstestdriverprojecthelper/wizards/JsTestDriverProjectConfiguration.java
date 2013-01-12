package in.inagaki.jstestdriverprojecthelper.wizards;

import in.inagaki.jstestdriverprojecthelper.Activator;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

/**
 * This creates libraries and a configuration file for JsTestDriver and stores
 * JsTestDriver settings.
 */
public class JsTestDriverProjectConfiguration extends Wizard implements
		INewWizard {
	private static final String PARENT_DIR = "..";
	private static final int FIRST_SEGMENT = 0;
	private static final String BLANK = "";
	private static final String PLUGIN_JS_TEST_LIB_FOLDER = "jstestlib";
	private static final String PLUGIN_DIST_FOLDER = "dist";
	private static final String JS_TEST_DRIVER_VM = "jsTestDriver.vm";
	private static final String DIR_SEPARATOR = "/";
	private static final String JS_TEST_DRIVER_CONF = "jsTestDriver.conf";
	private JsTestDriverProjectConfigurationPage page;
	private ISelection selection;
	private final IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
			.getRoot();

	/**
	 * Constructor for JsTestDriverProjectConfiguration.
	 */
	public JsTestDriverProjectConfiguration() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		page = new JsTestDriverProjectConfigurationPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		final String sourceFolder = page.getSourceFolder().getText();
		final String testSourceFolder = page.getTestSourceFolder().getText();
		final String testLibraryFolder = page.getTestLibraryFolder().getText();
		final String port = page.getPort().getText();
		final String pathToIE = page.getPathToIE().getText();
		final String pathToFireFox = page.getPathToFireFox().getText();
		final String pathToChrome = page.getPathToChrome().getText();
		final String pathToOpera = page.getPathToOpera().getText();
		final String pathToSafari = page.getPathToSafari().getText();
		final boolean isJsConfCreatable;
		IProject testProject = root.getProject(new Path(testSourceFolder)
				.segment(FIRST_SEGMENT));
		IFile jsConfFile = testProject.getFile(JS_TEST_DRIVER_CONF);
		if (jsConfFile.exists()) {
			isJsConfCreatable = MessageDialog.openQuestion(getShell(),
					"Confirm overwrite", JS_TEST_DRIVER_CONF
							+ " already exsists. Do you want to overwrite it?");
		} else {
			isJsConfCreatable = true;
		}
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(sourceFolder, testSourceFolder, testLibraryFolder,
							port, pathToIE, pathToFireFox, pathToChrome,
							pathToOpera, pathToSafari, isJsConfCreatable,
							monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} catch (IOException e) {
					throw new InvocationTargetException(e);
				} catch (BackingStoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}

		return true;
	}

	private void doFinish(String sourceFolderName, String testSourceFolderName,
			String testLibraryFolderName, String port, String pathToIE,
			String pathToFireFox, String pathToChrome, String pathToOpera,
			String pathToSafari, boolean isJsConfCreatable,
			IProgressMonitor monitor) throws CoreException, IOException,
			BackingStoreException {
		monitor.beginTask("Processing", 7);

		// store JsTestRunner setting
		monitor.subTask("Storing JsTestRunner settings...");
		IPreferenceStore preferenceStore = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, JsTestDriverConfigConstants.PLUGIN_ID);
		preferenceStore.setValue(
				JsTestDriverConfigConstants.PREFERRED_SERVER_PORT, port);
		preferenceStore.setValue(JsTestDriverConfigConstants.IE_PATH, pathToIE);
		preferenceStore.setValue(JsTestDriverConfigConstants.FIREFOX_PATH,
				pathToFireFox);
		preferenceStore.setValue(JsTestDriverConfigConstants.CHROME_PATH,
				pathToChrome);
		preferenceStore.setValue(JsTestDriverConfigConstants.OPERA_PATH,
				pathToOpera);
		preferenceStore.setValue(JsTestDriverConfigConstants.SAFARI_PATH,
				pathToSafari);

		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof IResource) {
					IProject project;
					if (obj instanceof IProject)
						project = (IProject) obj;
					else
						project = ((IResource) obj).getParent().getProject();
					IScopeContext projectScope = new ProjectScope(project);
					IEclipsePreferences projectNode = projectScope
							.getNode(Activator.getDefault().getBundle()
									.getSymbolicName());
					if (projectNode != null) {
						projectNode.put(Constants.SOURCE_FOLDER_ID,
								sourceFolderName);
						projectNode.put(Constants.TEST_SOURCE_FOLDER_ID,
								testSourceFolderName);
						projectNode.put(Constants.TEST_LIBRARY_FOLDER_ID,
								testLibraryFolderName);
						projectNode.flush();
					}
				}
			}
		}
		monitor.worked(1);

		// create source folder
		monitor.subTask("Creating source folder...");
		IResource sourceFolder = root.findMember(new Path(sourceFolderName));
		if (sourceFolder == null) {
			sourceFolder = root.getFolder(new Path(sourceFolderName));
			((IFolder) sourceFolder).create(true, true, monitor);
		}
		monitor.worked(1);

		// create test source folder
		monitor.subTask("Creating test source folder...");
		IResource testSourceFolder = root.findMember(new Path(
				testSourceFolderName));
		if (testSourceFolder == null) {
			testSourceFolder = root.getFolder(new Path(testSourceFolderName));
			((IFolder) testSourceFolder).create(true, true, monitor);
		}
		monitor.worked(1);

		// create test library folder
		monitor.subTask("Creating test source folder...");
		IResource testLibraryFolder = root.findMember(new Path(
				testLibraryFolderName));
		if (testLibraryFolder == null) {
			testLibraryFolder = root.getFolder(new Path(testLibraryFolderName));
			((IFolder) testLibraryFolder).create(true, true, monitor);
		}
		monitor.worked(1);

		// copy test libraries
		monitor.subTask("Copying test libraries...");

		URL pluginURL = FileLocator.toFileURL(Activator.getDefault()
				.getBundle().getEntry(DIR_SEPARATOR));
		File distDir = new File(pluginURL.getFile(), PLUGIN_DIST_FOLDER);
		copyFilesFromJar(new File(distDir, PLUGIN_JS_TEST_LIB_FOLDER), null,
				(IContainer) testLibraryFolder, monitor);
		monitor.worked(1);

		// create JsTestDriver configuration file from VM templates
		if (isJsConfCreatable) {
			monitor.subTask("Creating config files...");
			List<String> sourceFolderList = new ArrayList<String>();
			if (sourceFolder instanceof IProject) {
				sourceFolderList.add(BLANK);
			} else {
				sourceFolderList.addAll(getFolders((IFolder) sourceFolder));
			}
			List<String> testSourceFolderList = new ArrayList<String>();
			if (testSourceFolderList instanceof IProject) {
				testSourceFolderList.add(BLANK);
			} else {
				testSourceFolderList
						.addAll(getFolders((IFolder) testSourceFolder));
			}
			String testLibPrjReleticePath = testLibraryFolder instanceof IProject ? BLANK
					: testLibraryFolder.getProjectRelativePath().toString();
			VelocityContext context = new VelocityContext();
			context.put("port", port);
			if (!sourceFolder.getProject().getName()
					.equals(testSourceFolder.getProject().getName())) {
				List<String> newSourceFolderList = new ArrayList<String>();
				for (String folderName : sourceFolderList) {
					newSourceFolderList.add(PARENT_DIR + DIR_SEPARATOR
							+ sourceFolder.getProject().getName()
							+ DIR_SEPARATOR + folderName);
				}
				sourceFolderList = newSourceFolderList;
			}
			context.put("sourcePathList", sourceFolderList);
			context.put("testSourcePathList", testSourceFolderList);
			context.put("testLibraryPath", testLibPrjReleticePath);
			createFileFromVMTemplate(testSourceFolder.getProject().getName()
					+ DIR_SEPARATOR + JS_TEST_DRIVER_CONF, new File(distDir,
					JS_TEST_DRIVER_VM), context, monitor);
		}
		monitor.worked(1);

		// show JsTestDriver page
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try {
					page.showView(JsTestDriverConfigConstants.JS_TEST_DRIVER_VIEW);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);

	}

	private List<String> getFolders(IFolder sourceFolder) {
		List<String> folders = new ArrayList<String>();
		folders.add(sourceFolder.getProjectRelativePath().toString());
		IResource[] childMembers;
		try {
			childMembers = sourceFolder.members();
			for (IResource childMember : childMembers) {
				if (childMember instanceof IFolder) {
					folders.addAll(getFolders((IFolder) childMember));
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
			return folders;
		}
		return folders;
	}

	private void copyFilesFromJar(File baseDir, String targetFileName,
			IContainer destDirectory, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException {
		if (targetFileName == null || targetFileName.equals(BLANK)) {
			if (baseDir.isDirectory()) {
				String[] childList = baseDir.list();
				for (String childFileName : childList) {
					copyFilesFromJar(baseDir, DIR_SEPARATOR + childFileName,
							destDirectory, monitor);
				}
			}
			return;
		}
		File file = new File(baseDir.getPath(), targetFileName);
		if (file.isDirectory()) {
			if (targetFileName != null) {
				IFolder outputFolder = destDirectory.getFolder(new Path(
						targetFileName));
				if (!outputFolder.exists()) {
					outputFolder.create(true, true, monitor);
				}
			}
			String[] childList = file.list();
			for (String childFileName : childList) {
				copyFilesFromJar(baseDir, targetFileName + DIR_SEPARATOR
						+ childFileName, destDirectory, monitor);
			}
		} else if (file.isFile()) {
			IFile outputFile = destDirectory.getFile(new Path(targetFileName));
			if (!outputFile.exists()) {
				BufferedInputStream outputlibDirInputStream;
				outputlibDirInputStream = new BufferedInputStream(
						new FileInputStream(file));
				outputFile.create(outputlibDirInputStream, true, monitor);
			}
		}
	}

	private void createFileFromVMTemplate(String outputFilePath, File distFile,
			VelocityContext context, IProgressMonitor monitor)
			throws CoreException, FileNotFoundException {
		IFile outputFile = root.getFile(new Path(outputFilePath));
		Velocity.init();
		FileReader templateFile = new FileReader(distFile);
		StringWriter writer = new StringWriter();
		Velocity.evaluate(context, writer, outputFilePath, templateFile);
		if (outputFile.exists()) {
			outputFile.setContents(new ByteArrayInputStream(writer.toString()
					.getBytes()), true, false, monitor);
		} else {
			outputFile.create(new ByteArrayInputStream(writer.toString()
					.getBytes()), true, monitor);
		}

	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}