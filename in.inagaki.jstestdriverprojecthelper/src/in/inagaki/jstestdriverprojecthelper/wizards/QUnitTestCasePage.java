package in.inagaki.jstestdriverprojecthelper.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * This wizard creates QUnit test case template source.
 * 
 * @author hiroki
 *
 */
public class QUnitTestCasePage extends WizardNewFileCreationPage {

	public QUnitTestCasePage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle("QUnit Test Case");
		setDescription("This wizard creates QUnit test case template source.");
		setFileName("test");
		setFileExtension("js");
	}

	public QUnitTestCasePage(IStructuredSelection selection) {
		this("wizardPage", selection);
	}

}
