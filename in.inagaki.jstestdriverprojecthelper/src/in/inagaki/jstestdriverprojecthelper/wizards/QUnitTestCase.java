package in.inagaki.jstestdriverprojecthelper.wizards;

import in.inagaki.jstestdriverprojecthelper.Activator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;

/**
 * This creates QUnit test case template file to the specified directory.
 * 
 * @author hiroki
 * 
 */
public class QUnitTestCase extends BasicNewFileResourceWizard implements
		INewWizard {
	private static final String QUNIT_TEMPLATE_LOCATION = "dist/qUnitTemplate.js";
	private static final String DIR_SEPARATOR = "/";
	private QUnitTestCasePage page;
	private IStructuredSelection selection;

	public QUnitTestCase() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		page = new QUnitTestCasePage(selection);
		addPage(page);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
		final IFile file = page.createNewFile();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(file, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} catch (IOException e) {
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

	protected void doFinish(IFile file, IProgressMonitor monitor)
			throws CoreException, IOException {
		monitor.beginTask("Creating file...", 1);
		URL pluginURL = FileLocator.toFileURL(Activator.getDefault()
				.getBundle().getEntry(DIR_SEPARATOR));
		File distFile = new File(pluginURL.getFile(), QUNIT_TEMPLATE_LOCATION);
		BufferedInputStream outputlibDirInputStream;
		outputlibDirInputStream = new BufferedInputStream(new FileInputStream(
				distFile));
		file.setContents(outputlibDirInputStream, true, false, monitor);

		monitor.worked(1);
	}

}
